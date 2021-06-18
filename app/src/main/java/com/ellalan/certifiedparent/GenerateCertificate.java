package com.ellalan.certifiedparent;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.ellalan.certifiedparent.util.AppPermissions;
import com.ellalan.certifiedparent.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GenerateCertificate extends AppCompatActivity implements View.OnClickListener {

    EditText edtName;
    String score;
    ImageView imgUserPicture;
    Button btnCamera, btnGallery, btnGenerate;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int IMAGE_GALLERY = 2;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_certificate);
        score = getIntent().getStringExtra("score");
        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnGenerate = (Button) findViewById(R.id.btnGenerate);
        btnGallery = (Button) findViewById(R.id.btnGallery);
        edtName = (EditText) findViewById(R.id.editName);
        imgUserPicture = (ImageView) findViewById(R.id.imgUserPicture);

        btnCamera.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
        btnGenerate.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnCamera:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.i("ERROR", "IOException");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
                break;
            case R.id.btnGallery:
                if (AppPermissions.hasPermissions(this, AppPermissions.PERMISSIONS)) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media
                                    .EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_GALLERY);
                } else {
                    ActivityCompat.requestPermissions(this, AppPermissions.PERMISSIONS, 1);
                }
                break;
            case R.id.btnGenerate:
                if (path == null) {
                    Toast.makeText(this, "Please Upload Your Photo", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edtName.getText().toString().equals("")) {
                    Toast.makeText(this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this, ShareActivityUp.class);
                intent.putExtra("image", path);
                intent.putExtra("score", score);
                intent.putExtra("name", edtName.getText().toString());
                startActivity(intent);
                finish();
                break;
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  // prefix
//                "HAHA.jpg",         // suffix
//                storageDir      // directory
//        );

        File img = new File(storageDir, imageFileName + ".jpg");
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = img.getAbsolutePath();
        return img;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
//                        Uri.fromFile(new File(mCurrentPhotoPath)));

            if (mCurrentPhotoPath != null) {
                Log.e("NULL", "Photo path is NULL");

                mImageBitmap = Utils.decodeFile(mCurrentPhotoPath);
                path = mCurrentPhotoPath;
//            mImageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                if (mImageBitmap == null) {
                    Log.e("NULL", "Bitmap is NULL");
                }
//                imgUserPicture.setImageBitmap(mImageBitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                Glide.with(this)
                        .load(stream.toByteArray())
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
                            }
                        });
            } else {
                Toast.makeText(this, "There was an error, please try again", Toast.LENGTH_SHORT).show();
            }


        } else if (requestCode == IMAGE_GALLERY && resultCode == RESULT_OK) {
//            mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(data.getData().getPath())));

            path = getPath(data.getData());
            mImageBitmap = Utils.decodeFile(path);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
            Glide.with(this)
                    .load(stream.toByteArray())
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
                        }
                    });
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
