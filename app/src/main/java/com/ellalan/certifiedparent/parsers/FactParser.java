package com.ellalan.certifiedparent.parsers;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class FactParser {

    Context context;

    public FactParser(Context context) {
        this.context = context;
    }


    public String loadFact(int id) {
        String fact = null;
        Random random = new Random();
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset(context));
            fact = jsonArray.getString(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return fact;
    }

    public String loadJSONFromAsset(Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open("facts.json");
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
