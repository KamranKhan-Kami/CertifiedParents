package com.ellalan.certifiedparent;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ellalan.certifiedparent.util.PrefHandler;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WelcomeActivity extends AppCompatActivity{
    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;


    private ProgressDialog mProgressDialog;


    private ImageView logo;
    private TextView textView;

    LinearLayout layoutLogin;
    PrefHandler pref;
    private SharedPreferences sharedPreferences;



    // [END declare_auth]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = new PrefHandler(this);
        sharedPreferences = this.getSharedPreferences("CP_QuestionTracker", MODE_PRIVATE);

        setContentView(R.layout.activity_welcome);
        logo = (ImageView) findViewById(R.id.login_logo);
        textView = (TextView) findViewById(R.id.splash_welcome);






        // ***********Getting A1-KeyHash****************************
  try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {

        }
        catch (NoSuchAlgorithmException e) {

        }



    }




    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(1500);
        logo.startAnimation(animation);
        ObjectAnimator anim = ObjectAnimator.ofFloat(logo, "translationY", 50, 0).setDuration(6000);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                /*isAnimationEnded=true;
                if(pref.getUserLoggedIn()==PrefHandler.USER_GMAIL)
                {*/
                Login();
                /*}*/
            }

            @Override
            public void onAnimationCancel(Animator animation) {
//                Login();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    private void Login() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE);
        boolean certificateGenerated = sharedPreferences.getBoolean(AppConstants.PREF_CERTIFICATE_GENERATED, false);
        boolean agreedTOS = sharedPreferences.getBoolean(AppConstants.PREF_AGREED, false);
        if (!agreedTOS) {
            startActivity(new Intent(WelcomeActivity.this, ParentAgreement.class));
        } else if (!certificateGenerated) {
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        } else {
            Intent i = new Intent(WelcomeActivity.this, AdditionalQuestionActivity.class);
            startActivity(i);
        }
        finish();
    }
}
