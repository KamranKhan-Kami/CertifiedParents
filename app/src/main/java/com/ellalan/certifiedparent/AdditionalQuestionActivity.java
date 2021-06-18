package com.ellalan.certifiedparent;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ellalan.certifiedparent.fragments.AboutFragment;
import com.ellalan.certifiedparent.fragments.ChildPsychologyFragment;
import com.ellalan.certifiedparent.fragments.ContactFragment;
import com.ellalan.certifiedparent.fragments.DoYouKnowFactsFragment;
import com.ellalan.certifiedparent.fragments.HomeFragment;
import com.ellalan.certifiedparent.fragments.ParentPsychologyFragment;
import com.ellalan.certifiedparent.fragments.ParentingTipsFragment;
import com.ellalan.certifiedparent.fragments.QuestionFragment;
import com.ellalan.certifiedparent.fragments.ResultFragment;
import com.ellalan.certifiedparent.fragments.WeeklyQuestionFragment;
import com.ellalan.certifiedparent.fragments.WeeklyQuizCompleteFragment;
import com.ellalan.certifiedparent.fragments.WeeklyQuizResultFragment;
import com.ellalan.certifiedparent.interfaces.LoadStatementInterface;
import com.ellalan.certifiedparent.interfaces.MainInterface;
import com.ellalan.certifiedparent.interfaces.QuestionInterface;
import com.ellalan.certifiedparent.interfaces.TestCompleteInterface;
import com.ellalan.certifiedparent.interfaces.WeeklyQuizInterface;
import com.ellalan.certifiedparent.parsers.ChildPsychologyStatementParser;
import com.ellalan.certifiedparent.parsers.KnowFactsParser;
import com.ellalan.certifiedparent.parsers.ParentPsychologyParser;
import com.ellalan.certifiedparent.parsers.ParentingTipsParser;
import com.ellalan.certifiedparent.parsers.QuestionParser;
import com.ellalan.certifiedparent.util.AppPermissions;
import com.ellalan.certifiedparent.util.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Random;

import static com.ellalan.certifiedparent.util.AppPermissions.PERMISSIONS_CODE;

public class AdditionalQuestionActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, MainInterface, QuestionInterface,
        TestCompleteInterface, LoadStatementInterface, WeeklyQuizInterface {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SharedPreferences wsharedPreferences;
    private SharedPreferences.Editor weditor;
    private int lastQuestionPosition = 10;
    private boolean isQuestion = true;
    private boolean answerCorrect = false;
    private QuestionParser questionParser;
    private boolean questionLock;

    private ProgressBar progressBar;
    private TextView progress_percentage;
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    NavigationView navigationView;
    private LinearLayout progress_container;
    private boolean adShown = false;

    private int currentFragmentID;
    private int currentHomeFragmentID;



    private int totalScore = 0;
    private int quizquestioncount;

    int i1;//for select random number of q..
    AdView adView;
    InterstitialAd mInterstitialAd;
    AdRequest adIRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Log.i("Log", "AdditionalQuestion Activity started...");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceId = md5(android_id).toUpperCase();
        Log.i("device id=",deviceId);

        // Load an ad into the AdMob banner view.
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("F543557A297F313071CFDCBA1CDF7FC7")
                .addTestDevice("5900ABC73E0193C5C3BCDA48AF95F170")
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        adIRequest = new AdRequest.Builder()
                .addTestDevice("F543557A297F313071CFDCBA1CDF7FC7")
                .addTestDevice("5900ABC73E0193C5C3BCDA48AF95F170")
                .build();

        wsharedPreferences = this.getSharedPreferences(AppConstants.PREF_WEEKLY, MODE_PRIVATE);

        questionParser = new QuestionParser(this);

        this.getSupportActionBar().setTitle("Parenting challenge");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        progressBar = (ProgressBar) findViewById(R.id.mf_progress_bar);
        progressBar.setMax(10);
        progress_container = (LinearLayout) findViewById(R.id.progress_container);
        progress_percentage = (TextView) findViewById(R.id.progress_percentage);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        currentFragmentID = R.id.nav_home;

        progressBar = (ProgressBar) findViewById(R.id.mf_progress_bar);
        progressBar.setVisibility(View.GONE);
        progress_container.setVisibility(View.GONE);


        sharedPreferences = this.getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, MODE_PRIVATE);

        quizquestioncount = sharedPreferences.getInt(AppConstants.QUIZQUESTIONCOUNT, 11);

        editor = sharedPreferences.edit();

        lastQuestionPosition = sharedPreferences.getInt(AppConstants.PREF_ADDITIONAL_LAST_QUESTION_POS, 10);
        totalScore = sharedPreferences.getInt(AppConstants.PREF_CURRENT_QUIZ_SCORE, 0);

        questionLock = sharedPreferences.getBoolean(AppConstants.PREF_ADDITIONAL_LOCKED, false);
        Log.i("Log", questionLock + "...");


        if (questionLock) {
            currentHomeFragmentID = AppConstants.HOME_FRAGMENT_LOCK;
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.main_container, new HomeFragment()).commitAllowingStateLoss();
        } else {
            if ((lastQuestionPosition % 10) == 0) {
                progressBar.setVisibility(View.GONE);
                progress_container.setVisibility(View.GONE);
            } else {
                progressBar.setProgress(lastQuestionPosition % 10);
                progress_percentage.setText(lastQuestionPosition % 10 + "%");
            }

            currentHomeFragmentID = AppConstants.HOME_FRAGMENT_QUESTION;
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.main_container, QuestionFragment.newInstance(questionParser.getQuestion(lastQuestionPosition)))
                    .commitAllowingStateLoss();
        }


        if (savedInstanceState != null) {
            currentHomeFragmentID = savedInstanceState.getInt("home_fragment_id");
            resetHomeFragment();
        }

        if (!AppPermissions.hasPermissions(AdditionalQuestionActivity.this, AppPermissions.PERMISSIONS)) {
            ActivityCompat.requestPermissions(AdditionalQuestionActivity.this, AppPermissions.PERMISSIONS, PERMISSIONS_CODE);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
        }
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }else {
            Log.i("Log"," Interstitial ads not loaded..!");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("home_fragment_id", currentHomeFragmentID);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }




    @Override
    public void LoadNext() {
        this.getSupportActionBar().setTitle("Parenting challenge");
        isQuestion = false;

        Log.i("Log", " LastQuestionPosition:" + lastQuestionPosition + " and quizquestioncount:" + quizquestioncount);
//        if (sharedPreferences.getBoolean(AppConstants.PREF_ADDITIONAL_LOCKED, false)) {
//            currentHomeFragmentID = AppConstants.HOME_FRAGMENT_LOCK;
//            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
//                    .replace(R.id.main_container, new HomeFragment()).commitAllowingStateLoss();
//        } else {
//        if (lastQuestionPosition+1 < quizquestioncount) {
//            currentHomeFragmentID = AppConstants.HOME_FRAGMENT_QUESTION;
//            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_container,
//                    QuestionFragment.newInstance(questionParser.getQuestion(lastQuestionPosition))
//            ).commitAllowingStateLoss();
//        } else {
        currentHomeFragmentID = AppConstants.HOME_FRAGMENT_LOCK;
        editor.putBoolean(AppConstants.PREF_TEST_COMPLETED, true);

        if ((lastQuestionPosition + 1) < 76) {
            getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, MODE_PRIVATE).edit().putInt(AppConstants.PREF_LAST_QUESTION_POS, lastQuestionPosition + 1).apply();
            //   getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, MODE_PRIVATE).edit().putInt(AppConstants.PREF_CURRENT_QUIZ_SCORE, 0).apply();
            // getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, MODE_PRIVATE).edit().putInt(AppConstants.PREF_TOTAL_SCORE, totalScore).apply();
        } else {
            getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, MODE_PRIVATE).edit().putInt(AppConstants.PREF_LAST_QUESTION_POS, 0).apply();
        }


//        if ((quizquestioncount + 1) < 76) {
//            getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, MODE_PRIVATE).edit().putInt(AppConstants.QUIZQUESTIONCOUNT, quizquestioncount + 1).apply();
//            getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, MODE_PRIVATE).edit().putInt(AppConstants.PREF_CURRENT_QUIZ_SCORE, 0).apply();
//            getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, MODE_PRIVATE).edit().putInt(AppConstants.PREF_TOTAL_SCORE, totalScore).apply();
//        } else {
//            getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, MODE_PRIVATE).edit().putInt(AppConstants.QUIZQUESTIONCOUNT,1).apply();
//            getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, MODE_PRIVATE).edit().putInt(AppConstants.PREF_ADDITIONAL_LAST_QUESTION_POS,0).apply();
//        }
        long time = System.currentTimeMillis();
        long nextquiztime = time + 86400000;

        Log.i("Log", "Next quiz SetTime" + nextquiztime);
        Log.i("Log", "TimeDifference:" + (nextquiztime - time));
        getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, MODE_PRIVATE)
                .edit().putLong(AppConstants.PREF_QUIZ_COMPLETION_TIME, time+nextquiztime).apply();
        editor.putBoolean(AppConstants.PREF_ADDITIONAL_LOCKED, true);

        editor.apply();

        //***************************For notification***********************************

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 1);

        Intent alarmIntent = new Intent(getApplicationContext(), CPBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        getApplicationContext().getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, MODE_PRIVATE)
                .edit().putLong(AppConstants.PREF_ADDITIONAL_NOTIFICATION_TIME, cal.getTimeInMillis()).apply();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                manager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
            } else {
                manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
            }
        } else {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        }

        getApplicationContext().getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, MODE_PRIVATE)
                .edit().putBoolean(AppConstants.PREF_ADDITIONAL_LOCKED, true).apply();

        //*******************Ending Notification***************************************

        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.main_container, new HomeFragment()).commitAllowingStateLoss();
        // }

        //}
    }

    @Override
    public void UpdateResult(Boolean status) {
        if (status) {
            totalScore += 10;
            sharedPreferences.edit().putInt(AppConstants.PREF_CURRENT_QUIZ_SCORE, totalScore).apply();
        }
    }

    @Override
    public void MoveToAnswer(boolean answerCorrect) {
//        this.answerCorrect = answerCorrect;
//        currentHomeFragmentID = AppConstants.HOME_FRAGMENT_RESULT;
//        isQuestion = false;
//        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
//                .replace(R.id.main_container, ResultFragment.newInstance(
//                        questionParser.getQuestion(lastQuestionPosition), answerCorrect)
//                )
//                .commitAllowingStateLoss();
//
//        if (lastQuestionPosition + 1 < questionParser.getQuestionNumbers()) {
//            lastQuestionPosition++;
//            editor.putInt(AppConstants.PREF_ADDITIONAL_LAST_QUESTION_POS, lastQuestionPosition);
//            editor.apply();
//        } else {
//            lastQuestionPosition = 0;
//            editor.putInt(AppConstants.PREF_ADDITIONAL_LAST_QUESTION_POS, lastQuestionPosition);
//            editor.apply();
//        }
//
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.HOUR, 24);
//
//        Intent alarmIntent = new Intent(this, CPBroadcastReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
//        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//        sharedPreferences.edit().putLong(AppConstants.PREF_ADDITIONAL_NOTIFICATION_TIME, cal.getTimeInMillis()).apply();
//
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                manager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//            } else {
//                manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//            }
//        } else {
//            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//        }
//
//
//        sharedPreferences.edit().putBoolean(AppConstants.PREF_ADDITIONAL_LOCKED, true).apply();
        this.answerCorrect = answerCorrect;
        isQuestion = false;

        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.main_container, ResultFragment.newInstance(new QuestionParser(this)
                        .getQuestion(lastQuestionPosition), answerCorrect)).commitAllowingStateLoss();

        //lastQuestionPosition++;
//        editor.putInt(AppConstants.PREF_LAST_QUESTION_POS, lastQuestionPosition);
//        editor.apply();
//
//        if (lastQuestionPosition  < quizquestioncount) {
//            lastQuestionPosition++;
//            editor.putInt(AppConstants.PREF_ADDITIONAL_LAST_QUESTION_POS, lastQuestionPosition);
//            editor.apply();
//        }
//


        //   updateProgress();
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (currentFragmentID == AppConstants.PARENT_PSYCHOLOGY_FRAGMENT || currentFragmentID == AppConstants.PARENTING_TIPS_FRAGMENTS || currentFragmentID == AppConstants.CHILD_PSYCHOLOGY_FRAGMENT || currentFragmentID == AppConstants.KNOW_FACTS_FRAGMENT) {
            resetHomeFragment();
        } else {
            super.onBackPressed();
        }
    }


    private void resetHomeFragment() {
        Log.i("Log", currentHomeFragmentID + "");

        if (currentHomeFragmentID == AppConstants.HOME_FRAGMENT_LOCK) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).
                    replace(R.id.main_container, new HomeFragment())
                    .commitAllowingStateLoss();
            currentFragmentID = currentHomeFragmentID;
        } else if (currentHomeFragmentID == AppConstants.HOME_FRAGMENT_QUESTION) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).
                    replace(R.id.main_container,
                            QuestionFragment.newInstance(questionParser.getQuestion(lastQuestionPosition)))
                    .commitAllowingStateLoss();
            currentFragmentID = currentHomeFragmentID;
        }

    }



//    private void resetHomeFragment() {
//        if (isQuestion) {
//            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
//                    .replace(R.id.main_container, QuestionFragment.newInstance(
//                            questionParser.getQuestion(lastQuestionPosition)))
//                    .commitAllowingStateLoss();
//        } else {
//            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).
//                    replace(R.id.main_container, new LockFragment())
//                    .commitAllowingStateLoss();
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home && currentFragmentID != R.id.nav_home) {
            // Handle the camera action

            resetHomeFragment();
        }
        else if (id == R.id.nav_tryourapp) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.handikapp.parentbox&hl=en")));
        }
        else if (id == R.id.nav_challenge) {
            Intent i = new Intent(this, CertificateActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_contact && currentFragmentID != R.id.nav_contact) {
            currentFragmentID = R.id.nav_contact;
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.main_container,
                            new ContactFragment()
                    ).commitAllowingStateLoss();
        } else if (id == R.id.nav_childpsychology && currentFragmentID != R.id.nav_childpsychology) {
            Log.i("Log", "Child Psychology item clicked..");
            currentFragmentID = R.id.nav_childpsychology;
            progress_container.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.main_container, ChildPsychologyFragment.newInstance(new ChildPsychologyStatementParser(this).getCategoryStatement(sharedPreferences.getInt(Utils.CHILDPSYCHOLOGYPOSITION,0))))
                    .commitAllowingStateLoss();
        } else if (id == R.id.nav_parentpsychology && currentFragmentID != R.id.nav_parentpsychology) {
            Log.i("Log", "Parentpsychology item clicked..");
            currentFragmentID = R.id.nav_parentpsychology;
            progress_container.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.main_container, ParentPsychologyFragment.newInstance(new ParentPsychologyParser(this).getCategoryStatement(sharedPreferences.getInt(Utils.PARENTPSYCHOLOGYPOSITION,0))))
                    .commitAllowingStateLoss();
        } else if (id == R.id.nav_knowfacts && currentFragmentID != R.id.nav_knowfacts) {
            Log.i("Log", "Do You know Facts item clicked..");
            currentFragmentID = R.id.nav_knowfacts;
            progress_container.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.main_container, DoYouKnowFactsFragment.newInstance(new KnowFactsParser(this).getCategoryStatement(sharedPreferences.getInt(Utils.FACTSPOSITION,0))))
                    .commitAllowingStateLoss();
        } else if (id == R.id.nav_parentingtips && currentFragmentID != R.id.nav_parentingtips) {
            Log.i("Log", "Parenting Tips item clicked..");
            currentFragmentID = R.id.nav_parentingtips;
            progress_container.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.main_container, ParentingTipsFragment.newInstance(new ParentingTipsParser(this).getCategoryStatement(sharedPreferences.getInt(Utils.CHILDPSYCHOLOGYPOSITION,0))))
                    .commitAllowingStateLoss();
        } else if (id == R.id.nav_about && currentFragmentID != R.id.nav_about) {
            currentFragmentID = R.id.nav_about;
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_container,
                    new AboutFragment()
            ).commitAllowingStateLoss();
        } else if (id == R.id.nav_share) {
            shareApp();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareApp() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/*");
        share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.ellalan.certifiedparent \n Hey, check out this awesome Parenting app!");
        startActivity(Intent.createChooser(share, "Share App"));
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                } else {
                    if (!AppPermissions.hasPermissions(this, AppPermissions.PERMISSIONS)) {
                        ActivityCompat.requestPermissions(this, AppPermissions.PERMISSIONS, PERMISSIONS_CODE);
                        return;
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
        }

    }




    @Override
    public void continueToCertificate() {
        AdditionalQuestionActivity.this.getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, MODE_PRIVATE).edit().putInt(AppConstants.QUIZ_QUESTION_NUMBER, 1).apply();
        startActivity(new Intent(AdditionalQuestionActivity.this, GenerateCertificate.class).putExtra("score", "" + totalScore));
        finish();
    }

    @Override
    public void LoadNextChildPsychologyStatement() {
        Log.i("Log", "ChildPsychologyPosition: " + Utils.CHILDPSYCHOLOGYPOSITION + "");
        int factposition = sharedPreferences.getInt(Utils.CHILDPSYCHOLOGYPOSITION,-1);
        factposition+=1;
        if (factposition >= 27) {
            sharedPreferences.edit().putInt(Utils.CHILDPSYCHOLOGYPOSITION,0).apply();
        }else {
            sharedPreferences.edit().putInt(Utils.CHILDPSYCHOLOGYPOSITION,factposition).apply();
        }

        if (factposition%9==0){
            mInterstitialAd.loadAd(adIRequest);

            mInterstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    showInterstitial();
                }
            });
        }


        currentFragmentID = AppConstants.CHILD_PSYCHOLOGY_FRAGMENT;
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.main_container, ChildPsychologyFragment.newInstance(new ChildPsychologyStatementParser(this).getCategoryStatement(sharedPreferences.getInt(Utils.CHILDPSYCHOLOGYPOSITION,0))))
                .commitAllowingStateLoss();


        Log.i("Log", "ChildPsychologyPosition: " + Utils.CHILDPSYCHOLOGYPOSITION + "");
    }

    @Override
    public void LoadNextParentPsychologyStatement() {
        int factposition = sharedPreferences.getInt(Utils.PARENTPSYCHOLOGYPOSITION,-1);
        factposition+=1;
        if (factposition >= 91) {
            sharedPreferences.edit().putInt(Utils.PARENTPSYCHOLOGYPOSITION,0).apply();
        }else {
            sharedPreferences.edit().putInt(Utils.PARENTPSYCHOLOGYPOSITION,factposition).apply();
        }

        if (factposition%9==0){
            mInterstitialAd.loadAd(adIRequest);

            mInterstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    showInterstitial();
                }
            });
        }

        currentFragmentID = AppConstants.PARENT_PSYCHOLOGY_FRAGMENT;
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.main_container, ParentPsychologyFragment.newInstance(new ParentPsychologyParser(this).getCategoryStatement(sharedPreferences.getInt(Utils.PARENTPSYCHOLOGYPOSITION,0))))
                .commitAllowingStateLoss();

    }

    @Override
    public void LoadNextParentingTipsStatement() {

        int factposition = sharedPreferences.getInt(Utils.PARENTINGTIPSPOSITION,-1);
        factposition+=1;
        if (factposition >= 48) {
            sharedPreferences.edit().putInt(Utils.PARENTINGTIPSPOSITION,0).apply();
        }else {
            sharedPreferences.edit().putInt(Utils.PARENTINGTIPSPOSITION,factposition).apply();
        }

        if (factposition%9==0){
            mInterstitialAd.loadAd(adIRequest);

            mInterstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    showInterstitial();
                }
            });
        }


        currentFragmentID = AppConstants.PARENTING_TIPS_FRAGMENTS;
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.main_container, ParentingTipsFragment.newInstance(new ParentingTipsParser(this).getCategoryStatement(sharedPreferences.getInt(Utils.PARENTINGTIPSPOSITION,0))))
                .commitAllowingStateLoss();



    }

    @Override
    public void LoadNextKnowFactsStatement() {
        int factposition = sharedPreferences.getInt(Utils.FACTSPOSITION,-1);
        factposition+=1;
        if (factposition >= 27) {
            sharedPreferences.edit().putInt(Utils.FACTSPOSITION,0).apply();
        }else {
            sharedPreferences.edit().putInt(Utils.FACTSPOSITION,factposition).apply();
        }

        if (factposition%9==0){
            mInterstitialAd.loadAd(adIRequest);

            mInterstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    showInterstitial();
                }
            });

        }


        currentFragmentID = AppConstants.KNOW_FACTS_FRAGMENT;
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.main_container, DoYouKnowFactsFragment.newInstance(new KnowFactsParser(this).getCategoryStatement(sharedPreferences.getInt(Utils.FACTSPOSITION,0))))
                .commitAllowingStateLoss();

    }

    @Override
    public void LoadPreviousChildPsychologyStatement() {
        int factposition = sharedPreferences.getInt(Utils.CHILDPSYCHOLOGYPOSITION,0);
        if (factposition != 0) {
            sharedPreferences.edit().putInt(Utils.CHILDPSYCHOLOGYPOSITION,factposition-1).apply();
        }



        currentFragmentID = AppConstants.CHILD_PSYCHOLOGY_FRAGMENT;
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.main_container, ChildPsychologyFragment.newInstance(new ChildPsychologyStatementParser(this).getCategoryStatement(sharedPreferences.getInt(Utils.CHILDPSYCHOLOGYPOSITION,0))))
                .commitAllowingStateLoss();
    }

    @Override
    public void LoadPreviousParentPsychologyStatement() {
        int factposition = sharedPreferences.getInt(Utils.PARENTPSYCHOLOGYPOSITION,0);
        if (factposition != 0) {
            sharedPreferences.edit().putInt(Utils.PARENTPSYCHOLOGYPOSITION,factposition-1).apply();
        }


        currentFragmentID = AppConstants.PARENT_PSYCHOLOGY_FRAGMENT;
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.main_container, ParentPsychologyFragment.newInstance(new ParentPsychologyParser(this).getCategoryStatement(sharedPreferences.getInt(Utils.PARENTPSYCHOLOGYPOSITION,0))))
                .commitAllowingStateLoss();
    }

    @Override
    public void LoadPreviousParentingTipsStatement() {
        int factposition = sharedPreferences.getInt(Utils.PARENTINGTIPSPOSITION,0);
        if (factposition != 0) {
            sharedPreferences.edit().putInt(Utils.PARENTINGTIPSPOSITION,factposition-1).apply();
        }


        currentFragmentID = AppConstants.PARENTING_TIPS_FRAGMENTS;
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.main_container, ParentingTipsFragment.newInstance(new ParentingTipsParser(this).getCategoryStatement(sharedPreferences.getInt(Utils.PARENTINGTIPSPOSITION,0))))
                .commitAllowingStateLoss();
    }

    @Override
    public void LoadPreviousKnowFactsStatement() {
        int factposition = sharedPreferences.getInt(Utils.FACTSPOSITION,0);
        if (factposition != 0) {
            sharedPreferences.edit().putInt(Utils.FACTSPOSITION,factposition-1).apply();
        }

//        if ((Utils.FACTSPOSITION%9)==0){
//            // Load ads into Interstitial Ads
//            mInterstitialAd.loadAd(adIRequest);
//
//            mInterstitialAd.setAdListener(new AdListener() {
//                public void onAdLoaded() {
//                    showInterstitial();
//                }
//            });
//        }
        currentFragmentID = AppConstants.KNOW_FACTS_FRAGMENT;
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.main_container, DoYouKnowFactsFragment.newInstance(new KnowFactsParser(this).getCategoryStatement(sharedPreferences.getInt(Utils.FACTSPOSITION,0))))
                .commitAllowingStateLoss();
    }

    @Override
    public void ChangeHomeFragment() {
        currentHomeFragmentID = AppConstants.HOME_FRAGMENT_QUESTION;
    }

    @Override
    public void LoadQuestion() {

        if (wsharedPreferences.getBoolean(AppConstants.WEEKLY_QUIZ_COMPLETED, true)) {
            return;
        }

        int qcount = wsharedPreferences.getInt(AppConstants.WEEKLY_QUIZ_QUESTION_COUNT, 1);
        if (qcount==1){
            wsharedPreferences.edit().putInt(AppConstants.WEEKLY_QUIZ_SCORE, 0).apply();
        }
        Log.i("Log", "quizquestioncount: " + qcount + "");
        if (qcount <= 5) {
            Random r = new Random();
            i1 = r.nextInt(lastQuestionPosition + 1);
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.main_container, WeeklyQuestionFragment.newInstance(questionParser.getQuestion(i1)))
                    .commitAllowingStateLoss();

        } else {
            weditor = wsharedPreferences.edit();
            weditor.putBoolean(AppConstants.WEEKLY_QUIZ_COMPLETED, true);
            long time= System.currentTimeMillis();
            long nextquiztime = time + (604800000);
            weditor.putLong(AppConstants.WEEKLY_QUIZ_NOTIFICATIONTIME,nextquiztime);
            weditor.commit();


            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, 168);

            Intent alarmIntent = new Intent(getApplicationContext(), WeeklyQuizBroadCastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            getApplicationContext().getSharedPreferences(AppConstants.PREF_WEEKLY, MODE_PRIVATE)
                    .edit().putLong(AppConstants.WEEKLY_QUIZ_NOTIFICATIONTIME, cal.getTimeInMillis()).apply();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    manager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                } else {
                    manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                }
            } else {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
            }

            //*******************Ending Notification***************************************
            wsharedPreferences.edit().putInt(AppConstants.WEEKLY_QUIZ_QUESTION_COUNT, 1).apply();
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.main_container, new WeeklyQuizCompleteFragment()).commitAllowingStateLoss();
        }

    }

    @Override
    public void LoadAnswer(boolean answerCorrect) {
        this.answerCorrect = answerCorrect;
        isQuestion = false;

        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.main_container, WeeklyQuizResultFragment.newInstance(new QuestionParser(this)
                        .getQuestion(i1), answerCorrect)).commitAllowingStateLoss();
        int qcount = wsharedPreferences.getInt(AppConstants.WEEKLY_QUIZ_QUESTION_COUNT, 1);
        progress_percentage.setText(wsharedPreferences.getInt(AppConstants.WEEKLY_QUIZ_QUESTION_COUNT, 1) * 10 + "%");
        wsharedPreferences.edit().putInt(AppConstants.WEEKLY_QUIZ_QUESTION_COUNT, qcount + 1).apply();

    }

    @Override
    public void UpdateWeeklyQuizResult(boolean re) {
        if (re) {
            totalScore = wsharedPreferences.getInt(AppConstants.WEEKLY_QUIZ_SCORE, 0);
            totalScore += 20;
            wsharedPreferences.edit().putInt(AppConstants.WEEKLY_QUIZ_SCORE, totalScore).apply();
        }
    }

    @Override
    public void ChallengeAParent() {
        Intent i = new Intent(this, CertificateActivity.class);
        startActivity(i);


    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
