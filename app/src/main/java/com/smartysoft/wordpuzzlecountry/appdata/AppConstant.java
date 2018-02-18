package com.smartysoft.wordpuzzlecountry.appdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by comsol on 10/27/2015.
 */

public class AppConstant {


    public static final int LANGUAGE_ENG = 0;

    public static final int LANGUAGE_POR = 1;

    public static int LANGUAGE = LANGUAGE_ENG;

    public static int GAMEOVER_COUNTER = 0;

    public final static String APP_PNAME = "com.smartysoft.wordpuzzle";// Package


    public static List<String> word_length_3 = new ArrayList<String>();

    public static List<String> word_length_4 = new ArrayList<String>();

    public static List<String> word_length_5 = new ArrayList<String>();


    public static List<String> word_length_6 = new ArrayList<String>();
    public static List<String> word_length_7 = new ArrayList<String>();
    public static List<String> word_length_8 = new ArrayList<String>();
    public static List<String> word_length_9 = new ArrayList<String>();
    public static List<String> word_length_10 = new ArrayList<String>();

    public static HashMap<String, ArrayList<String>> similarWordMap = new HashMap<String,  ArrayList<String>>();


    public static int[] levelCircleLimit =
            {
                    0,
                    5,
                    10,
                    10,
                    10,
                    10,
                    10,
                    10,
                    10
            };

    public static String[] complementList_eng =
            {
                    "correct",
                    "easy",
                    "good",
                    "is_this",
                    "nice",
                    "right"
            };

    public static String[][] complementList =
            {
                    {
                            "correct",
                            "easy",
                            "good",
                            "is_this",
                            "nice",
                            "right"
                    },
                    {
                            "correto",
                            "boa",
                            "facil",
                            "incrivel",
                            "isso_ai",
                            "otimo"
                    }
            };


    public static String[] wordFileName =
            {
                    "word_english",
                    "word_por"
            };

    public   static int powerUpCircle = 5;

    public  static final int powerUpLimitation = 3;


}
