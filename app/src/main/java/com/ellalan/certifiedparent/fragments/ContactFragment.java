package com.ellalan.certifiedparent.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import androidx.core.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ellalan.certifiedparent.R;


public class ContactFragment extends Fragment {
    public ContactFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contact, container, false);

        return root;
    }
}
