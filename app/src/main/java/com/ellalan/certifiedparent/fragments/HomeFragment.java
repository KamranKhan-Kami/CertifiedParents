package com.ellalan.certifiedparent.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import androidx.core.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ellalan.certifiedparent.AppConstants;
import com.ellalan.certifiedparent.R;
import com.ellalan.certifiedparent.interfaces.LoadStatementInterface;
import com.ellalan.certifiedparent.interfaces.WeeklyQuizInterface;
import com.ellalan.certifiedparent.parsers.QuestionParser;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static com.google.android.gms.internal.zzagy.runOnUiThread;

public class HomeFragment extends Fragment {

    @BindView(R.id.rlchildpsychology)
    RelativeLayout rlchildpsychology;
    @BindView(R.id.rlparentpsychology)
    RelativeLayout rlparentpsyshology;
    @BindView(R.id.rlfacts)
    RelativeLayout rlfacts;
    @BindView(R.id.rlparentingtips)
    RelativeLayout rlparentingtips;
    @BindView(R.id.header)
    TextView header;
    @BindView(R.id.footer)
    TextView footer;
    @BindView(R.id.ch)
    Chronometer chron;
    @BindView(R.id.wch)
    Chronometer wchron;
    @BindView(R.id.btnchallenge)
    Button btnchallenge;
    @BindView(R.id.btntryourapp)
    Button btntryourapp;

    private SharedPreferences sharedPreferences;
    private SharedPreferences wsharedPreferences;
    private LoadStatementInterface mListener;
    private WeeklyQuizInterface mwinterface;

    long quiztime;
    AppCompatActivity callingActivity;
    Context context;

    QuestionParser questionParser;
    public static boolean status = true;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this,view);
        questionParser = new QuestionParser(getActivity());
        wsharedPreferences =getActivity().getSharedPreferences(AppConstants.PREF_WEEKLY, MODE_PRIVATE);
        context = getContext();
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(3000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (status){
                                    btntryourapp.setVisibility(View.GONE);
                                    btnchallenge.setVisibility(View.VISIBLE);
                                    status = false;
                                }else {
                                    btntryourapp.setVisibility(View.VISIBLE);
                                    btnchallenge.setVisibility(View.GONE);
                                    status = true;
                                }

                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

        btntryourapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.handikapp.parentbox&hl=en")));
            }
        });



        rlchildpsychology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Log","rlchildpsychology button clicked...");
                //getFragmentManager().beginTransaction().replace(R.id.main_container,new ChildPsychologyFragment()).commitAllowingStateLoss();
                mListener.LoadNextChildPsychologyStatement();
            }
        });

        rlparentingtips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Log","rlparentingtips button clicked...");
                mListener.LoadNextParentingTipsStatement();
            }
        });

        rlparentpsyshology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Log","rlparentpsychology button clicked...");
                mListener.LoadNextParentPsychologyStatement();
            }
        });

        rlfacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Log","rlknowfacts button clicked...");
                mListener.LoadNextKnowFactsStatement();
            }
        });

        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mwinterface.LoadQuestion();
            }
        });

        btnchallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mwinterface.ChallengeAParent();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences =getActivity().getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, MODE_PRIVATE);
//        editor = sharedPreferences.edit();

        callingActivity.getSupportActionBar().setTitle("Parenting Challenge");


        Log.i("Log","ValueRecieved: "+quiztime+"");

        chron.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                quiztime = sharedPreferences.getLong(AppConstants.PREF_QUIZ_COMPLETION_TIME,0);

                Calendar cl = Calendar.getInstance();
                long currentMillis = System.currentTimeMillis();
                long millidiff = 0;
                if (quiztime  > currentMillis){
                    millidiff = quiztime - currentMillis;
                }else {
                    chron.stop();
                   callingActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.main_container, QuestionFragment.newInstance(questionParser.getQuestion(sharedPreferences.getInt(AppConstants.PREF_ADDITIONAL_LAST_QUESTION_POS, 10))))
                            .commitAllowingStateLoss();
                    if (mListener!=null){
                        mListener.ChangeHomeFragment();
                    }



                    SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, Context.MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean(AppConstants.PREF_ADDITIONAL_LOCKED, false).apply();
                    sharedPreferences.edit().putLong(AppConstants.PREF_ADDITIONAL_NOTIFICATION_TIME, 0).apply();
                }

                cl.setTimeInMillis(millidiff);  //here your time in miliseconds

                long seconds = millidiff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                long months = days / 30;



                long month = months % 12;
                long day = days % 30;
                long hour = hours % 24;
                long minute = minutes % 60;
                long sec = seconds % 60;
                long years =  months / 12;


                //+(day<10?"0"+day:day)+" days "+
                header.setText("You have "+(hour<10?"0"+hour:hour)+":"+(minute<10?"0"+minute : minute)+":"+(sec<10?"0"+sec:sec)+" left for \n next daily quiz");

            }
        });

            chron.start();
        startWChron();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof AppCompatActivity) {
            callingActivity = (AppCompatActivity) context;
        }
        mListener = (LoadStatementInterface) context;
        mwinterface = (WeeklyQuizInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mwinterface = null;
    }


    void startWChron(){
        boolean firstlaunch = wsharedPreferences.getBoolean("firstlaunch",true);
        final long time= System.currentTimeMillis();

        if (firstlaunch){
            wsharedPreferences.edit().putLong(AppConstants.WEEKLY_QUIZ_NOTIFICATIONTIME,time + (86400000*7)).apply();
            wsharedPreferences.edit().putBoolean("firstlaunch",false).apply();
        }





        wchron.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {

//                long nextquiztime = ;
                long wquiztime = wsharedPreferences.getLong(AppConstants.WEEKLY_QUIZ_NOTIFICATIONTIME,time + (86400000*7));
                Calendar cl = Calendar.getInstance();
                long currentMillis = System.currentTimeMillis();
                long millidiff = 0;
                if (wquiztime  > currentMillis){
                    millidiff = wquiztime - currentMillis;
                    cl.setTimeInMillis(millidiff);  //here your time in miliseconds

                    long seconds = millidiff / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    long days = hours / 24;
                    long months = days / 30;



                    long month = months % 12;
                    long day = days % 30;
                    long hour = hours % 24;
                    long minute = minutes % 60;
                    long sec = seconds % 60;
                    long years =  months / 12;

                    long d = day+1;

                    footer.setText("0"+d+" days"+" for next weekly revision quiz");
                }else {

                   // SharedPreferences sharedPreferences = getContext().getSharedPreferences(AppConstants.PREF_WEEKLY, Context.MODE_PRIVATE);
                    wsharedPreferences.edit().putBoolean(AppConstants.WEEKLY_QUIZ_COMPLETED, false).apply();
                    wsharedPreferences.edit().putLong(AppConstants.WEEKLY_QUIZ_NOTIFICATIONTIME, 0).apply();
                    footer.setText("Your weekly quiz is ready..");
                }



            }
        });

        wchron.start();

    }

}
