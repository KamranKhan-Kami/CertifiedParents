package com.ellalan.certifiedparent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CertificateActivity extends AppCompatActivity {

    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    ImageView certificate;
    File file;
    Button share_button;
    boolean share;

    Bitmap b = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.up_arrow_white);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = this.getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE);
        certificate = (ImageView) findViewById(R.id.certificate);
        share_button = (Button) findViewById(R.id.share_button);

        certificate.setImageBitmap(loadImageFromStorage(this, AppConstants.PREF_CERTIFICATE_NAME));

        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share = false;
                shareCertificate();
            }
        });
    }

    private void shareCertificate() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");

        if(this.share){
            share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.ellalan.certifiedparent \n Hey checkout this cool application :)");
        } else {
            share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.ellalan.certifiedparent \n Can you score above 20 in this tricky parenting quiz?");
        }

        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(share, "Share Image"));
    }


    public Bitmap loadImageFromStorage(Context context, String filename) {
        sharedPreferences = context.getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE);
        String path = sharedPreferences.getString(AppConstants.PREF_CERTIFICATE_PATH, null);
        if (path == null) {
            Log.i("Log","Image path is noull");
            return null;
        }


        try {
            file = new File(path, filename);
            b = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            share = true;
            shareCertificate();
        }

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
