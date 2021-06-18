package com.ellalan.certifiedparent.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import androidx.core.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ellalan.certifiedparent.Cons;
import com.ellalan.certifiedparent.R;
import com.ellalan.certifiedparent.interfaces.WeeklyQuizInterface;
import com.ellalan.certifiedparent.model.Question;

public class WeeklyQuizResultFragment extends Fragment {
    private Question question;
    private boolean status;
    //    private ImageView result_image;
    private TextView description, answer_title;
    private WeeklyQuizInterface mListener;
    //    private FloatingActionButton fab_result;
    AppCompatActivity callingActivity;
    Context context;

    public WeeklyQuizResultFragment() {
        // Required empty public constructor
    }

    public static WeeklyQuizResultFragment newInstance(Question question, boolean status) {
        WeeklyQuizResultFragment fragment = new WeeklyQuizResultFragment();
        Bundle args = new Bundle();
        args.putSerializable(Cons.QUESTION, question);
        args.putBoolean(Cons.STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(Cons.QUESTION);
            status = getArguments().getBoolean(Cons.STATUS, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListener.UpdateWeeklyQuizResult(status);

        description = (TextView) view.findViewById(R.id.description);
        answer_title = (TextView) view.findViewById(R.id.answer_title);

        if (status) {
            callingActivity.getSupportActionBar().setTitle("Your answer is correct");
        } else {
            callingActivity.getSupportActionBar().setTitle("More desirable answer");
        }
        view.findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.LoadQuestion();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Glide.with(getContext()).load(status ? question.getCorrect_image_resource() : question.getWrong_image_resource()).into(result_image);
//        Glide.with(getContext()).load(status ? R.drawable.correct_image_resource : R.drawable.wrong_image_resource).into(result_image);
        description.setText(question.getD());
        answer_title.setText(question.getT().toUpperCase());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof AppCompatActivity) {
            callingActivity = (AppCompatActivity) context;
        }
        mListener = (WeeklyQuizInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
