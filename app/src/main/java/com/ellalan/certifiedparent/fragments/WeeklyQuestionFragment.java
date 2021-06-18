package com.ellalan.certifiedparent.fragments;


import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import androidx.core.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ellalan.certifiedparent.AppConstants;
import com.ellalan.certifiedparent.Cons;
import com.ellalan.certifiedparent.R;
import com.ellalan.certifiedparent.interfaces.WeeklyQuizInterface;
import com.ellalan.certifiedparent.model.Answer;
import com.ellalan.certifiedparent.model.Question;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;


public class WeeklyQuestionFragment extends Fragment {

    private Question question;
    private ImageView question_image;
    private TextView question_text_view, option_a, option_b, option_c, answer_label;
    private LinearLayout option_a_card, option_b_card, option_c_card;
    private DatabaseReference mDatabase;
    private Answer answer;
    private MediaPlayer mPlayer;
    private WeeklyQuizInterface mListener;
    private FloatingActionButton music_button;
    private boolean play;




    public WeeklyQuestionFragment() {
        // Required empty public constructor
    }

    public static WeeklyQuestionFragment newInstance(Question question) {
        WeeklyQuestionFragment fragment = new WeeklyQuestionFragment();

        Bundle args = new Bundle();
        args.putSerializable(Cons.QUESTION, question);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_question, container, false);
        music_button = (FloatingActionButton) root.findViewById(R.id.music_button);
        music_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mPlayer.isPlaying()){
                    stopMusic();
                    getContext().getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE)
                            .edit().putBoolean(AppConstants.PREF_PLAY_MUSIC, false).apply();
                    music_button.setImageResource(R.drawable.ic_volume_off_black_24dp);
                }else {
                    startMusic();
                    getContext().getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE)
                            .edit().putBoolean(AppConstants.PREF_PLAY_MUSIC, true).apply();
                    music_button.setImageResource(R.drawable.ic_volume_up_black_24dp);
                }

            }
        });

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        question_image = (ImageView) view.findViewById(R.id.question_image);
        question_text_view = (TextView) view.findViewById(R.id.question);
        answer_label = (TextView) view.findViewById(R.id.answer_label);
        answer_label.setVisibility(View.GONE);
        option_a = (TextView) view.findViewById(R.id.option_a);
        option_b = (TextView) view.findViewById(R.id.option_b);
        option_c = (TextView) view.findViewById(R.id.option_c);
        option_a_card = (LinearLayout) view.findViewById(R.id.option_a_layout);
        option_b_card = (LinearLayout) view.findViewById(R.id.option_b_layout);
        option_c_card = (LinearLayout) view.findViewById(R.id.option_c_layout);

//        callingActivity.getSupportActionBar().setTitle("Weekly Quiz");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable(Cons.QUESTION);
            mDatabase = FirebaseDatabase.getInstance().getReference("answers");
        }
        mPlayer = MediaPlayer.create(getContext(), R.raw.bg_music);
        mPlayer.setLooping(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Glide.with(getContext()).load("file:///android_asset/images/" + question.getI()).centerCrop().into(question_image);
        question_text_view.setText(question.getQ());
        option_a.setText(question.getA());
        option_b.setText(question.getB());
        option_c.setText(question.getC());

        option_a_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = question.getR().equalsIgnoreCase("A");
                option_a_card.setBackgroundColor(ContextCompat.getColor(getContext(), result ? R.color.green_500 : R.color.red_500));
                MoveNext(result);
            }
        });

        option_b_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = question.getR().equalsIgnoreCase("B");
                option_b_card.setBackgroundColor(ContextCompat.getColor(getContext(), result ? R.color.green_500 : R.color.red_500));
                MoveNext(result);
            }
        });

        option_c_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = question.getR().equalsIgnoreCase("C");
                option_c_card.setBackgroundColor(ContextCompat.getColor(getContext(), result ? R.color.green_500 : R.color.red_500));
                MoveNext(result);
            }
        });

        GetAnswerPercentage(question.getI().replace(".jpg", ""));
    }

    @Override
    public void onResume() {
        super.onResume();

        play = getContext().getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(AppConstants.PREF_PLAY_MUSIC, true);
        if (play){
            music_button.setImageResource(R.drawable.ic_volume_up_black_24dp);
            startMusic();
        } else {
            music_button.setImageResource(R.drawable.ic_volume_off_black_24dp);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
//        if (mPlayer != null && mPlayer.isPlaying()){
//            mPlayer.stop();
//        }
        stopMusic();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mListener = (WeeklyQuizInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stopMusic();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
        }
    }


    private void MoveNext(boolean status) {
//        ((HomeActivity)getActivity()).MoveToAnswer(status);
        mListener.LoadAnswer(status);
        if (answer != null) {
            answer.setT(answer.getT() + 1);
            answer.setA(answer.getA() + (status ? 1 : 0));
        } else {
            answer = new Answer();
            answer.setT(1);
            answer.setA(status ? 1 : 0);
        }
        mDatabase.child(question.getI().replace(".jpg", "")).setValue(answer);
    }

    private void GetAnswerPercentage(String name) {
        DatabaseReference ref = mDatabase.child(name);
        // This fires when the servlet first runs, returning all the existing values
        // only runs once, until the servlet starts up again.
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                answer = dataSnapshot.getValue(Answer.class);
                if (answer != null) {
                    /*answer_label.setText(String.format(Locale.ENGLISH,"%s out %s are correctly answered",answer.getA(),answer.getT()));
                    answer_label.setVisibility(View.VISIBLE);*/
                    System.out.println(String.format(Locale.ENGLISH, "%s out %s are correctly answered", answer.getA(), answer.getT()));
                    int percentage = answer.getA() * 100 / answer.getT();
                    answer_label.setText(percentage + " % of users have answered this question correctly");
                    answer_label.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("Error: " + error);
            }
        });
    }

    private void startMusic() {
        if (!mPlayer.isPlaying())
            mPlayer.start();
    }

    private void stopMusic() {
        if (mPlayer.isPlaying())
            mPlayer.pause();
    }
}
