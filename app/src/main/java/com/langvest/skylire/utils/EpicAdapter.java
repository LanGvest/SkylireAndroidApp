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

public class EpicAdapter {

    private final Activity activity;
    private ParticleSystem ps1, ps2, ps3, ps4, ps5, ps6;
    private ShimmerTextView view;
    private CountDownTimer particlesUpdate1, particlesUpdate2, particlesUpdate3, particlesUpdate4;

    public EpicAdapter(Activity activity) {
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
        ps1 = new ParticleSystem(activity, 3, R.drawable.particle_primary_alpha, 2000);
        ps1.setSpeedRange(0.001f, 0.007f);
        ps1.setScaleRange(0.3f, 0.7f);
        ps1.setAcceleration(0.00001f, 270);
        ps1.setFadeOut(1000);

        ps2 = new ParticleSystem(activity, 1, R.drawable.particle_epic_star, 3000);
        ps2.setSpeedRange(0.002f, 0.004f);
        ps2.setScaleRange(0.5f, 1f);
        ps2.setRotationSpeedRange(50, 300);
        ps2.setFadeOut(1000);

        ps3 = new ParticleSystem(activity, 1, R.drawable.particle_epic_star, 5000);
        ps3.setSpeedRange(0.001f, 0.004f);
        ps3.setScaleRange(0.5f, 1f);
        ps3.setRotationSpeedRange(25, 200);
        ps3.setFadeOut(1000);

        ps4 = new ParticleSystem(activity, 3, R.drawable.particle_primary_alpha, 1000);
        ps4.setSpeedRange(0.001f, 0.007f);
        ps4.setScaleRange(0.3f, 0.7f);
        ps4.setAcceleration(0.00001f, 270);
        ps4.setFadeOut(500);

        ps5 = new ParticleSystem(activity, 2, R.drawable.particle_primary_alpha, 2000);
        ps5.setSpeedRange(0.001f, 0.007f);
        ps5.setScaleRange(0.3f, 0.7f);
        ps5.setAcceleration(0.00001f, 270);
        ps5.setFadeOut(1000);

        ps6 = new ParticleSystem(activity, 2, R.drawable.particle_primary_alpha, 1000);
        ps6.setSpeedRange(0.001f, 0.007f);
        ps6.setScaleRange(0.3f, 0.7f);
        ps6.setAcceleration(0.00001f, 270);
        ps6.setFadeOut(500);

        int[] position = getRandomPosition();
        ps1.emit(position[0], position[1], 3);

        position = getRandomPosition();
        ps2.emit(position[0], position[1], 1);

        position = getRandomPosition();
        ps3.emit(position[0], position[1], 1);

        position = getRandomPosition();
        ps4.emit(position[0], position[1], 3);

        position = getRandomPosition();
        ps5.emit(position[0], position[1], 2);

        position = getRandomPosition();
        ps6.emit(position[0], position[1], 2);

        particlesUpdate1 = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millis) {}

            @Override
            public void onFinish() {
                int[] newPosition = getRandomPosition();

                ps1.updateEmitPoint(newPosition[0], newPosition[1]);

                newPosition = getRandomPosition();

                ps5.updateEmitPoint(newPosition[0], newPosition[1]);

                particlesUpdate1.start();
            }
        };

        particlesUpdate2 = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millis) {}

            @Override
            public void onFinish() {
                int[] newPosition = getRandomPosition();

                ps2.updateEmitPoint(newPosition[0], newPosition[1]);

                particlesUpdate2.start();
            }
        };

        particlesUpdate3 = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millis) {}

            @Override
            public void onFinish() {
                int[] newPosition = getRandomPosition();

                ps3.updateEmitPoint(newPosition[0], newPosition[1]);

                particlesUpdate3.start();
            }
        };

        particlesUpdate4 = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millis) {}

            @Override
            public void onFinish() {
                int[] newPosition = getRandomPosition();

                ps4.updateEmitPoint(newPosition[0], newPosition[1]);

                newPosition = getRandomPosition();

                ps6.updateEmitPoint(newPosition[0], newPosition[1]);

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