package com.eurobuddha.minimacore.shell;

import android.content.Context;
import android.view.View;
import android.widget.*;
import com.eurobuddha.minimacore.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

/** Shared DS1 presentation primitives; all text wraps and scales with system fonts. */
public final class ShellViews {
    private ShellViews() {}
    public static int dp(Context c, int n) { return Math.round(c.getResources().getDisplayMetrics().density * n); }
    public static LinearLayout column(Context c) {
        LinearLayout v = new LinearLayout(c); v.setOrientation(LinearLayout.VERTICAL); return v;
    }
    public static TextView text(Context c, CharSequence value, boolean title) {
        TextView t = new TextView(c); t.setText(value);
        t.setTextAppearance(title ? tech.trackium.designsystem.R.style.TextAppearance_TrackiumDS1_Title : tech.trackium.designsystem.R.style.TextAppearance_TrackiumDS1_Body);
        t.setPadding(0, dp(c,4),0,dp(c,8));
        androidx.core.view.ViewCompat.setAccessibilityHeading(t, title);
        return t;
    }
    public static LinearLayout card(LinearLayout parent, int title) {
        Context c = parent.getContext();
        MaterialCardView card = new MaterialCardView(c);
        card.setRadius(dp(c,20)); card.setCardElevation(0);
        card.setCardBackgroundColor(c.getColor(tech.trackium.designsystem.R.color.tc_ds1_surface));
        card.setStrokeColor(c.getColor(tech.trackium.designsystem.R.color.tc_ds1_outline_subtle)); card.setStrokeWidth(dp(c,1));
        LinearLayout box = column(c); box.setPadding(dp(c,20),dp(c,16),dp(c,20),dp(c,16));
        box.addView(text(c,c.getText(title),true)); card.addView(box);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2); lp.bottomMargin=dp(c,16);
        parent.addView(card,lp); return box;
    }
    public static void button(LinearLayout parent, int title, Runnable action) {
        MaterialButton b = new MaterialButton(parent.getContext()); b.setText(title); b.setAllCaps(false);
        b.setMinHeight(dp(parent.getContext(),48)); b.setMaxLines(4);
        parent.addView(b,new LinearLayout.LayoutParams(-1,-2)); b.setOnClickListener(v -> action.run());
    }
}
