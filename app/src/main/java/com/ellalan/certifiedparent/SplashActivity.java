package com.ellalan.certifiedparent;



import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ellalan.certifiedparent.model.Quote;
import com.ellalan.certifiedparent.parsers.QuoteParser;


public class SplashActivity extends AppCompatActivity {

    private TextView quote, person;
    private Quote quoteObj;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = this.getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();


        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));



        quote = (TextView) findViewById(R.id.quote);
        person = (TextView) findViewById(R.id.person);

        quoteObj = new QuoteParser(this).randomQuote();

        quote.setText(quoteObj.getQuote());
        person.setText("― " + quoteObj.getPerson());


        MyNotificationManager myNotificationManager = new MyNotificationManager(this);
        myNotificationManager.dismissNotification();


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        quote.setAlpha(0);
        person.setAlpha(0);
        person.animate().alpha(1.0f).setDuration(2000).start();
        quote.animate().alpha(1.0f).setDuration(2000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {


                person.animate()
                        .alpha(0f)
                        .translationX(300)
                        .setDuration(1000)
                        .setStartDelay(2500).start();

                quote.animate()
                        .alpha(0f)
                        .translationX(-300)
                        .setDuration(1000)
                        .setStartDelay(2500)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                Login();
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        }).start();

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).start();


    }

    private void Login() {
        startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
        finish();
    }
}
