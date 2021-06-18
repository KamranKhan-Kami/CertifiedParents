package com.ellalan.certifiedparent.parsers;

import android.content.Context;

import com.ellalan.certifiedparent.model.CategoryStatements;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;



public class ChildPsychologyStatementParser {
    Context context;

    public ChildPsychologyStatementParser(Context context) {
        this.context = context;
    }

    public CategoryStatements getCategoryStatement(int id) {
        CategoryStatements quote;
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset(context));
            JSONObject root = jsonArray.getJSONObject(id);
            quote = parseStatement(root);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return quote;
    }



    public String loadJSONFromAsset(Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open("childpsychology.json");
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

    private CategoryStatements parseStatement(JSONObject root) {
        CategoryStatements statements = new CategoryStatements();
        try {
            String Q = root.getString("quote");

            String I = root.getString("image");

            statements.setQUOTE(Q);
            statements.setIMAGE(I);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return statements;
    }

    public int getChildPsychologyStatementNumbers() {
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
}
