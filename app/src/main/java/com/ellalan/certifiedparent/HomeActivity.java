package com.ellalan.certifiedparent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

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

import com.ellalan.certifiedparent.fragments.QuestionFragment;
import com.ellalan.certifiedparent.fragments.ResultFragment;
import com.ellalan.certifiedparent.interfaces.MainInterface;
import com.ellalan.certifiedparent.model.Question;
import com.ellalan.certifiedparent.util.AppPermissions;
import com.ellalan.certifiedparent.util.QuestionInfo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import static com.ellalan.certifiedparent.util.AppPermissions.PERMISSIONS_CODE;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainInterface {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static int correct_answers = 0;
    //    private MenuItem item;
    //private Question question = new Question("Why do children lie?","Due to fear","Can’t differentiate between Truth & Lies","Due to fear",R.drawable.children_lie,R.drawable.correct_image_resource,R.drawable.wrong_image_resource,R.string.question_one_description);
    private ArrayList<Question> questions;
    private int question_index = 0;
    int[] bases;
    private boolean is_question = true;
    private boolean question_status = false;
    private SharedPreferences sharedPreferences;
    private String CHALLENGE = "challenge";
    private Boolean question_bank = false;

    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.i("Log","HomeActivity Started...!");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        question_bank = sharedPreferences.getBoolean(CHALLENGE, false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load an ad into the AdMob banner view.
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bases = getResources().getIntArray(R.array.important_questions);
        QuestionInfo allQuestions = QuestionInfo.getInstance(getApplicationContext());
        questions = allQuestions.getAllCourses();

        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_container, QuestionFragment.newInstance(questions.get(question_bank ? question_index : bases[question_index]))).commitAllowingStateLoss();

        if (!AppPermissions.hasPermissions(HomeActivity.this, AppPermissions.PERMISSIONS)) {
            ActivityCompat.requestPermissions(HomeActivity.this, AppPermissions.PERMISSIONS, PERMISSIONS_CODE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_challenge) {

        } else if (id == R.id.nav_contact) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    protected void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
        }
    }

    @Override
    public void LoadNext() {
        is_question = true;
        if ((question_bank && (question_index + 1) < questions.size()) || (question_index + 1) < bases.length) {
            question_index = question_index + 1;
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_container, QuestionFragment.newInstance(questions.get(question_bank ? question_index : bases[question_index]))).commitAllowingStateLoss();
        } else {
            if (!question_bank) {
                sharedPreferences.edit().putBoolean(CHALLENGE, true).apply();
//                startActivity(new Intent(HomeActivity.this, ShareActivity.class).putExtra("score", "(" + correct_answers + "/25)"));

                startActivity(new Intent(HomeActivity.this, AddProfileActivity.class).putExtra("score", "(" + correct_answers + "/25)"));
                finish();
            }
        }
        if (question_bank) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        } else {
//            getSupportActionBar().setTitle("You have " + correct_answers + " out of " + (question_bank ? questions.size() : bases.length) + " questions correct");
            getSupportActionBar().setTitle("(" + correct_answers + "/" + (question_bank ? questions.size() : bases.length) + ") questions correct");
        }
    }

    @Override
    public void UpdateResult(Boolean status) {
//        if (item != null) {
        if (question_bank) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        } else {
            correct_answers = correct_answers + (status ? 1 : 0);
//            getSupportActionBar().setTitle("You have "+correct_answers+" out of "+(question_bank ? questions.size() : bases.length)+" questions correct");
            getSupportActionBar().setTitle("(" + correct_answers + "/" + (question_bank ? questions.size() : bases.length) + ") questions correct");
        }
//        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        item = menu.findItem(R.id.menu_total_answers);
        return true;
    }*/

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (is_question) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_container, QuestionFragment.newInstance(questions.get(question_bank ? question_index : bases[question_index]))).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_container, ResultFragment.newInstance(questions.get(question_bank ? question_index : bases[question_index]), question_status)).commitAllowingStateLoss();
        }
    }

    public void MoveToAnswer(boolean status) {
        is_question = false;
        question_status = status;
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.main_container, ResultFragment.newInstance(questions.get(question_bank ? question_index : bases[question_index]), status)).commitAllowingStateLoss();
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
}
