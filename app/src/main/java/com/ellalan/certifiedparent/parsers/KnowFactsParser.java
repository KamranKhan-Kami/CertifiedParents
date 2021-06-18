package com.ellalan.certifiedparent.parsers;

import android.content.Context;
import android.util.Log;

import com.ellalan.certifiedparent.model.CategoryStatements;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class KnowFactsParser {
    Context context;

    public KnowFactsParser(Context context) {
        this.context = context;
    }

    public CategoryStatements getCategoryStatement(int id) {
        CategoryStatements quote;
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset(context));
            JSONObject root = jsonArray.getJSONObject(id);
            quote = parseStatement(root);
            Log.i("Log","KnowFactsParser:"+quote+"");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return quote;
    }



    public String loadJSONFromAsset(Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open("doyouknowfacts.json");
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

    public int getKnowFactsStatementNumbers() {
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
