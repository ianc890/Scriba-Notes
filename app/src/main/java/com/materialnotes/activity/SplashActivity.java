package com.materialnotes.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.TextView;

import com.materialnotes.R;

/**
 * Created by Ian C on 08/06/2016.
 */
public class SplashActivity extends Activity {

    AnimationDrawable scribaAnimation;
    Animation textAnimation;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        /*ImageView animation = (ImageView) findViewById(R.id.imageView);
        animation.setBackgroundResource(R.drawable.scriba_animation);
        scribaAnimation = (AnimationDrawable) animation.getBackground();

        text = (TextView) findViewById(R.id.textView);

        // load the animation
        textAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);

        text.startAnimation(textAnimation);*/

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(1800);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    /*public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            scribaAnimation.start();
            return true;
        }
        return super.onTouchEvent(event);
    }*/

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //scribaAnimation.start();


        super.onWindowFocusChanged(hasFocus);
    }
}
