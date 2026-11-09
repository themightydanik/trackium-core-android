package com.eurobuddha.minimacore.shell;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import com.eurobuddha.minimacore.R;
import com.eurobuddha.minimacore.main.MainActivity;
import com.eurobuddha.minimacore.main.MainAdapter;
import com.eurobuddha.minimacore.utils.MinimaCMD;
import com.eurobuddha.minimacore.utils.CoalescingRefresh;
import tech.trackium.designsystem.ThemePreference;

/** Activity-bound shell. The existing service and every privileged operation keep their owners. */
public final class ShellController {
    private final MainActivity activity;
    private final FrameLayout stage;
    private final LinearLayout navContainer;
    private final View pager;
    private final Toolbar toolbar;
    private final ShellState state=new ShellState();
    private final Handler main=new Handler(Looper.getMainLooper());
    private volatile boolean visible, destroyed;
    private ScrollView scroll;
    private View page;
    private TextView node,network,block,observed,gateway;
    private AlertDialog dialog;
    private final CoalescingRefresh<NodeObservation> refresh;
    private final Runnable tick=new Runnable(){@Override public void run(){
        if(canObserve()){refresh.request();main.postDelayed(this,15_000);}
    }};

    public ShellController(MainActivity activity,Bundle saved) {
        this.activity=activity;state.restore(saved);
        stage=activity.findViewById(R.id.shell_stage);pager=activity.findViewById(R.id.main_pager);
        toolbar=activity.findViewById(R.id.toolbar);navContainer=activity.findViewById(R.id.shell_nav_container);
        refresh=new CoalescingRefresh<>(MinimaCMD.readExecutor(),main::post,this::read,this::renderObservation,
            error->renderObservation(new NodeObservation(false,false,null,null,0,-1)));
        activity.findViewById(R.id.tabs).setVisibility(View.GONE);
        activity.findViewById(R.id.main_footer).setVisibility(View.GONE);
        activity.getOnBackPressedDispatcher().addCallback(activity,new androidx.activity.OnBackPressedCallback(true){
            @Override public void handleOnBackPressed(){
                View focus=activity.getCurrentFocus();
                androidx.core.view.WindowInsetsCompat insets=androidx.core.view.ViewCompat.getRootWindowInsets(stage);
                if(insets!=null&&insets.isVisible(androidx.core.view.WindowInsetsCompat.Type.ime())){
                    if(focus!=null)focus.clearFocus();
                    androidx.core.view.WindowInsetsControllerCompat controller=androidx.core.view.ViewCompat.getWindowInsetsController(stage);
                    if(controller!=null)controller.hide(androidx.core.view.WindowInsetsCompat.Type.ime());return;
                }
                if(state.back()){show();return;}
                setEnabled(false);activity.getOnBackPressedDispatcher().onBackPressed();setEnabled(true);
            }
        });
        show();
    }
    public void configurationChanged(){if(scroll!=null)state.scroll=scroll.getScrollY();if(dialog!=null)dialog.dismiss();show();}
    public void accept(Intent intent){
        if(intent.hasExtra(RootNavigation.DESTINATION)){
            int target=intent.getIntExtra(RootNavigation.DESTINATION,0);
            if(target==0||target==2||target==3)select(target);
            intent.removeExtra(RootNavigation.DESTINATION);
        }
    }
    public void select(int target){
        if(target==1){activity.startActivity(new Intent(activity,tech.trackium.cargo.MainActivity.class));return;}
        if(target!=0&&target!=2&&target!=3)return;
        if(scroll!=null)state.scroll=0;
        state.root=target;state.tool=-1;show();
    }
    public boolean legacyVisible(){return state.root==2||state.tool>=0;}
    public void visible(boolean value){
        visible=value;main.removeCallbacks(tick);
        if(value&&canObserve())main.post(tick);else refresh.invalidate();
    }
    private boolean canObserve(){return visible&&!destroyed&&state.root==0
        &&(!com.eurobuddha.minimacore.lock.AppLock.isEnabled(activity)||com.eurobuddha.minimacore.lock.AppLock.get().isUnlocked());}
    public void save(Bundle out){if(scroll!=null)state.scroll=scroll.getScrollY();state.save(out);}
    public void destroy(){destroyed=true;refresh.close();main.removeCallbacksAndMessages(null);if(dialog!=null)dialog.dismiss();}
    private void openTool(int position){state.root=3;state.tool=position==MainAdapter.HOME_POSITION?0:position==MainAdapter.APPS_POSITION?1:position==MainAdapter.UTILITY_POSITION?2:3;state.scroll=0;show();}
    private void launch(Class<?> type){activity.startActivity(new Intent(activity,type));}
    private void show(){
        refresh.invalidate();main.removeCallbacks(tick);
        if(page!=null){stage.removeView(page);page=null;}scroll=null;
        navContainer.removeAllViews();navContainer.addView(RootNavigation.create(activity,state.root,this::select),new LinearLayout.LayoutParams(-1,-2));
        toolbar.setTitle(state.root==0?"Trackium Core":activity.getString(state.root==2?R.string.shell_wallet:R.string.shell_more));
        toolbar.setSubtitle(null);toolbar.setLogo(R.drawable.shell_brand);toolbar.setTitleMarginStart(ShellViews.dp(activity,18));
        toolbar.post(()->{ for(int i=0;i<toolbar.getChildCount();i++) if(toolbar.getChildAt(i) instanceof TextView){ TextView title=(TextView)toolbar.getChildAt(i);title.setSingleLine(false);title.setMaxLines(3); } });
        pager.setVisibility(legacyVisible()?View.VISIBLE:View.GONE);
        if(legacyVisible()){
            int[] positions={MainAdapter.HOME_POSITION,MainAdapter.APPS_POSITION,MainAdapter.UTILITY_POSITION,MainAdapter.CHAT_POSITION};
            int[] labels={R.string.shell_node_details,R.string.shell_apps,R.string.shell_history,R.string.shell_chat};
            if(state.tool>=0)toolbar.setTitle(labels[state.tool]);
            if(state.tool==3 && MainAdapter.CHAT_POSITION<0){state.tool=-1;show();return;}
            activity.showShellTool(state.root==2?MainAdapter.WALLET_POSITION:positions[state.tool]);
            if(state.tool>=0) toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            else toolbar.setNavigationIcon((android.graphics.drawable.Drawable)null);
            toolbar.setNavigationContentDescription(R.string.shell_more);
            toolbar.setNavigationOnClickListener(v->{state.tool=-1;state.root=3;show();});
        }else{
            toolbar.setNavigationIcon(null);
            scroll=new ScrollView(activity);scroll.setFillViewport(true);scroll.setId(R.id.shell_scroll);
            LinearLayout body=ShellViews.column(activity);int p=ShellViews.dp(activity,20);body.setPadding(p,p,p,p);
            scroll.addView(body);page=scroll;stage.addView(page,new FrameLayout.LayoutParams(-1,-1));
            if(state.root==0)overview(body);else more(body);
            final ScrollView target=scroll;final int y=state.scroll;target.post(()->target.scrollTo(0,y));
        }
        activity.shellVisibilityChanged();
        if(canObserve())main.post(tick);
    }
    private void overview(LinearLayout body){
        body.addView(ShellViews.text(activity,activity.getText(R.string.shell_app_status),false));
        LinearLayout card=ShellViews.card(body,R.string.shell_node);
        node=ShellViews.text(activity,activity.getText(R.string.shell_node_starting),true);card.addView(node);
        network=ShellViews.text(activity,activity.getText(R.string.shell_network_unknown),false);card.addView(network);
        block=ShellViews.text(activity,activity.getString(R.string.shell_block,"—"),false);card.addView(block);
        observed=ShellViews.text(activity,activity.getString(R.string.shell_observed,"—"),false);card.addView(observed);
        card.addView(ShellViews.text(activity,activity.getText(R.string.shell_sync_unknown),false));
        ShellViews.button(card,R.string.shell_refresh,()->refresh.request());
        LinearLayout workspace=ShellViews.card(body,R.string.shell_workspace);
        gateway=ShellViews.text(activity,activity.getText(R.string.shell_unpaired),false);workspace.addView(gateway);
        workspace.addView(ShellViews.text(activity,activity.getText(R.string.shell_no_data),false));
        ShellViews.button(workspace,R.string.shell_open_trackium,()->select(1));
        LinearLayout details=ShellViews.card(body,R.string.shell_tools);
        ShellViews.button(details,R.string.shell_node_details,()->openTool(MainAdapter.HOME_POSITION));
        ShellViews.button(details,R.string.shell_diagnostics,()->launch(com.eurobuddha.minimacore.main.views.logs.LogsActivity.class));
    }
    private NodeObservation read(){
        if(!canObserve())return new NodeObservation(false,false,null,null,0,-1);
        int stored=-1;
        // Only derive a non-secret state; never pass the credential/config object to the UI.
        try{if(canObserve())stored=new tech.trackium.cargo.SecureStore(activity.getApplicationContext()).config().has("session")?1:0;}catch(Exception ignored){}
        boolean running=MinimaCMD.checkMinimaStarted();
        return NodeObservation.parse(running,running?MinimaCMD.execute("status"):null,System.currentTimeMillis(),stored);
    }
    private void renderObservation(NodeObservation value){
        if(!canObserve()||node==null)return;
        node.setText(value.observed?R.string.shell_node_running:R.string.shell_node_unknown);
        network.setText(value.peers==null?activity.getString(R.string.shell_network_unknown):activity.getString(R.string.shell_network,value.peers));
        block.setText(activity.getString(R.string.shell_block,value.block==null?"—":value.block));
        observed.setText(activity.getString(R.string.shell_observed,value.at==0?"—":java.text.DateFormat.getDateTimeInstance().format(new java.util.Date(value.at))));
        gateway.setText(value.gateway==0?R.string.shell_unpaired:value.gateway==1?R.string.shell_session:R.string.shell_storage_unknown);
    }
    private void more(LinearLayout body){
        LinearLayout tools=ShellViews.card(body,R.string.shell_tools);
        ShellViews.button(tools,R.string.shell_node_details,()->openTool(MainAdapter.HOME_POSITION));
        ShellViews.button(tools,R.string.shell_apps,()->openTool(MainAdapter.APPS_POSITION));
        ShellViews.button(tools,R.string.shell_history,()->openTool(MainAdapter.UTILITY_POSITION));
        if(MainAdapter.CHAT_POSITION>=0)ShellViews.button(tools,R.string.shell_chat,()->openTool(MainAdapter.CHAT_POSITION));
        ShellViews.button(tools,R.string.shell_terminal,()->launch(com.eurobuddha.minimacore.main.views.terminal.TerminalActivity.class));
        ShellViews.button(tools,R.string.shell_diagnostics,()->launch(com.eurobuddha.minimacore.main.views.logs.LogsActivity.class));
        LinearLayout settings=ShellViews.card(body,R.string.shell_settings);
        ShellViews.button(settings,R.string.shell_theme,this::chooseTheme);
        ShellViews.button(settings,R.string.shell_startup_settings,()->launch(com.eurobuddha.minimacore.main.ParamsActivity.class));
        ShellViews.button(settings,R.string.shell_security,()->launch(com.eurobuddha.minimacore.lock.LockSettingsActivity.class));
        ShellViews.button(settings,R.string.shell_backup,()->launch(com.eurobuddha.minimacore.main.backup.BackupActivity.class));
        ShellViews.button(settings,R.string.shell_about,()->{dialog=new AlertDialog.Builder(activity).setTitle(R.string.shell_about).setMessage(R.string.shell_attribution).setPositiveButton(android.R.string.ok,null).show();});
        settings.addView(ShellViews.text(activity,activity.getText(R.string.shell_advanced_note),false));
    }
    public void chooseTheme(){
        String[] keys={"system","light","dark"};String value=ThemePreference.get(activity);int selected=java.util.Arrays.asList(keys).indexOf(value);
        CharSequence[] labels={activity.getText(R.string.shell_system),activity.getText(R.string.shell_light),activity.getText(R.string.shell_dark)};
        dialog=new AlertDialog.Builder(activity).setTitle(R.string.shell_theme).setSingleChoiceItems(labels,selected,(d,which)->{
            ThemePreference.set(activity,keys[which]);d.dismiss();
            AppCompatDelegate.setDefaultNightMode(which==1?AppCompatDelegate.MODE_NIGHT_NO:which==2?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }).setNegativeButton(android.R.string.cancel,null).show();
    }
}
