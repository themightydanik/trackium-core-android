package com.eurobuddha.minimacore.shell;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.eurobuddha.minimacore.R;
import com.eurobuddha.minimacore.main.MainActivity;
import java.util.function.IntConsumer;

/** Exactly one root navigation bar. Native Trackium supplies only nested navigation. */
public final class RootNavigation {
    public static final String DESTINATION = "trackium.shell.destination";
    public static final int OVERVIEW=0, TRACKIUM=1, WALLET=2, MORE=3;
    private RootNavigation() {}
    public static View create(Activity activity, int selected, IntConsumer select) {
        LinearLayout nav = new LinearLayout(activity); nav.setId(R.id.shell_navigation);
        nav.setBackgroundColor(activity.getColor(tech.trackium.designsystem.R.color.tc_ds1_surface));
        int[] labels={R.string.shell_overview,R.string.shell_trackium,R.string.shell_wallet,R.string.shell_more};
        int[] icons={R.drawable.ic_node,R.drawable.tc_ds1_brand_mark,R.drawable.ic_minima,R.drawable.ic_dapps};
        for(int i=0;i<4;i++) {
            final int target=i;
            LinearLayout item=ShellViews.column(activity); item.setGravity(Gravity.CENTER);
            item.setPadding(ShellViews.dp(activity,4),ShellViews.dp(activity,8),ShellViews.dp(activity,4),ShellViews.dp(activity,8));
            item.setMinimumHeight(ShellViews.dp(activity,64)); item.setFocusable(true); item.setClickable(true);
            item.setSelected(i==selected); item.setContentDescription(activity.getText(labels[i]));
            androidx.core.view.ViewCompat.setStateDescription(item,i==selected?activity.getString(R.string.shell_selected):null);
            item.setAccessibilityDelegate(new View.AccessibilityDelegate(){
                @Override public void onInitializeAccessibilityNodeInfo(View host,android.view.accessibility.AccessibilityNodeInfo info){
                    super.onInitializeAccessibilityNodeInfo(host,info);info.setClassName("android.widget.Button");
                }
            });
            ImageView icon=new ImageView(activity);icon.setImageResource(icons[i]);icon.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            if(i!=TRACKIUM)icon.setColorFilter(activity.getColor(i==selected?tech.trackium.designsystem.R.color.tc_ds1_accent:tech.trackium.designsystem.R.color.tc_ds1_text_secondary));
            item.addView(icon,new LinearLayout.LayoutParams(ShellViews.dp(activity,24),ShellViews.dp(activity,24)));
            TextView label=new TextView(activity);label.setText(labels[i]);label.setTextSize(12);label.setGravity(Gravity.CENTER);
            label.setTextColor(activity.getColor(i==selected?tech.trackium.designsystem.R.color.tc_ds1_accent:tech.trackium.designsystem.R.color.tc_ds1_text_secondary));
            label.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);item.addView(label,new LinearLayout.LayoutParams(-1,-2));
            nav.addView(item,new LinearLayout.LayoutParams(0,-1,1)); item.setOnClickListener(v->select.accept(target));
        }
        return nav;
    }
    public static View forTrackium(Activity activity) {
        return create(activity,TRACKIUM,target->{
            if(target==TRACKIUM)return;
            activity.startActivity(new Intent(activity,MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP).putExtra(DESTINATION,target));
            activity.finish();
        });
    }
}
