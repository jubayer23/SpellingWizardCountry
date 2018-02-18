package com.smartysoft.wordpuzzlecountry;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.smartysoft.wordpuzzlecountry.Utils.langDict;
import com.smartysoft.wordpuzzlecountry.appdata.AppConstant;
import com.smartysoft.wordpuzzlecountry.appdata.AppController;
import com.smartysoft.wordpuzzlecountry.service.MusicService;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Button btn_setting, btn_conquitas, btn_ranking;

    private RelativeLayout btn_play;

    private LinearLayout main_setting_layout;

    private langDict word_obj;

    private ImageView traingle;

    private LinearLayout logo;

    private Timer timer;

    private Animation move_down, move_up, move_up_title;

    // indicates whether the activity is linked to service player.
    private boolean mIsBound = false;

    // Saves the binding instance with the service.
    private MusicService mServ;

    private boolean flag_music = false, flag_play_btn_already_clicked = false;

    private Dialog dialog;

    // Client used to interact with Google APIs
    private static GoogleApiClient mGoogleApiClient;

    // Has the user clicked the sign-in button?

    private boolean mSignInClicked = false;

    // Automatically start the sign-in flow when the Activity starts
    private boolean mAutoStartSignInFlow = true;

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    // request codes we use when invoking an external activity
    @SuppressWarnings("unused")
    private static final int RC_RESOLVE = 5000;
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;


    TextView tv_how_to_play;

    private static boolean flagTryOnlyFirstTime = true;
    private static boolean flagCallFromShowLeaderboardBtn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_3);


        // Create the Google API Client with access to Plus and Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).addApi(Games.API)
                .addScope(Games.SCOPE_GAMES).build();


        // Starting the service of the player, if not already started.
        Intent music = new Intent(this, MusicService.class);
        // music.putExtra("music_name", "tired");
        startService(music);

        doBindService();

        loadAnimation();

        init();

        // new LoadViewTask().execute();


    }


    @Override
    protected void onStart() {
        super.onStart();
        if (isNetworkAvailable() && flagTryOnlyFirstTime) {
            // start the sign-in flow
           // flagTryOnlyFirstTime = false;
           // mSignInClicked = true;
           // mGoogleApiClient.connect();
        }
    }

    private void loadAnimation() {
        move_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move_down);
        move_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move_up);
        move_up_title = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move_up_title);


        move_up_title.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                btn_play.setVisibility(View.VISIBLE);

                YoYo.with(Techniques.FadeIn).duration(400).playOn(btn_play);

                btn_play.setOnClickListener(MainActivity.this);


                main_setting_layout.setVisibility(View.VISIBLE);

                YoYo.with(Techniques.FadeIn).duration(400).playOn(main_setting_layout);

                btn_setting.setOnClickListener(MainActivity.this);
                btn_conquitas.setOnClickListener(MainActivity.this);
                btn_ranking.setOnClickListener(MainActivity.this);


                if (timer != null) {
                    timer.cancel();
                }
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                YoYo.with(Techniques.Tada)
                                        .duration(500)
                                        .playOn(traingle);
                            }
                        });
                    }
                }, 0, 3000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        move_up.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (dialog != null) {
                    dialog.dismiss();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }


    private void init() {

        btn_play = (RelativeLayout) findViewById(R.id.main_btn_play);
        btn_play.setOnClickListener(MainActivity.this);

        traingle = (ImageView) findViewById(R.id.main_traingle);

        btn_setting = (Button) findViewById(R.id.main_setting);
        btn_conquitas = (Button) findViewById(R.id.main_conquistas);
        btn_conquitas.setOnClickListener(this);
        btn_ranking = (Button) findViewById(R.id.main_ranking);
        btn_ranking.setOnClickListener(this);

        tv_how_to_play = (TextView) findViewById(R.id.tv_how_to_play);
        tv_how_to_play.setOnClickListener(this);

        btn_setting.setOnClickListener(MainActivity.this);
        if (AppController.getInstance().getPrefManger().getMusicOnOff()) {
            btn_setting.setBackgroundResource(R.drawable.sound_on);
        } else {
            btn_setting.setBackgroundResource(R.drawable.sound_off);
        }


        // traingle = (ImageView) findViewById(R.id.main_traingle);
        logo = (LinearLayout) findViewById(R.id.logo);

        //logo.startAnimation(move_up_title);

        main_setting_layout = (LinearLayout) findViewById(R.id.main_setting_layout);


    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (!flag_play_btn_already_clicked && !mSignInClicked) {


            if (AppController.getInstance().getPrefManger().getMusicOnOff() && mServ != null)
                mServ.start2("click", false);


            if (id == R.id.main_btn_play) {

                flag_play_btn_already_clicked = true;
                AppController.getInstance().getPrefManger().setLevelNum(1);

                Intent intent = new Intent(MainActivity.this, GamePlay.class);
                startActivity(intent);

            }

            if (id == R.id.main_setting) {
                soundOnOff();
            }
            if (id == R.id.tv_how_to_play) {
                startActivity(new Intent(MainActivity.this, HowToPlayActivity.class));
            }
            if (id == R.id.main_ranking) {


                if (!BaseGameUtils.verifySampleSetup(this, R.string.app_id,
                        R.string.leaderboard_highestscore)) {
                    // Log.w(TAG,
                    // "*** Warning: setup problems detected. Sign in may not work!");
                }

                // start the sign-in flow
                // mSignInClicked = true;
                // mGoogleApiClient.connect();
                if (!isNetworkAvailable()) {
                    BaseGameUtils.makeSimpleDialog(this, "No internet connection. Make sure that Wi-Fi or cellular mobile data is turned on, then try again.").show();
                    return;
                }
                if (!isSignedIn()) {
                   // BaseGameUtils.makeSimpleDialog(this,
                    //        getString(R.string.leaderboards_not_available)).show();
                    mSignInClicked = true;
                    flagCallFromShowLeaderboardBtn = true;
                    mGoogleApiClient.connect();
                    return;
                }


                // startActivityForResult(
                // Games.Leaderboards
                // .getAllLeaderboardsIntent(mGoogleApiClient),
                // RC_UNUSED);
                saveScoreToLeaderBoard(getString(R.string.leaderboard_highestscore));

                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(
                        mGoogleApiClient,
                        getString(R.string.leaderboard_highestscore)),
                        RC_UNUSED);

            }

            if (id == R.id.main_conquistas) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse("https://play.google.com/store/apps/details?id="
                                + AppConstant.APP_PNAME)));
            }
        }

    }

    private void soundOnOff() {
        if (AppController.getInstance().getPrefManger().getMusicOnOff()) {

            AppController.getInstance().getPrefManger().setMusicOnOff(false);
            flag_music = false;
            stopMusic();
            btn_setting.setBackgroundResource(R.drawable.sound_off);


        } else {
            AppController.getInstance().getPrefManger().setMusicOnOff(true);
            flag_music = true;
            playMusic("menu_page");
            btn_setting.setBackgroundResource(R.drawable.sound_on);
        }
    }


    private void playMusic(String music_name) {
        if(mServ != null){
            mServ.start(music_name, true);
        }
    }

    private void stopMusic() {
        if(mServ != null){
            mServ.stop();
            mServ.stop2();
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {


        mServ = ((MusicService.ServiceBinder) iBinder).getService();

        flag_music = AppController.getInstance().getPrefManger().getMusicOnOff();

        if (flag_music) {
            playMusic("menu_page");
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


        super.onBackPressed();
        if(mServ != null){
            mServ.stop();
            mServ.stop2();
        }
        doUnbindService();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (flag_music) {
            playMusic("menu_page");
        }
        flag_play_btn_already_clicked = false;

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        YoYo.with(Techniques.Tada)
                                .duration(500)
                                .playOn(traingle);
                    }
                });
            }
        }, 0, 3000);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
        if (AppController.getInstance().getPrefManger().getMusicOnOff() && mServ != null) {
            mServ.stop();
            mServ.stop2();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        doUnbindService();
    }

    public void doUnbindService() {
        // disconnects the service activity.
        if (mIsBound) {
            unbindService(this);
            mIsBound = false;
        }
    }











    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isSignedIn() {
        return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }

    public static void saveScoreToLeaderBoard(String leaderboard_id) {

        int high_score = Integer.parseInt(AppController.getInstance().getPrefManger().getHighScore());

        if (high_score != 0) {
            Games.Leaderboards.submitScore(mGoogleApiClient,
                    leaderboard_id, high_score);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        // TODO Auto-generated method stub
        // Log.d("DEBUG", "onConnectionFailed(): attempting to resolve");
        if (mResolvingConnectionFailure) {
            mSignInClicked = false;
            //Log.d("DEBUG", "onConnectionFailed(): already resolving");
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;
            if (!BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient,
                    connectionResult, RC_SIGN_IN,
                    getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }

    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
        //Log.d("DEBUG", "onConnected(): connected to Google APIs");
        mSignInClicked = false;

        if(flagCallFromShowLeaderboardBtn){
            flagCallFromShowLeaderboardBtn = false;
            saveScoreToLeaderBoard(getString(R.string.leaderboard_highestscore));

            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(
                    mGoogleApiClient,
                    getString(R.string.leaderboard_highestscore)),
                    RC_UNUSED);
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub
        // Log.d("DEBUG", "onConnectionSuspended(): attempting to connect");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(this, requestCode,
                        resultCode, R.string.signin_other_error);
            }
        }
    }
}
