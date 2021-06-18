package com.ellalan.certifiedparent.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.core.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ellalan.certifiedparent.AppConstants;
import com.ellalan.certifiedparent.R;
import com.ellalan.certifiedparent.parsers.FactParser;

public class LockFragment extends Fragment {

    TextView fact;
    SharedPreferences sharedPreferences;
    int factPos;

    public LockFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_lock, container, false);
        fact = (TextView) root.findViewById(R.id.fact);

        sharedPreferences = getContext().getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, Context.MODE_PRIVATE);
        factPos = sharedPreferences.getInt(AppConstants.PREF_ADDITIONAL_FACT_POSITION, 0);
        fact.setText(new FactParser(getContext()).loadFact(factPos));
        factPos++;
        if (factPos > 99) {
            factPos = 0;
        }
        sharedPreferences.edit().putInt(AppConstants.PREF_ADDITIONAL_FACT_POSITION, factPos).apply();
        return root;
    }


}
