package com.ellalan.certifiedparent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ParentAgreement extends AppCompatActivity {

    Button okay;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_agreement);
        okay = (Button) findViewById(R.id.okay);
        Log.i("Log","ParentAgreement activity started...");

        sharedPreferences = getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE);

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit().putBoolean(AppConstants.PREF_AGREED, true).apply();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }
}
