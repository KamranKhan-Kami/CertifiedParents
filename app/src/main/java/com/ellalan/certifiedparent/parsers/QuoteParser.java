package com.ellalan.certifiedparent.parsers;

import android.content.Context;

import com.ellalan.certifiedparent.model.Question;
import com.ellalan.certifiedparent.model.Quote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class QuoteParser {

    Context context;

    public QuoteParser(Context context) {
        this.context = context;
    }


    public Quote randomQuote() {
        Quote quote = null;
        Random random = new Random();
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset(context));
            int randomQuoteID = random.nextInt(jsonArray.length());
            quote = parseQuote(jsonArray.getJSONObject(randomQuoteID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return quote;
    }

    private Quote parseQuote(JSONObject root) {
        Quote quote = new Quote();
        try {
            quote.setQuote(root.getString("quote"));
            quote.setPerson(root.getString("person"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return quote;
    }


    public String loadJSONFromAsset(Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open("quotes.json");
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
