package com.eurobuddha.minimacore.shell;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
/** Retains legacy screen state without exposing the old tab ordering through swipe navigation. */
public final class ShellPager extends androidx.viewpager.widget.ViewPager {
    public ShellPager(Context context, AttributeSet attributes){super(context,attributes);}
    @Override public boolean onInterceptTouchEvent(MotionEvent event){return false;}
    @Override public boolean onTouchEvent(MotionEvent event){return false;}
}
