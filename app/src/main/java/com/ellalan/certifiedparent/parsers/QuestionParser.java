package com.ellalan.certifiedparent.parsers;

import android.content.Context;

import com.ellalan.certifiedparent.model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class QuestionParser {

    private Context context;


    public QuestionParser(Context context) {
        this.context = context;
    }


    public Question getQuestion(int id) {
        Question questionObj;
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset(context));
            JSONObject root = jsonArray.getJSONObject(id);
            questionObj = parseQuestion(root);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return questionObj;
    }



    public int getQuestionNumbers() {
        int n;
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset(context));
            n = jsonArray.length();
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
        return n;
    }

    private Question parseQuestion(JSONObject root) {
        Question questionObj = new Question();
        try {
            String Q = root.getString("Q");
            String A = root.getString("A");
            String B = root.getString("B");
            String C = root.getString("C");
            String D = root.getString("D");
            String R = root.getString("R");
            String I = root.getString("I");
            String T = root.getString("T");

            questionObj.setQ(Q);
            questionObj.setA(A);
            questionObj.setB(B);
            questionObj.setC(C);
            questionObj.setD(D);
            questionObj.setR(R);
            questionObj.setI(I);
            questionObj.setT(T);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return questionObj;
    }

    public String loadJSONFromAsset(Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open("questions.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
