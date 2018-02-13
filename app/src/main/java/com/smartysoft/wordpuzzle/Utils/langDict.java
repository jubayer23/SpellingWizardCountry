package com.smartysoft.wordpuzzle.Utils;

import com.smartysoft.wordpuzzle.appdata.AppConstant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/*
 * This could be implemented with a SQLite db instead
 * but allow it.
 */
public class langDict {

    private  Set<String> wordSet;

    Set<String> wordSet_length_3;
    Set<String> wordSet_length_4;
    Set<String> wordSet_length_5;
    Set<String> wordSet_length_6;
    Set<String> wordSet_length_7;
    Set<String> wordSet_length_8;
    Set<String> wordSet_length_9;
    Set<String> wordSet_length_10;

    public langDict() {
        wordSet = new TreeSet<String>();
        wordSet_length_3 =  new TreeSet<String>();
        wordSet_length_4 =  new TreeSet<String>();
        wordSet_length_5 =  new TreeSet<String>();
        wordSet_length_6 =  new TreeSet<String>();
        wordSet_length_7 =  new TreeSet<String>();
        wordSet_length_8 =  new TreeSet<String>();
        wordSet_length_9 =  new TreeSet<String>();
        wordSet_length_10 =  new TreeSet<String>();

    }

    public boolean readDict(InputStream I) {
        wordSet.clear();
        wordSet_length_3.clear();
        wordSet_length_4.clear();
        wordSet_length_5.clear();
        wordSet_length_6.clear();
        wordSet_length_7.clear();
        wordSet_length_8.clear();
        wordSet_length_9.clear();
        wordSet_length_10.clear();

        Reader iR = new InputStreamReader(I, Charset.forName("ISO-8859-1"));
        BufferedReader R = new BufferedReader(iR);
        String line;

        try {
            while ((line = R.readLine()) != null) {



                String s[] = line.split(",");

                if(s.length>1)
                {
                    ArrayList<String> temp = new ArrayList<String>();
                    for(int i=0;i<s.length;i++)
                    {
                        temp.add(s[i]);
                    }
                    for(int i=0;i<s.length;i++)
                    {
                        AppConstant.similarWordMap.put(s[i],temp);
                    }

                }








                wordSet.add(line);

                if (s[0].length() == 3) {
                    wordSet_length_3.add(s[0]);
                } else if (s[0].length() == 4) {
                    wordSet_length_4.add(s[0]);
                } else if (s[0].length() == 5) {
                    wordSet_length_5.add(s[0]);
                } else if (s[0].length() == 6) {
                    wordSet_length_6.add(s[0]);
                }else if (s[0].length() == 7) {
                    wordSet_length_7.add(s[0]);
                }else if (s[0].length() == 8) {
                    wordSet_length_8.add(s[0]);
                }else if (s[0].length() == 9) {
                    wordSet_length_9.add(s[0]);
                }else if (s[0].length() == 10) {
                    wordSet_length_10.add(s[0]);
                }

            }

            AppConstant.word_length_3.clear();
            AppConstant.word_length_3.addAll(wordSet_length_3);
            AppConstant.word_length_4.clear();
            AppConstant.word_length_4.addAll(wordSet_length_4);
            AppConstant.word_length_5.clear();
            AppConstant.word_length_5.addAll(wordSet_length_5);
            AppConstant.word_length_6.clear();
            AppConstant.word_length_6.addAll(wordSet_length_6);

            AppConstant.word_length_7.clear();
            AppConstant.word_length_7.addAll(wordSet_length_7);
            AppConstant.word_length_8.clear();
            AppConstant.word_length_8.addAll(wordSet_length_8);
            AppConstant.word_length_9.clear();
            AppConstant.word_length_9.addAll(wordSet_length_9);
            AppConstant.word_length_10.clear();
            AppConstant.word_length_10.addAll(wordSet_length_10);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean isWord(String s) {
        return wordSet.contains(s.toLowerCase());
    }

    public Set<String> getWordList() {
        return wordSet;
    }
}