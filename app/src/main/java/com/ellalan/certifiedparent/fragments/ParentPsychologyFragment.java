package com.ellalan.certifiedparent.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import androidx.core.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ellalan.certifiedparent.AppConstants;
import com.ellalan.certifiedparent.R;
import com.ellalan.certifiedparent.interfaces.LoadStatementInterface;
import com.ellalan.certifiedparent.model.CategoryStatements;
import com.ellalan.certifiedparent.util.Utils;


public class ParentPsychologyFragment extends Fragment {
    private TextView description;
    private ImageView imageView;
    CategoryStatements statement;
    private LoadStatementInterface mListener;
    AppCompatActivity callingActivity;
    Context context;

    public ParentPsychologyFragment() {
        // Required empty public constructor
    }

    public static ParentPsychologyFragment newInstance(CategoryStatements statement) {
        ParentPsychologyFragment fragment = new ParentPsychologyFragment();
        Bundle args = new Bundle();
        args.putSerializable("statement",statement);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Log.i("Log","ParentingPsychology fragment onCreateView Called");
        return inflater.inflate(R.layout.categories_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        description = (TextView) view.findViewById(R.id.description);
        imageView = (ImageView) view.findViewById(R.id.result_image);

        callingActivity.getSupportActionBar().setTitle("Parent Psychology");

        view.findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.LoadNextParentPsychologyStatement();
            }
        });

        view.findViewById(R.id.fabprevious).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.LoadPreviousParentPsychologyStatement();
            }
        });

        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(3).setChecked(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try{
            Glide.with(getContext()).load("file:///android_asset/images/" + statement.getIMAGE()).centerCrop().into(imageView);
            description.setText(statement.getQUOTE());
        }catch (Exception e){
            context.getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME,Context.MODE_PRIVATE).edit().putInt(Utils.PARENTPSYCHOLOGYPOSITION,0).apply();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            statement = (CategoryStatements) getArguments().getSerializable("statement");

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof AppCompatActivity) {
            callingActivity = (AppCompatActivity) context;
        }
        mListener = (LoadStatementInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}

