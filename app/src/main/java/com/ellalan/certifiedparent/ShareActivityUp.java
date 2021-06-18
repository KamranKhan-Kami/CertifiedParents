package com.ellalan.certifiedparent;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.ellalan.certifiedparent.util.PrefHandler;
import com.ellalan.certifiedparent.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class ShareActivityUp extends AppCompatActivity {
    RelativeLayout view;
    EditText text;
    String score = "";
    String image;
    ImageView imgUserPicture;
    TextView txtUserName;
    PrefHandler pref;
    TextView tvScore, parent_tip;
    ProgressBar progress;
    SharedPreferences sharedPreferences;
    Toolbar toolbar;
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    Button share_button;
    File file;
    Bitmap raw;
    private ArrayList<Integer> parenting_tip = new ArrayList<>();
    boolean share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_up);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.up_arrow_white);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = this.getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE);
        pref = new PrefHandler(this);
        tvScore = (TextView) findViewById(R.id.tvScore);
        parent_tip = (TextView) findViewById(R.id.parent_tip);
        share_button = (Button) findViewById(R.id.share_button);

        score = getIntent().getStringExtra("score");
        image = getIntent().getStringExtra("image");

        progress = (ProgressBar) findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
        tvScore.setText(score);
        view = (RelativeLayout) findViewById(R.id.certificate_layout);
        text = (EditText) findViewById(R.id.parent_name);
        imgUserPicture = (ImageView) findViewById(R.id.imgUserPicture);
        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtUserName.setText(getIntent().getStringExtra("name"));

        raw = Utils.decodeFile(image);
        Glide.with(this)
                .load(bitmapToByte(raw))
                .asBitmap()
                .override(300, 300)
                .fitCenter()
                .into(new BitmapImageViewTarget(imgUserPicture) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imgUserPicture.setImageDrawable(circularBitmapDrawable);
                        sharedPreferences.edit().putBoolean(AppConstants.PREF_CERTIFICATE_GENERATED, true).apply();

//                        Calendar cal = Calendar.getInstance();
//                        cal.add(Calendar.HOUR, 24);
//
//                        Intent alarmIntent = new Intent(getApplicationContext(), CPBroadcastReceiver.class);
//                        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);
//                        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//                        getApplicationContext().getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, MODE_PRIVATE)
//                                .edit().putLong(AppConstants.PREF_ADDITIONAL_NOTIFICATION_TIME, cal.getTimeInMillis()).apply();
//
//                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                                manager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//                            } else {
//                                manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//                            }
//                        } else {
//                            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//                        }


                        getApplicationContext().getSharedPreferences(AppConstants.PREF_ADDITIONAL_NAME, MODE_PRIVATE)
                                .edit().putBoolean(AppConstants.PREF_ADDITIONAL_LOCKED, true).apply();
                    }
                });


        parenting_tip.add(R.string.pt_0);
        parenting_tip.add(R.string.pt_1);
        parenting_tip.add(R.string.pt_2);
        parenting_tip.add(R.string.pt_3);
        parenting_tip.add(R.string.pt_4);
        parenting_tip.add(R.string.pt_5);
        parenting_tip.add(R.string.pt_6);
        parenting_tip.add(R.string.pt_7);
        parenting_tip.add(R.string.pt_8);
        parenting_tip.add(R.string.pt_9);
        parenting_tip.add(R.string.pt_10);
        parenting_tip.add(R.string.pt_11);
        parenting_tip.add(R.string.pt_12);
        parenting_tip.add(R.string.pt_13);
        parenting_tip.add(R.string.pt_14);
        parenting_tip.add(R.string.pt_15);
        parenting_tip.add(R.string.pt_16);
        parenting_tip.add(R.string.pt_17);

        Random random = new Random();

        //f15513

        final SpannableStringBuilder sb = new SpannableStringBuilder(
                "A parenting tip for you, \n" + getString(parenting_tip.get(random.nextInt(18)))
        );

        // Span to set text color to some RGB value
        final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.parseColor("#f15513"));

        // Set the text color for first 4 characters
        sb.setSpan(fcs, 0, 25, Spannable.SPAN_INCLUSIVE_INCLUSIVE);


        //parent_tip.setText("A parenting tip for you, \n" + getString(parenting_tip.get(random.nextInt(18))));
        parent_tip.setText(sb);


        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share = false;
                shareCertificate();
            }
        });

    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void saveCertificate() {
        String file_path = getApplicationContext().getExternalFilesDir("certificate").getAbsolutePath() + "/" + getString(R.string.app_name);
        File dir = new File(file_path);
        if (!dir.exists())
            dir.mkdirs();
        String fileName = "PC_CERTIFICATE.jpg";
        file = new File(dir, fileName);
        sharedPreferences.edit().putString(AppConstants.PREF_CERTIFICATE_PATH, dir.getAbsolutePath()).apply();
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 30, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

    }

    MenuItem itemShare;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        itemShare = menu.findItem(R.id.action_share);

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;
//        bm = getResizedBitmap(view.getDrawingCache(), width, height);
//
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                saveCertificate();
//            }
//        });
//
//        thread.start();

        return true;
    }

    Bitmap bm = null;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            share = true;
            shareCertificate();
        }

        if (item.getItemId() == android.R.id.home) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            bm = getResizedBitmap(view.getDrawingCache(), width, height);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    saveCertificate();
                }
            });

            thread.start();
            startActivity(new Intent(this, AdditionalQuestionActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareCertificate() {
        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(ShareActivityUp.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permissionCheck1 = ContextCompat.checkSelfPermission(ShareActivityUp.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED || permissionCheck1 != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 12);
                return;
            }
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        bm = getResizedBitmap(view.getDrawingCache(), width, height);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                saveCertificate();
            }
        });

        thread.start();

        AsyncCreateImage asyncCreateImage = new AsyncCreateImage();
        asyncCreateImage.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        bm = getResizedBitmap(view.getDrawingCache(), width, height);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                saveCertificate();
            }
        });

        thread.start();

        startActivity(new Intent(this, AdditionalQuestionActivity.class));
        finish();
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    ProgressDialog pDialog;

    private class AsyncCreateImage extends AsyncTask<String, String, String> {
        File file;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ShareActivityUp.this);
            pDialog.setMessage("Generating Certificate");
            pDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "fail";


            String file_path = getApplicationContext().getExternalFilesDir("certificate").getAbsolutePath() + "/" + getString(R.string.app_name);
            File dir = new File(file_path);
            if (!dir.exists())
                dir.mkdirs();
            String fileName = "PC_CERTIFICATE.jpg";
            file = new File(dir, fileName);
            FileOutputStream fOut;
            try {
                fOut = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.JPEG, 30, fOut);
                fOut.flush();
                fOut.close();
                result = "success";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            pDialog.dismiss();
            if (result.equals("success")) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                if (ShareActivityUp.this.share) {
                    share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.ellalan.certifiedparent \n Hey checkout this cool application :)");
                } else {
                    share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.ellalan.certifiedparent \n Can you score above 20 in this tricky parenting quiz?");
                }

                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(share, "Share Image"));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bm != null)
            bm.recycle();
    }
}
