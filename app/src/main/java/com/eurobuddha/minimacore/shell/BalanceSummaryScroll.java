package com.eurobuddha.minimacore.shell;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;
/** Keep token navigation reachable when the balance summary expands at large font sizes. */
public final class BalanceSummaryScroll extends ScrollView {
 public BalanceSummaryScroll(Context context,AttributeSet attrs){super(context,attrs);}
 @Override protected void onMeasure(int width,int height){
  int available=MeasureSpec.getSize(height);
  if(MeasureSpec.getMode(height)!=MeasureSpec.UNSPECIFIED && available>0)
   height=MeasureSpec.makeMeasureSpec(available*3/5,MeasureSpec.AT_MOST);
  super.onMeasure(width,height);
 }
}
