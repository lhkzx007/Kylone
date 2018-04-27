package com.kylone.player.view;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.kylone.video.R;


/**
 * Created by  frank.z on 2016/7/26
 */
public class LoadingRun {
//    ImageView run;
    ImageView loading;
    View root;

    public LoadingRun(View root) {
        this.root = root;
        loading= (ImageView) root.findViewById(R.id.loading_img_load);
//        run = (ImageView) root.findViewById(R.id.loading_img_run);
    }

    public void show(){
        root.bringToFront();
        root.setVisibility(View.VISIBLE);
        startAnimation();
    }

    public void hide(){
        root.setVisibility(View.GONE);
        loading.clearAnimation();
    }


    public void startAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0f, -360f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(-1);
        loading.setAnimation(rotateAnimation);
    }


}
