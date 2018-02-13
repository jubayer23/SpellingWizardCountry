package com.smartysoft.wordpuzzle;

import android.media.Image;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;


public class HowToPlayActivity extends AppCompatActivity {
    CountDownTimer countdown_timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        final ImageView img = (ImageView)findViewById(R.id.img);
        final String[] img_name = {"s2","s3","s4","s5","s6","s7","s8","s9"};

        final int[] counter = {0};


        final int[] long_run = {0};
        countdown_timer=  new CountDownTimer(14000, 1000) {

            public void onTick(long millisUntilFinished) {


                if(counter[0] == 0){
                    long_run[0]++;
                    if(long_run[0]>=4){
                        long_run[0] = 0;
                        counter[0]++;
                        img.setBackgroundResource(HowToPlayActivity.this.getResources()
                                .getIdentifier(img_name[counter[0]], "drawable",
                                        getPackageName()));
                    }
                }else if(counter[0] == 3){
                    long_run[0]++;
                    if(long_run[0]>=4){
                        long_run[0] = 0;
                        counter[0]++;
                        img.setBackgroundResource(HowToPlayActivity.this.getResources()
                                .getIdentifier(img_name[counter[0]], "drawable",
                                        getPackageName()));
                    }
                }else{
                    counter[0]++;
                    img.setBackgroundResource(HowToPlayActivity.this.getResources()
                            .getIdentifier(img_name[counter[0]], "drawable",
                                    getPackageName()));

                }


                // donutProgress.setText(String.valueOf(remaining_sec));
            }

            public void onFinish() {
                    finish();
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        if (countdown_timer != null)
            countdown_timer.cancel();
        super.onBackPressed();
    }
}
