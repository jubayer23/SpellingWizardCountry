package com.smartysoft.wordpuzzlecountry.sharedPref;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.smartysoft.wordpuzzlecountry.appdata.AppConstant;


public class PrefManager {
    private static final String TAG = PrefManager.class.getSimpleName();


    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "com.creative.nagraam";

    // Google's username
    private static final String KEY_HIGHSCORE = "highscore";

    private static final String KEY_LEVEL_NUM = "level_num";

    private static final String KEY_LANGUAGE = "language";

    private static final String KEY_PU_REMAINING = "pu_remaining";

    private static final String KEY_MUSIC_ONOFF = "music_onoff";

    public static final String KEY_SOLVE_LETTER = "solve_letter";
    public static final String KEY_JUMP_LEVEL = "jump_level";
    public static final String KEY_ADD_TIME = "add_time";


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);

    }

    public void setLevelNum(int levelNum) {
        editor = pref.edit();

        editor.putInt(KEY_LEVEL_NUM, levelNum);

        // commit changes
        editor.commit();
    }

    public int getLevelNum() {
        return pref.getInt(KEY_LEVEL_NUM, 1);
    }

    public void setLanguage(int language) {
        editor = pref.edit();

        editor.putInt(KEY_LANGUAGE, language);

        // commit changes
        editor.commit();
    }

    public int getLanguage() {
        return pref.getInt(KEY_LANGUAGE, 0);
    }

    public void setPuRemaining(int value) {
        editor = pref.edit();

        editor.putInt(KEY_PU_REMAINING, value);

        // commit changes
        editor.commit();
    }

    public int getPuRemaining() {
        return pref.getInt(KEY_PU_REMAINING, AppConstant.powerUpLimitation);
    }

    public void setSolveletterEnable(boolean value) {
        editor = pref.edit();

        editor.putBoolean(KEY_SOLVE_LETTER, value);

        // commit changes
        editor.commit();
    }

    public boolean getSolveletterEnable() {
        return pref.getBoolean(KEY_SOLVE_LETTER, true);
    }

    public void setJumpLevelEnable(boolean value) {
        editor = pref.edit();

        editor.putBoolean(KEY_JUMP_LEVEL, value);

        // commit changes
        editor.commit();
    }

    public boolean getJumpLevelEnable() {
        return pref.getBoolean(KEY_JUMP_LEVEL, true);
    }

    public void setAddTimeEnable(boolean value) {
        editor = pref.edit();

        editor.putBoolean(KEY_ADD_TIME, value);

        // commit changes
        editor.commit();
    }

    public boolean getAddTimeEnable() {
        return pref.getBoolean(KEY_ADD_TIME, true);
    }


    public void setHighScore(String score) {
        editor = pref.edit();

        editor.putString(KEY_HIGHSCORE, score);

        // commit changes
        editor.commit();
    }

    public String getHighScore() {
        return pref.getString(KEY_HIGHSCORE, "0");
    }


    public void setMusicOnOff(boolean value) {
        editor = pref.edit();

        editor.putBoolean(KEY_MUSIC_ONOFF, value);

        // commit changes
        editor.commit();
    }

    public boolean getMusicOnOff() {
        return pref.getBoolean(KEY_MUSIC_ONOFF, true);
    }


}