package com.langvest.skylire.utils;

import android.graphics.EmbossMaskFilter;
import android.view.View;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

public class EpicAdapter {

    public void applyFor(ShimmerTextView view) {
        EmbossMaskFilter filter = new EmbossMaskFilter(new float[] {5, 4, 1.5f}, 1f, 0, 1.5f);
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        view.getPaint().setMaskFilter(filter);

        Shimmer shimmer = new Shimmer();
        shimmer.setDuration(2000);
        shimmer.setStartDelay(1000);
        shimmer.start(view);
    }
}