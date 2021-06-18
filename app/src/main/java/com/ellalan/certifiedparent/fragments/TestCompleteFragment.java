package com.ellalan.certifiedparent.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.core.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ellalan.certifiedparent.R;
import com.ellalan.certifiedparent.interfaces.TestCompleteInterface;


public class TestCompleteFragment extends Fragment {

    private TestCompleteInterface testCompleteInterface;

    public TestCompleteFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_test_complete, container, false);
        Button button = (Button) root.findViewById(R.id.continueToCertificate);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testCompleteInterface.continueToCertificate();

            }
        });
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        testCompleteInterface = (TestCompleteInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        testCompleteInterface = null;
    }
}
