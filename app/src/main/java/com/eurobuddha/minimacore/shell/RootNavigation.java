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
        int[] icons={R.drawable.ic_shell_dashboard,selected==TRACKIUM?R.drawable.shell_logo_green:R.drawable.shell_logo_gray,R.drawable.ic_minima,R.drawable.ic_dapps};
        for(int i=0;i<4;i++) {
            final int target=i;
            LinearLayout item=ShellViews.column(activity); item.setGravity(Gravity.CENTER);
            item.setPadding(ShellViews.dp(activity,4),ShellViews.dp(activity,8),ShellViews.dp(activity,4),ShellViews.dp(activity,8));
            item.setMinimumHeight(ShellViews.dp(activity,64)); item.setFocusable(true); item.setClickable(true);
            android.graphics.drawable.StateListDrawable feedback=new android.graphics.drawable.StateListDrawable();
            feedback.addState(new int[]{android.R.attr.state_pressed},surface(activity,true,tech.trackium.designsystem.R.color.tc_ds1_accent));
            feedback.addState(new int[]{android.R.attr.state_focused},surface(activity,true,tech.trackium.designsystem.R.color.tc_ds1_focus));
            feedback.addState(new int[]{android.R.attr.state_selected},surface(activity,true,0));
            feedback.addState(new int[]{},surface(activity,false,0));item.setBackground(feedback);
            item.setSelected(i==selected); item.setContentDescription(activity.getText(labels[i]));
            androidx.core.view.ViewCompat.setStateDescription(item,i==selected?activity.getString(R.string.shell_selected):null);
            item.setAccessibilityDelegate(new View.AccessibilityDelegate(){
                @Override public void onInitializeAccessibilityNodeInfo(View host,android.view.accessibility.AccessibilityNodeInfo info){
                    super.onInitializeAccessibilityNodeInfo(host,info);info.setClassName("android.widget.Button");
                }
            });
            ImageView icon=new ImageView(activity);icon.setImageResource(icons[i]);icon.setScaleType(ImageView.ScaleType.FIT_CENTER);icon.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            if(i!=TRACKIUM)icon.setColorFilter(activity.getColor(i==selected?tech.trackium.designsystem.R.color.tc_ds1_accent:tech.trackium.designsystem.R.color.tc_ds1_text_secondary));
            item.addView(icon,new LinearLayout.LayoutParams(ShellViews.dp(activity,24),ShellViews.dp(activity,24)));
            TextView label=new TextView(activity);label.setText(labels[i]);label.setTextSize(12);label.setGravity(Gravity.CENTER);
            label.setTextColor(activity.getColor(i==selected?tech.trackium.designsystem.R.color.tc_ds1_accent:tech.trackium.designsystem.R.color.tc_ds1_text_secondary));
            label.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);item.addView(label,new LinearLayout.LayoutParams(-1,-2));
            nav.addView(item,new LinearLayout.LayoutParams(0,-1,1)); item.setOnClickListener(v->select.accept(target));
        }
        return nav;
    }
    private static android.graphics.drawable.GradientDrawable surface(Activity a,boolean raised,int outline) {
        android.graphics.drawable.GradientDrawable shape=new android.graphics.drawable.GradientDrawable();
        shape.setColor(a.getColor(raised?tech.trackium.designsystem.R.color.tc_ds1_surface_raised:tech.trackium.designsystem.R.color.tc_ds1_surface));
        shape.setCornerRadius(ShellViews.dp(a,12));
        if(outline!=0)shape.setStroke(ShellViews.dp(a,2),a.getColor(outline));return shape;
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
