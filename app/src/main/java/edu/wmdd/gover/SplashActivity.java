package edu.wmdd.gover;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Timer;
import java.util.TimerTask;

import static androidx.databinding.DataBindingUtil.setContentView;

public class SplashActivity extends AppCompatActivity {

    LottieAnimationView lottieAnimationView;
    int flag = 0;

    private static int SPLASH_SCREEN_TIME_OUT=6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.

        setContentView(R.layout.activity_splash);


        lottieAnimationView = (LottieAnimationView) findViewById(R.id.animation_view);
//        startCheckAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(SplashActivity.this,
                        LoginActivity.class);
                //Intent is used to switch from one activity to another.

                startActivity(i);
                //invoke the SecondActivity.

                finish();
                //the current activity will get finished.
            }
        }, SPLASH_SCREEN_TIME_OUT);

    }

//    private void startCheckAnimation() {
//        ValueAnimator animator = ValueAnimator.ofFloat(0f,1f).setDuration(6000);
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                lottieAnimationView.setProgress((Float).animation.getAnimatedValue());
//            }
//        });
//
//        if (lottieAnimationView.getProgress() == 0f) {
//            animator.start();
//        } else {
//            lottieAnimationView.setProgress(0f);
//        }
//    }

//    private void startCheckAnimation() {
////        ValueAnimator animator = ValueAnimator.ofFloat(0f,1f).setDuration(6000);
////        animator.addUpdateListener(animation -> {
////            lottieAnimationView.setProgress(0f);
////        });
////
////        if (lottieAnimationView.getProgress() == 0f) {
////            animator.start();
////        } else {
////            lottieAnimationView.setProgress(0f);
////        }
////    }

    public void animate(View v) {

        ValueAnimator animator = ValueAnimator.ofFloat(0f,1f).setDuration(6000);

        if (lottieAnimationView.isAnimating()) {
            lottieAnimationView.cancelAnimation();
        } else {
            lottieAnimationView.playAnimation();
        }
    }

}
