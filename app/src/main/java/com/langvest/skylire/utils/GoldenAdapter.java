package com.langvest.skylire.utils;

import android.app.Activity;
import android.graphics.EmbossMaskFilter;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.view.View;
import androidx.core.content.res.ResourcesCompat;
import com.langvest.skylire.R;
import com.plattysoft.leonids.ParticleSystem;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import java.util.Random;

public class GoldenAdapter {
    private final Activity activity;
    private ParticleSystem ps1, ps2, ps3, ps4;
    private ShimmerTextView view;
    private CountDownTimer particlesUpdate1, particlesUpdate2, particlesUpdate3, particlesUpdate4;

    public GoldenAdapter(Activity activity) {
        this.activity = activity;
    }

    private int[] getRandomPosition() {
        int[] viewPosition = new int[2];
        int[] newPosition = new int[2];
        view.getLocationOnScreen(viewPosition);
        int minX = viewPosition[0];
        int maxX = minX + view.getWidth();
        newPosition[0] = new Random().nextInt(maxX-minX+1) + minX;
        int minY = viewPosition[1];
        int maxY = minY + view.getHeight();
        newPosition[1] = new Random().nextInt(maxY-minY+1) + minY;
        return newPosition;
    }

    private void show() {
        ps1 = new ParticleSystem(activity, 1, R.drawable.particle_golden_glare, 3000);
        ps1.setSpeedRange(0.001f, 0.002f);
        ps1.setScaleRange(1.4f, 2f);
        ps1.setFadeOut(2364);

        ps2 = new ParticleSystem(activity, 1, R.drawable.particle_golden_star, 3200);
        ps2.setScaleRange(0.5f, 0.8f);
        ps2.setRotationSpeedRange(50, 100);
        ps2.setFadeOut(1153);

        ps3 = new ParticleSystem(activity, 1, R.drawable.particle_golden_star, 2600);
        ps3.setScaleRange(0.5f, 1f);
        ps3.setRotationSpeedRange(25, 70);
        ps3.setFadeOut(1074);

        ps4 = new ParticleSystem(activity, 1, R.drawable.particle_golden_glare, 2800);
        ps4.setSpeedRange(0.001f, 0.003f);
        ps4.setScaleRange(1.2f, 2.2f);
        ps4.setFadeOut(1558);


        int[] ps1position = getRandomPosition();
        ps1.emit(ps1position[0], ps1position[1], 1);

        int[] ps2position = getRandomPosition();
        ps2.emit(ps2position[0], ps2position[1], 1);

        int[] ps3position = getRandomPosition();
        ps3.emit(ps3position[0], ps3position[1], 1);

        int[] ps4position = getRandomPosition();
        ps4.emit(ps4position[0], ps4position[1], 1);

        particlesUpdate1 = new CountDownTimer(3000, 2364) {
            @Override
            public void onTick(long millis) {}

            @Override
            public void onFinish() {
                int[] newPosition = getRandomPosition();
                ps1.updateEmitPoint(newPosition[0], newPosition[1]);
                particlesUpdate1.start();
            }
        };

        particlesUpdate2 = new CountDownTimer(3200, 1153) {
            @Override
            public void onTick(long millis) {}

            @Override
            public void onFinish() {
                int[] newPosition = getRandomPosition();
                ps2.updateEmitPoint(newPosition[0], newPosition[1]);
                particlesUpdate2.start();
            }
        };

        particlesUpdate3 = new CountDownTimer(2600, 1074) {
            @Override
            public void onTick(long millis) {}

            @Override
            public void onFinish() {
                int[] newPosition = getRandomPosition();
                ps3.updateEmitPoint(newPosition[0], newPosition[1]);
                particlesUpdate3.start();
            }
        };

        particlesUpdate4 = new CountDownTimer(2800, 1558) {
            @Override
            public void onTick(long millis) {}

            @Override
            public void onFinish() {
                int[] newPosition = getRandomPosition();
                ps4.updateEmitPoint(newPosition[0], newPosition[1]);
                particlesUpdate4.start();
            }
        };

        particlesUpdate1.start();
        particlesUpdate2.start();
        particlesUpdate3.start();
        particlesUpdate4.start();
    }

    public void start(ShimmerTextView view, boolean useParticles) {
        this.view = view;

        EmbossMaskFilter filter = new EmbossMaskFilter(new float[] {5, 4, 1.5f}, 1f, 0, 1.5f);
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        view.getPaint().setMaskFilter(filter);

        Shimmer shimmer = new Shimmer();
        shimmer.setDuration(2000);
        shimmer.setStartDelay(1000);
        shimmer.start(view);

        view.setTypeface(ResourcesCompat.getFont(activity, R.font.game_bold), Typeface.NORMAL);

        if(useParticles) {
            CountDownTimer countDownTimer = new CountDownTimer(100, 100) {
                @Override
                public void onTick(long millis) {}

                @Override
                public void onFinish() {
                    show();
                }
            };
            countDownTimer.start();
        }
    }
}
