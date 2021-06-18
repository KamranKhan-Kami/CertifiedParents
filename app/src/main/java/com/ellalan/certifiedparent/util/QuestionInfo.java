package com.ellalan.certifiedparent.util;

import android.content.Context;
import android.util.Log;

import com.ellalan.certifiedparent.model.Question;
import com.ellalan.certifiedparent.parsers.QuestionInfoParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class QuestionInfo {

    //private static final String feedURL = "file:///android_asset/CourseInfo.xml";
    private static Context thisCtx;
    private static QuestionInfo ourInstance; // = new BKCourseInfo();
    protected static ArrayList<Question> allCourses = new ArrayList<>();

    public static QuestionInfo getInstance(Context ctx) {

        thisCtx = ctx;
        if( ourInstance == null){

            ourInstance = new QuestionInfo();
        }
        return ourInstance;

    }

    private QuestionInfo() {

        try {

            // Load Course info from Asset File
            InputStream cFile = null;
            QuestionInfoParser cParser = new QuestionInfoParser();

            cFile = thisCtx.getAssets().open("Questions.xml");
            if( cFile != null ){

                allCourses = cParser.parse(cFile);
            }

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
        catch (IOException | XmlPullParserException e){
            Log.d("CourseInfoTask", e.getLocalizedMessage());
        }

    }

    public ArrayList<Question> getAllCourses(){

        return allCourses;
    }

}
