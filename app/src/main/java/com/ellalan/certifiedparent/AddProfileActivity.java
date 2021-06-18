package com.ellalan.certifiedparent;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.ellalan.certifiedparent.util.AppPermissions;
import com.ellalan.certifiedparent.util.PrefHandler;
import com.ellalan.certifiedparent.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddProfileActivity extends AppCompatActivity {
    private static int GALLERY_REQUEST = 11;
    private static int CAMERA_REQUEST = 12;
    EditText edtName;
    Uri data;
    String score = "";
    ImageView imgUserPicture;
    PrefHandler pref;
    //    TextView tvScore;
    ProgressBar progress;
    Button btnCamera, btnGallery, btnGenerate;
    Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);
        pref = new PrefHandler(this);
//        tvScore=(TextView)findViewById(R.id.tvScore);
        score = getIntent().getStringExtra("score");
        progress = (ProgressBar) findViewById(R.id.progress);
        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnGenerate = (Button) findViewById(R.id.btnGenerate);
        btnGallery = (Button) findViewById(R.id.btnGallery);
        btnCamera.setOnClickListener(onClickListener);
        btnGallery.setOnClickListener(onClickListener);
        btnGenerate.setOnClickListener(onClickListener);
        btnGallery = (Button) findViewById(R.id.btnGallery);
//        score="(10/25)";
//        tvScore.setText(score);
        edtName = (EditText) findViewById(R.id.editName);
        imgUserPicture = (ImageView) findViewById(R.id.imgUserPicture);
        if (pref.getUserLoggedIn() != PrefHandler.USER_NOT_LOGGED_IN) {
            Glide.with(AddProfileActivity.this).load(pref.getPhotoUrl())
                    .asBitmap()
                    .thumbnail(0.5f)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            progress.setVisibility(View.GONE);
//                        itemShare.setVisible(false);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progress.setVisibility(View.GONE);
//                        itemShare.setVisible(true);
                            return false;
                        }
                    })
                    .into(new BitmapImageViewTarget(imgUserPicture) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(AddProfileActivity.this.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            imgUserPicture.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }
    }

    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    private ImageView mImageView;
    Uri imageUri;
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnCamera:
                    // Define the file-name to save photo taken by Camera activity
                    if (AppPermissions.hasPermissions(AddProfileActivity.this, AppPermissions.PERMISSIONS)) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File
                                Log.i("Error createing image", "IOException");
                            }
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                // Do something for lollipop and above versions
                                if (photoFile != null) {
                                    cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    photoURI = FileProvider.getUriForFile(AddProfileActivity.this, getApplicationContext().getPackageName() + ".my.package.name.provider", photoFile);
                                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI /*Uri.fromFile(photoFile)*/);
                                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                                }
                            } else {
                                // do something for phones running an SDK before lollipop
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                            }

                        }

                    } else {
                        ActivityCompat.requestPermissions(AddProfileActivity.this, AppPermissions.PERMISSIONS, 1);
                    }
                    return;
                case R.id.btnGallery:
                    if (AppPermissions.hasPermissions(AddProfileActivity.this, AppPermissions.PERMISSIONS)) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media
                                        .EXTERNAL_CONTENT_URI);

                        startActivityForResult(intent, GALLERY_REQUEST);
                    } else {
                        ActivityCompat.requestPermissions(AddProfileActivity.this, AppPermissions.PERMISSIONS, 1);
                    }
                    return;
                case R.id.btnGenerate:

                    if (Utils.imgBitmap == null) {
                        Toast.makeText(AddProfileActivity.this, "Please Upload Your Photo", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (edtName.getText().toString().equals("")) {
                        Toast.makeText(AddProfileActivity.this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(AddProfileActivity.this, ShareActivityUp.class);
                    intent.putExtra("image", data);
                    intent.putExtra("score", score);
                    intent.putExtra("name", edtName.getText().toString());
                    startActivity(intent);
            }
        }
    };

    private byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStorageDirectory();
        /*File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );*/
        File image = new File(storageDir.getAbsolutePath(), imageFileName + ".jpg");
        if (!image.exists())
            image.createNewFile();

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == GALLERY_REQUEST) {
                try {
                    this.data = data.getData();
                    File file = new File(getRealPathFromURI(this, data.getData()));
                    Utils.imgBitmap = Utils.decodeFile(file.getAbsolutePath());
//                    Utils.imgBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                    Glide.with(AddProfileActivity.this)
                            .load(bitmapToByte(Utils.imgBitmap))
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


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CAMERA_REQUEST) {

                try {
//                    this.data = data.getData();
                    this.data = photoURI;
//                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
                    //bitmap=GlobalMethods.decodeSampledBitmapFromResource(_path, 80, 80);
                    File file = new File(mCurrentPhotoPath);
                    Utils.imgBitmap = Utils.decodeFile(file.getAbsolutePath());

                    Glide.with(AddProfileActivity.this)
                            .load(bitmapToByte(Utils.imgBitmap))
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
                    Toast.makeText(this, "Working", Toast.LENGTH_SHORT).show();
                    /*String imageId = convertImageUriToFile(imageUri, AddProfileActivity.this);
                    new LoadImagesFromSDCard().execute("" + imageId);*/

                } catch (Exception e) {
                    Log.e("ERRROR", e.toString());
                    e.printStackTrace();
                    Toast.makeText(this, "FAILED", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    /*//    Bitmap mBitmap=null;
    Uri cameraImgUri=null;
    public class LoadImagesFromSDCard extends AsyncTask<String, Void, Void> {

        private ProgressDialog Dialog = new ProgressDialog(AddProfileActivity.this);



        protected void onPreExecute() {
            *//****** NOTE: You can call UI Element here. *****//*

            // Progress Dialog
            *//*Dialog.setMessage(getString(R.string.loading));
            Dialog.show();*//*
        }


        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {

            Bitmap bitmap = null;
            Bitmap newBitmap = null;
            cameraImgUri = null;


            try {

                *//**  Uri.withAppendedPath Method Description
     * Parameters
     *    baseUri  Uri to append path segment to
     *    pathSegment  encoded path segment to append
     * Returns
     *    a new Uri based on baseUri with the given segment appended to the path
     *//*

                cameraImgUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "" + urls[0]);

                *//**************  Decode an input stream into a bitmap. *********//*
//                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                bitmap = handleSamplingAndRotationBitmap(AddProfileActivity.this,cameraImgUri);

                if (bitmap != null) {

                    *//********* Creates a new bitmap, scaled from an existing bitmap. ***********//*
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int height = displaymetrics.heightPixels;
                    int width = displaymetrics.widthPixels;
                    newBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);



                    bitmap.recycle();

                    if (newBitmap != null) {

                        Utils.imgBitmap = newBitmap;
                    }

                }
            } catch (Exception e) {
                // Error fetching image, try to recover
                *//********* Cancel execution of this task. **********//*
                Utils.imgBitmap=null;
                cancel(true);
            }

            return null;
        }


        protected void onPostExecute(Void unused) {

            try {
                if (Utils.imgBitmap != null) {


                    ExifInterface exif = new ExifInterface(cameraImgUri.getPath());
                    int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int rotationInDegrees = exifToDegrees(rotation);
                    Glide.with(AddProfileActivity.this)
                            .load(bitmapToByte(Utils.imgBitmap))
                            .asBitmap()
                            .override(300, 300)
                            .fitCenter()
                            .transform(new RotateTransformation(AddProfileActivity.this, ((float) rotationInDegrees)))
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    private int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }
    public Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 100;
        int MAX_WIDTH = 100;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(img, selectedImage);
        return img;
    }
    private int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }
    private Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = AddProfileActivity.this.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(img, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(img, false, true);

            default:
                return img;
        }
    }
    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
    public Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public String convertImageUriToFile(Uri imageUri, Activity activity) {

        Cursor cursor = null;
        int imageID = 0;

        try {
            *//*********** Which columns values want to get *******//*
            String[] proj = {
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Thumbnails._ID,
                    MediaStore.Images.ImageColumns.ORIENTATION
            };
            cursor = activity.managedQuery(

                    imageUri,         //  Get data for specific image URI
                    proj,             //  Which columns to return
                    null,             //  WHERE clause; which rows to return (all rows)
                    null,             //  WHERE clause selection arguments (none)
                    null              //  Order-by clause (ascending by name)

            );
            //  Get Query Data
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int columnIndexThumb = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
            int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //int orientation_ColumnIndex = cursor.
            //getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);
            int size = cursor.getCount();
            *//*******  If size is 0, there are no images on the SD Card. *****//*
            if (size == 0) {

            } else {
                int thumbID = 0;
                if (cursor.moveToFirst()) {
                    *//**************** Captured image details ************//*
                    *//*****  Used to show image on view in LoadImagesFromSDCard class ******//*
                    imageID = cursor.getInt(columnIndex);

                    thumbID = cursor.getInt(columnIndexThumb);

                    String Path = cursor.getString(file_ColumnIndex);

                    //String orientation =  cursor.getString(orientation_ColumnIndex);

                    *//*String CapturedImageDetails = " CapturedImageDetails : \n\n"
                            + " ImageID :" + imageID + "\n"
                            + " ThumbID :" + thumbID + "\n"
                            + " Path :" + Path + "\n";*//*

                    // Show Captured Image detail on activity

                }
            }
        } finally {
            *//*if (cursor != null) {
                cursor.close();
            }*//*
        }

        // Return Captured Image ImageID ( By this ImageID Image will load from sdcard )
        return "" + imageID;
    }*/

}
