package com.smartysoft.wordpuzzle;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.smartysoft.wordpuzzle.appdata.AppConstant;
import com.smartysoft.wordpuzzle.appdata.AppController;
import com.smartysoft.wordpuzzle.service.MusicService;
import com.smartysoft.wordpuzzle.sharedPref.PrefManager;
import com.smartysoft.wordpuzzle.view.MyTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GamePlay extends AppCompatActivity implements View.OnClickListener, ServiceConnection {


    private static int levelNum = 1;

    private static int levelCircle = 0, levelProgress = 1;

    private LinearLayout layout_keyboard, layout_solve_keyboard, layout_pu;
    private LinearLayout layout_keyboard_2, layout_solve_keyboard_2;

    private List<String> wordList;

    private static String levelQuestString = "";

    private static String shuffleString = "";

    private ArrayList<ImageView> keyboard = new ArrayList<ImageView>();
    private ArrayList<ImageView> keyboard_solve = new ArrayList<ImageView>();

    private static Integer[] string_track;
    private char userWordCharArray[];

    private ImageView correctWordComplement;


    private static HashMap<Integer, Integer> keyboard_map = new HashMap<Integer, Integer>();

    private static HashMap<Integer, Integer> keyboard_inverse_map = new HashMap<Integer, Integer>();

   // private DonutProgress donutProgress;

    private Timer timer;

    private CountDownTimer countdown_timer;

    private Animation move_left, move_left_level_passed;
    private Animation move_down, move_up, move_up_zoom_in, bounce;


    private Button btn_solve_letter, btn_add_time, btn_jump_level;

    private MyTextView tv_pu_notification;

    // indicates whether the activity is linked to service player.
    private boolean mIsBound = false;

    // Saves the binding instance with the service.
    private MusicService mServ;

    private float remaining_sec;

    private static final int COUNTDOWN_SEC_WORD_LENGTH_3 = 15;
    private static final int COUNTDOWN_SEC_WORD_LENGTH_4 = 25;
    private static final int COUNTDOWN_SEC_WORD_LENGTH_5 = 35;
    private static final int COUNTDOWN_SEC_WORD_LENGTH_6 = 45;
    private static final int COUNTDOWN_MILI_SEC = COUNTDOWN_SEC_WORD_LENGTH_3 * 1000;

    // private double timeSpentOnCurrentLevel = 0.0D;

    private Dialog dialog;

    private ArrayList<String> similarWordList = new ArrayList<String>();

    private boolean FLAG_GAME_RESUME = true;
    private boolean FLAG_GAME_PAUSED_DURING_PLAY = false;


    private static int numOfJumpPowerUsed = 0;

    // variable for initial FONT
    //private static String fontpath_nuevaStd = "fonts/Nueva_Std.ttf";
    private static String fontpath_badabb = "fonts/badaboom-bb.regular.ttf";
    private Typeface nueva, badabb;

    // ADMOB VARIABLE
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;

   // private TextView tv_plus_time;
    private int current_level_countdown_sec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play_3);

        // Starting the service of the player, if not already started.
        Intent music = new Intent(this, MusicService.class);
        // music.putExtra("music_name", "tired");
        startService(music);

        doBindService();

        //this method is called for the single time
        loadAdView();
        //this method is called for the single time
        initial_font();
        //this method is called for the single time
        init();
        //this method is called for the single time
        loadAnimation();
        //this method is called for the single time
        setDonutCircleWidthAndHeiht();


        /****/

        FLAG_GAME_RESUME = false;
        VisibleInsvisibleOnGameOver();

        doOnlyFirstTimeInitialization();

        getLevelNum();

        getLevelData();

        getlevelQuest(levelCircle);

        getShuffleString(levelQuestString);

        setupKeyboard();

        updatePowerUp("null", "null");
        levelProgress = 1;
        VisibleInsvisibleOnGameOver();

        /*****/

    }

    private void initial_font() {

        // Loading Font Face
        //nueva = Typeface.createFromAsset(getAssets(), fontpath_nuevaStd);
        // Loading Font Face
        badabb = Typeface.createFromAsset(getAssets(), fontpath_badabb);

    }

    private void init() {

        wordList = new ArrayList<String>();

        layout_keyboard = (LinearLayout) findViewById(R.id.layout_keybaord);
        layout_keyboard_2 = (LinearLayout) findViewById(R.id.layout_keybaord_2);

        layout_solve_keyboard = (LinearLayout) findViewById(R.id.layout_keybaord_solve);
        layout_solve_keyboard_2 = (LinearLayout) findViewById(R.id.layout_keybaord_solve_2);

        correctWordComplement = (ImageView) findViewById(R.id.main_correct_word_complement);
        correctWordComplement.setVisibility(View.INVISIBLE);

       // donutProgress = (DonutProgress) findViewById(R.id.donut_progress);


        btn_solve_letter = (Button) findViewById(R.id.btn_solve_letter);
        btn_solve_letter.setOnClickListener(this);
        btn_add_time = (Button) findViewById(R.id.btn_add_time);
        btn_add_time.setOnClickListener(this);
        btn_jump_level = (Button) findViewById(R.id.btn_jump_level);
        btn_jump_level.setOnClickListener(this);

        tv_pu_notification = (MyTextView) findViewById(R.id.pu_notification_tv);

        layout_pu = (LinearLayout) findViewById(R.id.pu_layout);

        //tv_plus_time = (TextView) findViewById(R.id.tv_plus_time);

    }

    private void doOnlyFirstTimeInitialization() {
        levelCircle = 0;
        numOfJumpPowerUsed = 0;
        levelProgress = 1;
        //isUserEligibleForExtraTwoSec = false;
    }

    private void getLevelNum() {

        levelNum = AppController.getInstance().getPrefManger().getLevelNum();

    }

    private void getLevelData() {
        wordList.clear();

        if (levelNum == 1) {
            Collections.shuffle(AppConstant.word_length_3);
            wordList.addAll(AppConstant.word_length_3);
        } else if (levelNum == 2) {
            Collections.shuffle(AppConstant.word_length_4);
            wordList.addAll(AppConstant.word_length_4);
        } else if (levelNum == 3) {
            Collections.shuffle(AppConstant.word_length_5);
            wordList.addAll(AppConstant.word_length_5);
        } else if (levelNum == 4) {
            Collections.shuffle(AppConstant.word_length_6);
            wordList.addAll(AppConstant.word_length_6);
        }  else if (levelNum == 5) {
            Collections.shuffle(AppConstant.word_length_7);
            wordList.addAll(AppConstant.word_length_7);
        }  else if (levelNum == 6) {
            Collections.shuffle(AppConstant.word_length_8);
            wordList.addAll(AppConstant.word_length_8);
        }  else if (levelNum == 7) {
            Collections.shuffle(AppConstant.word_length_9);
            wordList.addAll(AppConstant.word_length_9);
        }  else if (levelNum == 8) {
            Collections.shuffle(AppConstant.word_length_10);
            wordList.addAll(AppConstant.word_length_10);
        } else {
            Collections.shuffle(AppConstant.word_length_4);
            wordList.addAll(AppConstant.word_length_4);
        }

        Collections.shuffle(wordList);
    }

    private void getlevelQuest(int levelCircle) {

        if (levelCircle < wordList.size()) {
            levelQuestString = wordList.get(levelCircle).toLowerCase();
        } else {
            try {
                levelQuestString = wordList.get(0).toLowerCase();
            } catch (Exception e) {
                finish();
            }
        }

        string_track = new Integer[levelQuestString.length()];
        userWordCharArray = new char[levelQuestString.length()];
        // Log.d("DEBUG", String.valueOf(temp.length));
        for (int i = 0; i < levelQuestString.length(); i++) {
            string_track[i] = 0;
            userWordCharArray[i] = '0';
        }

        similarWordList.clear();

        try {
            if (AppConstant.similarWordMap.get(levelQuestString) != null) {
                similarWordList.addAll(AppConstant.similarWordMap.get(levelQuestString));
            }
        } catch (Exception e) {

        }

    }

    private void getShuffleString(String levelQuestString) {

        do {
            shuffleString = shuffle(levelQuestString);

        }
        while (levelQuestString.equalsIgnoreCase(shuffleString) || similarWordList.contains(shuffleString));


    }

    private void setupKeyboard() {
        keyboard.clear();
        keyboard_solve.clear();

        int marginLeft[] = new int[10];
        int marginRight[] = new int[10];

        if (layout_keyboard.getChildCount() > 0)
            layout_keyboard.removeAllViews();

        if (layout_keyboard_2.getChildCount() > 0)
            layout_keyboard_2.removeAllViews();

        if (layout_solve_keyboard.getChildCount() > 0)
            layout_solve_keyboard.removeAllViews();

        if (layout_solve_keyboard_2.getChildCount() > 0)
            layout_solve_keyboard_2.removeAllViews();

        if (levelQuestString.length() == 3) {
            marginLeft[1] = AppController.getInstance().getPixelValue(50);
            marginRight[1] = AppController.getInstance().getPixelValue(50);

        } else if (levelQuestString.length() == 4) {

            if (AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_MDPI) || AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_LDPI)
                    || AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_HDPI)) {
                marginLeft[0] = 10;
                marginLeft[1] = 10;
                marginLeft[2] = 10;
                marginLeft[3] = 10;
                marginRight[3] = 16;
            } else {
                marginLeft[0] = 15;
                marginLeft[1] = 20;
                marginLeft[2] = 20;
                marginLeft[3] = 20;
                marginRight[3] = 20;
            }
        } else if (levelQuestString.length() == 5) {


            if (AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_MDPI) || AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_LDPI)
                    || AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_HDPI)) {
                marginLeft[0] = 5;
                marginLeft[1] = 3;
                marginLeft[2] = 3;
                marginLeft[3] = 3;
                marginLeft[4] = 3;
                marginRight[4] = 18;
            } else {
                marginLeft[0] = 5;
                marginLeft[1] = 10;
                marginLeft[2] = 10;
                marginLeft[3] = 10;
                marginLeft[4] = 10;
                marginRight[4] = 18;
            }

        }
        else if (levelQuestString.length() == 8) {


            if (AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_MDPI) || AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_LDPI)
                    || AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_HDPI)) {
                marginLeft[0] = 5;
                marginLeft[1] = 3;
                marginLeft[2] = 3;
                marginLeft[3] = 3;
                marginLeft[4] = 3;
                marginLeft[5] = 3;
                marginRight[5] = 5;

                marginLeft[7] = AppController.getInstance().getPixelValue(50);
                marginRight[7] = AppController.getInstance().getPixelValue(50);
            } else {
                marginLeft[0] = 0;
                marginLeft[1] = 5;
                marginLeft[2] = 5;
                marginLeft[3] = 5;
                marginLeft[4] = 5;
                marginLeft[5] = 5;
                marginRight[5] = 5;

                marginLeft[7] = AppController.getInstance().getPixelValue(50);
                marginRight[7] = AppController.getInstance().getPixelValue(50);
            }
        }

        else if (levelQuestString.length() == 9) {


            if (AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_MDPI) || AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_LDPI)
                    || AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_HDPI)) {
                marginLeft[0] = 5;
                marginLeft[1] = 3;
                marginLeft[2] = 3;
                marginLeft[3] = 3;
                marginLeft[4] = 3;
                marginLeft[5] = 3;
                marginRight[5] = 5;

                marginLeft[6] = 10;
                marginLeft[7] = 10;
                marginLeft[8] = 10;
                marginLeft[9] = 10;
                marginRight[9] = 16;
            } else {
                marginLeft[0] = 0;
                marginLeft[1] = 5;
                marginLeft[2] = 5;
                marginLeft[3] = 5;
                marginLeft[4] = 5;
                marginLeft[5] = 5;
                marginRight[5] = 5;

                marginLeft[6] = 15;
                marginLeft[7] = 20;
                marginLeft[8] = 20;
                marginLeft[9] = 20;
                marginRight[9] = 20;;
            }
        }

        else if (levelQuestString.length() == 10) {


            if (AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_MDPI) || AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_LDPI)
                    || AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_HDPI)) {
                marginLeft[0] = 5;
                marginLeft[1] = 3;
                marginLeft[2] = 3;
                marginLeft[3] = 3;
                marginLeft[4] = 3;
                marginLeft[5] = 3;
                marginRight[5] = 5;

                marginLeft[6] = 5;
                marginLeft[7] = 3;
                marginLeft[8] = 3;
                marginLeft[9] = 3;
                marginLeft[10] = 3;
                marginRight[10] = 18;
            } else {
                marginLeft[0] = 0;
                marginLeft[1] = 5;
                marginLeft[2] = 5;
                marginLeft[3] = 5;
                marginLeft[4] = 5;
                marginLeft[5] = 5;
                marginRight[5] = 5;

                marginLeft[6] = 5;
                marginLeft[7] = 10;
                marginLeft[8] = 10;
                marginLeft[9] = 10;
                marginLeft[10] = 10;
                marginRight[10] = 18;
            }
        }


        for (int i = 0; i < levelQuestString.length(); i++) {


            /***************************************KEYBOARD*********************************************/
            ImageView img = new ImageView(this);

            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            imgParams.setMargins(marginLeft[i], 0, marginRight[i], 0);
            img.setLayoutParams(imgParams);
            img.setImageResource(GamePlay.this.getResources()
                    .getIdentifier(getDrawableFileName(shuffleString.charAt(i)), "drawable",
                            getPackageName()));

            img.setOnClickListener(this);

            keyboard.add(img);


            if(i < 5){
                layout_keyboard.addView(img);
            }else{
                layout_keyboard_2.addView(img);
            }


            /***************************************SOLVE KEYBOARD*********************************************/
            ImageView img_solve = new ImageView(this);

            LinearLayout.LayoutParams imgParams_solve = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            imgParams_solve.setMargins(marginLeft[i], 0, marginRight[i], 0);
            img_solve.setLayoutParams(imgParams_solve);
            img_solve.setImageResource(R.drawable.letter_blank_background);

            img_solve.setOnClickListener(this);

            keyboard_solve.add(img_solve);

            if(i < 5){
                layout_solve_keyboard.addView(img_solve);
            }else{
                layout_solve_keyboard_2.addView(img_solve);
            }


        }


    }


    private void setDonutCircleWidthAndHeiht() {
        int width = AppController.getInstance().getPixelValue(350);
        int height = AppController.getInstance().getPixelValue(340);

        if (AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_LDPI)) {

            width = AppController.getInstance().getPixelValue(110);
            height = AppController.getInstance().getPixelValue(100);
        }
        if (AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_HDPI)) {

            width = AppController.getInstance().getPixelValue(270);
            height = AppController.getInstance().getPixelValue(260);

        }
        if (AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_MDPI)) {
            width = AppController.getInstance().getPixelValue(210);
            height = AppController.getInstance().getPixelValue(200);
        }
        if (AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_XHDPI)) {

            width = AppController.getInstance().getPixelValue(310);
            height = AppController.getInstance().getPixelValue(300);

        }
        if (AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_XXHDPI)) {

        }
        if (AppController.getInstance().getDeviceScreenSize().equalsIgnoreCase(AppController.SCREENSIZE_XXXHDPI)) {

        }


        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(width, height);

       // donutProgress.setLayoutParams(imgParams);
    }

    public void startCounDownAndTimerForProgessBar() {
        //donutProgress.setText(String.valueOf(levelProgress));

        current_level_countdown_sec = COUNTDOWN_SEC_WORD_LENGTH_3;

        switch (levelQuestString.length()) {
            case 3:
                current_level_countdown_sec = COUNTDOWN_SEC_WORD_LENGTH_3;
                break;
            case 4:
                current_level_countdown_sec = COUNTDOWN_SEC_WORD_LENGTH_4;
                break;
            case 5:
                current_level_countdown_sec = COUNTDOWN_SEC_WORD_LENGTH_5;
                break;
            case 6:
                current_level_countdown_sec = COUNTDOWN_SEC_WORD_LENGTH_6;
                break;
        }

        int countdown_miliSec;
       // if (isUserEligibleForExtraTwoSec) {
       //     countdown_miliSec = (current_level_countdown_sec + 2) * 1000;
       // } else {
            countdown_miliSec = current_level_countdown_sec * 1000;
       // }
        int donut_progressbar_speed = (int) (countdown_miliSec / 100);


        //donutProgress.setProgress(0);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //donutProgress.setProgress(donutProgress.getProgress() + 1);
                    }
                });
            }
        }, 0, donut_progressbar_speed);


        countdown_timer = new CountDownTimer(countdown_miliSec, 1000) {

            public void onTick(long millisUntilFinished) {
                remaining_sec = (float) (millisUntilFinished / 1000);
                // donutProgress.setText(String.valueOf(remaining_sec));
            }

            public void onFinish() {

                AppConstant.GAMEOVER_COUNTER++;


                // Log.d("DEBUG_OnFinish", String.valueOf(FLAG_GAME_RESUME));

                FLAG_GAME_RESUME = false;

                stopMusic();
                if (timer != null) {
                    timer.cancel();
                }

                clearSolveKeyBoardFully();

                /*
                if (AppConstant.GAMEOVER_COUNTER >= 2 && !FLAG_GAME_PAUSED_DURING_PLAY) {
                    AppConstant.GAMEOVER_COUNTER = 0;
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        clearSolveKeyBoardFully();
                        //dialogGameOver();
                    }
                } else {
                    clearSolveKeyBoardFully();
                    //dialogGameOver();
                }*/


            }
        }.start();
    }


    public String shuffle(String input) {
        List<Character> characters = new ArrayList<Character>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(input.length());
        while (characters.size() != 0) {
            int randPicker = (int) (Math.random() * characters.size());
            output.append(characters.remove(randPicker));
        }
        return output.toString();
    }

    @Override
    public void onClick(View view) {

        if (FLAG_GAME_RESUME) {
            if (AppController.getInstance().getPrefManger().getMusicOnOff() && mServ !=null) {
                mServ.start2("click", false);
            }


            if (keyboard.contains(view)) {
                // Toast.makeText(this,
                //         String.valueOf(keyboardCharArray[keyboard.indexOf(view)]),
                //         Toast.LENGTH_SHORT).show();
                updateSolveKeyboard(shuffleString.charAt(keyboard.indexOf(view)),
                        keyboard.indexOf(view), view, 0);


                // checkarray(userword);

            }
            if (keyboard_solve.contains(view)) {
                if (string_track[keyboard_solve.indexOf(view)] == 1) {
                    manageChangeInSolveKeyboard(keyboard_solve.indexOf(view));
                }

            }


            if (view.getId() == R.id.btn_solve_letter) {
                if (AppController.getInstance().getPrefManger().getSolveletterEnable()) {


                    solveLetter();

                    updatePowerUp("minus", PrefManager.KEY_SOLVE_LETTER);
                } else {
                    Toast.makeText(this, "Power Is Empty!!", Toast.LENGTH_LONG).show();
                }
            }
            if (view.getId() == R.id.btn_jump_level) {
                if (AppController.getInstance().getPrefManger().getJumpLevelEnable()) {
                    jumpLevel2();

                    updatePowerUp("minus", PrefManager.KEY_JUMP_LEVEL);
                } else {
                    Toast.makeText(this, "Power Is Empty!!", Toast.LENGTH_LONG).show();
                }
            }
            if (view.getId() == R.id.btn_add_time) {
                if (AppController.getInstance().getPrefManger().getAddTimeEnable()) {
                    addTime();

                    updatePowerUp("minus", PrefManager.KEY_ADD_TIME);
                } else {
                    Toast.makeText(this, "Power Is Empty!!", Toast.LENGTH_LONG).show();
                }
            }
        }


    }

    private void clearSolveKeyBoardFully(){
        for(int i = 0 ; i < levelQuestString.length(); i++){
            if (string_track[i] == 1) {
                manageChangeInSolveKeyboard(i);
            }
        }


        for(int i = 0; i < levelQuestString.length() ; i++){
            solveLetter(i);
        }


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppConstant.GAMEOVER_COUNTER >= 2 && !FLAG_GAME_PAUSED_DURING_PLAY) {
                    AppConstant.GAMEOVER_COUNTER = 0;
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        dialogGameOver();
                    }
                } else {
                    dialogGameOver();
                }
            }
        }, 3000);


    }

    private void playMusic(String music_name) {
        if (AppController.getInstance().getPrefManger().getMusicOnOff() && mServ !=null) {
            mServ.start(music_name, true);
        }

    }

    private void stopMusic() {
        if (mServ != null) {
            mServ.stop();
        }
    }

    private void updateSolveKeyboard(char onSolveChar, int keyboard_pos,
                                     View view, int index_start) {
        // TODO Auto-generated method stub

        for (int index = index_start; index < levelQuestString.length(); index++) {

            if (string_track[index] == 0) {

                keyboard_solve.get(index).setImageResource(GamePlay.this.getResources()
                            .getIdentifier(getDrawableFileName(onSolveChar), "drawable",
                                    getPackageName()));



                userWordCharArray[index] = onSolveChar;

                string_track[index] = 1;

                keyboard_map.put(index, keyboard_pos);
                keyboard_inverse_map.put(keyboard_pos, index);

                ImageView v = (ImageView) view;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    v.setImageAlpha(128);
                } else {
                    v.setAlpha(128);
                }
                v.setEnabled(false);


                String userword = String.copyValueOf(userWordCharArray);

                if (userword.equalsIgnoreCase(levelQuestString) || similarWordList.contains(userword)) {

                    if (FLAG_GAME_RESUME) {

                        FLAG_GAME_RESUME = false;

                        // Log.d("DEBUG_solve", String.valueOf(FLAG_GAME_RESUME));

                        //Toast.makeText(this, "Match", Toast.LENGTH_LONG).show();

                        stopMusic();

                        showLevelCompleteComplement();
                    }


                    //    }
                    // }, 3000);
                } else {
                }
                break;

            }

        }

    }


    private void manageChangeInSolveKeyboard(int onSolveKeyboard_pos) {

        Log.d("onSolveKeyboard_pos", String.valueOf(onSolveKeyboard_pos));

        string_track[onSolveKeyboard_pos] = 0;


        keyboard_solve.get(onSolveKeyboard_pos).setImageResource(R.drawable.letter_blank_background);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            keyboard.get(keyboard_map.get(onSolveKeyboard_pos)).setImageAlpha(255);
        } else {
            keyboard.get(keyboard_map.get(onSolveKeyboard_pos)).setAlpha(255);
        }
        keyboard.get(keyboard_map.get(onSolveKeyboard_pos)).setEnabled(true);

    }

    private void showLevelCompleteComplement() {


        /*    stop Timer And CoundDown     */
        if (countdown_timer != null)
            countdown_timer.cancel();
        if (timer != null) {
            timer.cancel();
        }



        correctWordComplement.setVisibility(View.VISIBLE);

        correctWordComplement.setImageResource(GamePlay.this.getResources()
                .getIdentifier(AppConstant.complementList[AppConstant.LANGUAGE][levelCircle % 6], "drawable",
                        getPackageName()));

        correctWordComplement.startAnimation(move_left);


        if (AppController.getInstance().getPrefManger().getMusicOnOff() && mServ !=null) {
                mServ.start("correto", false);
        }


        // Execute some code after 2 seconds have passed
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                VisibleInsvisibleOnGameOver();

                startNextLevel();
            }
        }, 2000);

    }

    private void startNextLevel() {
        cleanUp();


        levelCircle++;
        levelProgress++;

        //donutProgress.setText(String.valueOf(levelProgress));

        if (((levelProgress - 1) % AppConstant.powerUpCircle) == 0) {
            updatePowerUp("plus", "null");
        }


        if (levelCircle < wordList.size() && levelCircle < (AppConstant.levelCircleLimit[levelNum] + numOfJumpPowerUsed)) {

            getlevelQuest(levelCircle);

            getShuffleString(levelQuestString);

            setupKeyboard();


            VisibleInsvisibleOnGameOver();

        } else {
            levelNum++;

            levelCircle = 0;
            numOfJumpPowerUsed = 0;

            saveLevelNum();


            getLevelNum();

            getLevelData();

            getlevelQuest(levelCircle);

            getShuffleString(levelQuestString);

            setupKeyboard();


            VisibleInsvisibleOnGameOver();
        }
    }

    private void cleanUp() {

        correctWordComplement.setVisibility(View.INVISIBLE);

        keyboard.clear();
        keyboard_solve.clear();


    }

    private void saveLevelNum() {
        AppController.getInstance().getPrefManger().setLevelNum(levelNum);
    }


    private String getDrawableFileName(char c) {
        String drawableFileName = "a";

        switch (c) {
            case 'ã':
                drawableFileName = "a2";
                break;
            case 'á':
                drawableFileName = "a1";
                break;
            case 'é':
                drawableFileName = "e1";
                break;

            case 'ô':
                drawableFileName = "o3";
                break;
            case 'í':
                drawableFileName = "i1";
                break;
            case 'ê':
                drawableFileName = "e3";
                break;

            case 'ç':
                drawableFileName = "ce";
                break;
            case 'ó':
                drawableFileName = "o1";
                break;
            case 'õ':
                drawableFileName = "o2";
                break;

            case 'ú':
                drawableFileName = "u1";
                break;
            default:
                drawableFileName = String.valueOf(c);
                break;
        }

        return drawableFileName;

    }

    private void loadAnimation() {
        move_left = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move_left);

        move_left.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                //FLAG_GAME_RESUME = true;

                correctWordComplement.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        move_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move_down);
        move_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move_up);
        move_up.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (dialog != null) {
                    dialog.dismiss();


                    AppController.getInstance().getPrefManger().setLevelNum(1);

                    doOnlyFirstTimeInitialization();

                    getLevelNum();

                    getLevelData();

                    getlevelQuest(levelCircle);

                    getShuffleString(levelQuestString);

                    setupKeyboard();

                    VisibleInsvisibleOnGameOver();


                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        move_up_zoom_in = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move_up_zoom_in);
        move_up_zoom_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

               // tv_plus_time.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //tv_plus_time.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        move_left_level_passed = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move_left);

        bounce = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);
    }

    private void updatePowerUp(String Case, String btn_click) {

        boolean flag = false;
        Button btn_need_to_animate = null;

        switch (Case) {
            case "plus":
                flag = true;
                Log.d("DEBUG", "its herer");
                if (!AppController.getInstance().getPrefManger().getAddTimeEnable()
                        && !AppController.getInstance().getPrefManger().getJumpLevelEnable()
                        && !AppController.getInstance().getPrefManger().getSolveletterEnable()) {

                    AppController.getInstance().getPrefManger().setAddTimeEnable(true);
                    btn_need_to_animate = btn_add_time;

                } else if (AppController.getInstance().getPrefManger().getAddTimeEnable()
                        && !AppController.getInstance().getPrefManger().getJumpLevelEnable()
                        && !AppController.getInstance().getPrefManger().getSolveletterEnable()) {

                    AppController.getInstance().getPrefManger().setJumpLevelEnable(true);
                    btn_need_to_animate = btn_jump_level;

                } else if (!AppController.getInstance().getPrefManger().getAddTimeEnable()
                        && AppController.getInstance().getPrefManger().getJumpLevelEnable()
                        && !AppController.getInstance().getPrefManger().getSolveletterEnable()) {

                    AppController.getInstance().getPrefManger().setAddTimeEnable(true);
                    btn_need_to_animate = btn_add_time;

                } else if (!AppController.getInstance().getPrefManger().getAddTimeEnable()
                        && !AppController.getInstance().getPrefManger().getJumpLevelEnable()
                        && AppController.getInstance().getPrefManger().getSolveletterEnable()) {

                    AppController.getInstance().getPrefManger().setJumpLevelEnable(true);
                    btn_need_to_animate = btn_jump_level;

                } else if (AppController.getInstance().getPrefManger().getAddTimeEnable()
                        && AppController.getInstance().getPrefManger().getJumpLevelEnable()
                        && !AppController.getInstance().getPrefManger().getSolveletterEnable()) {

                    AppController.getInstance().getPrefManger().setSolveletterEnable(true);
                    btn_need_to_animate = btn_solve_letter;

                } else if (!AppController.getInstance().getPrefManger().getAddTimeEnable()
                        && AppController.getInstance().getPrefManger().getJumpLevelEnable()
                        && AppController.getInstance().getPrefManger().getSolveletterEnable()) {

                    AppController.getInstance().getPrefManger().setAddTimeEnable(true);
                    btn_need_to_animate = btn_add_time;

                } else if (AppController.getInstance().getPrefManger().getAddTimeEnable()
                        && !AppController.getInstance().getPrefManger().getJumpLevelEnable()
                        && AppController.getInstance().getPrefManger().getSolveletterEnable()) {

                    AppController.getInstance().getPrefManger().setJumpLevelEnable(true);
                    btn_need_to_animate = btn_jump_level;
                }
                AppController.getInstance().getPrefManger().setPuRemaining(AppController.getInstance().getPrefManger().getPuRemaining() + 1);

                break;
            case "minus":
                if (btn_click.equals(PrefManager.KEY_ADD_TIME)) {
                    AppController.getInstance().getPrefManger().setAddTimeEnable(false);
                } else if (btn_click.equals(PrefManager.KEY_JUMP_LEVEL)) {
                    AppController.getInstance().getPrefManger().setJumpLevelEnable(false);
                } else if (btn_click.equals(PrefManager.KEY_SOLVE_LETTER)) {
                    AppController.getInstance().getPrefManger().setSolveletterEnable(false);
                }
                flag = true;
                AppController.getInstance().getPrefManger().setPuRemaining(AppController.getInstance().getPrefManger().getPuRemaining() - 1);
                break;
            default:
                break;
        }


        if (AppController.getInstance().getPrefManger().getSolveletterEnable()) {
            btn_solve_letter.setBackgroundResource(R.drawable.pu_solve_letter_btn_selector);
        } else {
            btn_solve_letter.setBackgroundResource(R.drawable.pu_solve_letter_p);
        }

        if (AppController.getInstance().getPrefManger().getJumpLevelEnable()) {
            btn_jump_level.setBackgroundResource(R.drawable.pu_jump_level_btn_selector);
        } else {
            btn_jump_level.setBackgroundResource(R.drawable.pu_jump_level_p);
        }

        if (AppController.getInstance().getPrefManger().getAddTimeEnable()) {
            btn_add_time.setBackgroundResource(R.drawable.pu_add_time_btn_selector);
        } else {
            btn_add_time.setBackgroundResource(R.drawable.pu_add_time_p);
        }
        if (btn_need_to_animate != null) {
            btn_need_to_animate.setAnimation(bounce);
            btn_need_to_animate.startAnimation(bounce);
            //YoYo.with(Techniques.Tada).duration(700).playOn(btn_need_to_animate);
        }
        tv_pu_notification.setText(String.valueOf(AppController.getInstance().getPrefManger().getPuRemaining()) + " power available");
        if (flag) YoYo.with(Techniques.BounceInDown)
                .duration(700)
                .playOn(tv_pu_notification);
    }

    public void solveLetter() {


        int random_index = 0;
        Random ran = new Random();
        do {


            random_index = ran.nextInt(levelQuestString.length());

            Log.d("ran", String.valueOf(random_index));

        } while (userWordCharArray[random_index] == levelQuestString.charAt(random_index));


        if (string_track[random_index] == 1) {
            if (userWordCharArray[random_index] != levelQuestString.charAt(random_index)) {
                manageChangeInSolveKeyboard(random_index);
                readyToRevealLetter(levelQuestString.charAt(random_index), random_index);
            }
        } else {
            readyToRevealLetter(levelQuestString.charAt(random_index), random_index);
        }
    }

    private void solveLetter(int position){
        readyToRevealLetter(levelQuestString.charAt(position), position);
    }

    private void readyToRevealLetter(char onSolveLetter, int random_index) {

        for (int key_pos = 0; key_pos < shuffleString.length(); key_pos++) {

            if (shuffleString.charAt(key_pos) == onSolveLetter) {
                updateSolveKeyboard(shuffleString.charAt(key_pos),
                        key_pos, keyboard.get(key_pos), random_index);
                break;
            }

        }
    }


    private void jumpLevel() {
        for (int index = 0; index < levelQuestString.length(); index++) {


            if (string_track[index] == 1) {
                if (userWordCharArray[index] != levelQuestString.charAt(index)) {
                    manageChangeInSolveKeyboard(index);


                    readyToRevealLetter(levelQuestString.charAt(index), 0);


                    // break;

                }
            } else {
                readyToRevealLetter(levelQuestString.charAt(index), 0);

                //break;
            }
        }
    }

    private void jumpLevel2() {
        FLAG_GAME_RESUME = false;

        stopMusic();
        /*    stop Timer And CoundDown     */
        if (countdown_timer != null)
            countdown_timer.cancel();
        if (timer != null) {
            timer.cancel();
        }

        showLevelJumpAnimation();

    }

    private void showLevelJumpAnimation() {
        move_left_level_passed.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                correctWordComplement.setVisibility(View.INVISIBLE);

                //donutProgress.setProgress(0);

                VisibleInsvisibleOnGameOver();

               // donutProgress.setProgress(0);


                levelCircle++;
                numOfJumpPowerUsed++;


                if (levelCircle < wordList.size()) {

                    getlevelQuest(levelCircle);

                    getShuffleString(levelQuestString);

                    setupKeyboard();


                    VisibleInsvisibleOnGameOver();
                } else {
                    levelNum++;

                    levelCircle = 0;

                    saveLevelNum();


                    getLevelNum();

                    getLevelData();

                    getlevelQuest(levelCircle);

                    getShuffleString(levelQuestString);

                    setupKeyboard();


                    VisibleInsvisibleOnGameOver();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        correctWordComplement.setVisibility(View.VISIBLE);

        correctWordComplement.setImageResource(GamePlay.this.getResources()
                .getIdentifier("level_passed", "drawable",
                        getPackageName()));

        correctWordComplement.startAnimation(move_left_level_passed);
    }

    private void addTime() {
        //donutProgress.setProgress(0);

        if (timer != null) {
            timer.cancel();
        }

        if (countdown_timer != null) {
            countdown_timer.cancel();
        }

        startCounDownAndTimerForProgessBar();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {


        mServ = ((MusicService.ServiceBinder) iBinder).getService();


        if (AppController.getInstance().getPrefManger().getMusicOnOff()) {
            //playMusic("tic_toc");
        }


    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

        mServ = null;
    }

    public void doBindService() {
        // activity connects to the service.
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    @Override
    public void onBackPressed() {

        if (FLAG_GAME_RESUME) {

            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }


            if(mServ != null){
                mServ.stop();
                mServ.stop2();
            }
            if (countdown_timer != null) {
                countdown_timer.cancel();
            }
            if (timer != null) {
                timer.cancel();
            }
            doUnbindService();


            super.onBackPressed();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        FLAG_GAME_PAUSED_DURING_PLAY = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("DEBUG_PAUSE","called 1");
        if (FLAG_GAME_RESUME) {
            Log.d("DEBUG_PAUSE","game is running but on pause called");
            FLAG_GAME_PAUSED_DURING_PLAY = true;
        }else{
            Log.d("DEBUG_PAUSE","game is NOT running but on pause called");
        }
        if (AppController.getInstance().getPrefManger().getMusicOnOff() && mServ != null) {
            mServ.stop();
            mServ.stop2();
        }

    }

    @Override
    public void onDestroy() {


        doUnbindService();

        super.onDestroy();


    }

    public void doUnbindService() {
        // disconnects the service activity.
        if (mIsBound) {
            unbindService(this);
            mIsBound = false;
        }
    }


    private void dialogGameOver() {

        VisibleInsvisibleOnGameOver();

        if (AppController.getInstance().getPrefManger().getMusicOnOff() && !FLAG_GAME_PAUSED_DURING_PLAY && mServ !=null) {
            mServ.start("errado", false);
        }

        //donutProgress.setProgress(0);

        dialog = new Dialog(this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_gameover_2);

        final RelativeLayout layout = (RelativeLayout) dialog.findViewById(R.id.dialoggameover_layout);

        layout.startAnimation(move_down);

        LinearLayout layout_score = (LinearLayout) dialog.findViewById(R.id.dialoggameover_score);
        if (layout_score.getChildCount() > 0) layout_score.removeAllViews();
        LinearLayout layout_top = (LinearLayout) dialog.findViewById(R.id.dialoggameover_top);
        if (layout_top.getChildCount() > 0) layout_top.removeAllViews();

        ImageView btn_play = (ImageView) dialog.findViewById(R.id.dialoggameoverbtn_playagain);
        ImageView dialoggameoverbtn_home = (ImageView) dialog.findViewById(R.id.dialoggameoverbtn_hone);


        String highscore = AppController.getInstance().getPrefManger().getHighScore();

        int user_score = levelProgress - 1;

        if (user_score > Integer.parseInt(highscore)) {
            highscore = String.valueOf(user_score);
            AppController.getInstance().getPrefManger().setHighScore(String.valueOf(user_score));
            if (MainActivity.isSignedIn()) {
                MainActivity.saveScoreToLeaderBoard(getString(R.string.leaderboard_highestscore));
            }
        }

        for (int i = 0; i < String.valueOf(user_score).length(); i++) {
            ImageView img = new ImageView(this);

            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            //imgParams.setMargins(marginLeft[i], 0, marginRight[i], 0);
            img.setLayoutParams(imgParams);
            img.setImageResource(GamePlay.this.getResources()
                    .getIdentifier("number_" + String.valueOf(user_score).charAt(i), "drawable",
                            getPackageName()));
            layout_score.addView(img);
        }

        for (int i = 0; i < highscore.length(); i++) {
            ImageView img = new ImageView(this);

            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            //imgParams.setMargins(marginLeft[i], 0, marginRight[i], 0);
            img.setLayoutParams(imgParams);
            img.setImageResource(GamePlay.this.getResources()
                    .getIdentifier("number_" + highscore.charAt(i), "drawable",
                            getPackageName()));
            layout_top.addView(img);
        }


        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                layout.startAnimation(move_up);


            }
        });


        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //dialog = null;

            }
        });

        dialoggameoverbtn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();

    }

    private void VisibleInsvisibleOnGameOver() {
        if (layout_solve_keyboard.getVisibility() == View.VISIBLE) {
            layout_solve_keyboard.setVisibility(View.INVISIBLE);
        } else {
            layout_solve_keyboard.setVisibility(View.VISIBLE);

            YoYo.with(Techniques.FadeIn).duration(600).interpolate(new AccelerateInterpolator()).playOn(layout_solve_keyboard);


        }

        if (layout_keyboard.getVisibility() == View.VISIBLE) {
            layout_keyboard.setVisibility(View.INVISIBLE);
        } else {
            layout_keyboard.setVisibility(View.VISIBLE);

            YoYo.with(Techniques.FadeIn).duration(600).interpolate(new AccelerateInterpolator()).playOn(layout_keyboard);
        }

        if (layout_pu.getVisibility() == View.VISIBLE) {
            layout_pu.setVisibility(View.INVISIBLE);
        } else {
            layout_pu.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.FadeIn).duration(700).interpolate(new AccelerateInterpolator()).withListener(new com.nineoldandroids.animation.Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(com.nineoldandroids.animation.Animator animation) {

                }

                @Override
                public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                    FLAG_GAME_RESUME = true;

                    startCounDownAndTimerForProgessBar();

                    updatePowerUp("null", "null");

                    playMusic("tic_toc");
                }

                @Override
                public void onAnimationCancel(com.nineoldandroids.animation.Animator animation) {

                }

                @Override
                public void onAnimationRepeat(com.nineoldandroids.animation.Animator animation) {

                }
            }).playOn(layout_pu);
            ;


            //if (isUserEligibleForExtraTwoSec) {
             //   isUserEligibleForExtraTwoSec = false;
             //   tv_plus_time.setAnimation(move_up_zoom_in);
             //   tv_plus_time.startAnimation(move_up_zoom_in);
                //showAnimationForExtraTwoSecs();
            //}
        }
        if (tv_pu_notification.getVisibility() == View.VISIBLE) {
            tv_pu_notification.setVisibility(View.INVISIBLE);
        } else {
            tv_pu_notification.setVisibility(View.VISIBLE);

            YoYo.with(Techniques.FadeIn).duration(600).interpolate(new AccelerateInterpolator()).playOn(tv_pu_notification);

        }


        //donutProgress.setProgress(0);


    }


    public void loadAdView() {
        mAdView = (AdView) findViewById(R.id.mAdView);
        // AdRequest adRequest = new AdRequest.Builder().build();
        AdRequest request = new AdRequest.Builder()
                .addTestDevice("554FD1C059BF37BF1981C59FF9E1DAE0")
                .build();
        mAdView.loadAd(request);

        mAdView.setVisibility(View.GONE);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
               // Log.d("DEBUG","add close");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                mAdView.setVisibility(View.GONE);
               // Log.d("DEBUG","add failed");
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                //Log.d("DEBUG","add open");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                //Log.d("DEBUG","add load");
                mAdView.setVisibility(View.VISIBLE);
            }
        });
        // load INTERTITIAL ADD VIEW


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.intertitial_test_ad_unit_id));
        requestNewInterstitial();

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();

                dialogGameOver();
                // beginPlayingGame();
            }
        });


    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(
                "554FD1C059BF37BF1981C59FF9E1DAE0").build();

        mInterstitialAd.loadAd(adRequest);
    }

}


