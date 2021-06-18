package com.ellalan.certifiedparent.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;


public class Utils {

    public static final int[] main_question_ids = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    public static Bitmap imgBitmap = null;
    public static int wqlimit = 10;
    public static int wqposition = 0;

    public static String CHILDPSYCHOLOGYPOSITION = "CHILDPSYCHOLOGYPOSITION";
    public static String PARENTPSYCHOLOGYPOSITION = "PARENTPSYCHOLOGYPOSITION";
    public static String PARENTINGTIPSPOSITION = "PARENTINGTIPSPOSITION";
    public static String FACTSPOSITION = "FACTSPOSITION";

    public static Bitmap decodeFile(String path) {

        int orientation;

        try {

            if (path == null) {

                return null;
            }
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 4;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale++;
            }
            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bm = BitmapFactory.decodeFile(path, o2);


            Bitmap bitmap = bm;

            ExifInterface exif = new ExifInterface(path);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.e("orientation", "" + orientation);
            Matrix m = new Matrix();

            if ((orientation == 3)) {

                m.postRotate(180);
                m.postScale((float) bm.getWidth(), (float) bm.getHeight());

//               if(m.preRotate(90)){
                Log.e("in orientation", "" + orientation);

                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
                return bitmap;
            } else if (orientation == 6) {

                m.postRotate(90);

                Log.e("in orientation", "" + orientation);

                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
                return bitmap;
            } else if (orientation == 8) {

                m.postRotate(270);

                Log.e("in orientation", "" + orientation);

                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
                return bitmap;
            }
            return bitmap;
        } catch (Exception e) {

        }
        return null;
    }
  /*  public void enableOrientationListener(final Context context){

        if (mOrientationEventListener == null) {
            mOrientationEventListener = new OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {

                @Override
                public void onOrientationChanged(int orientation) {

                    // determine our orientation based on sensor response
                    int lastOrientation = mOrientation;

                    Display display = null;
                    if(parentActivity == null){
                        display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                    }else{
                        display = parentActivity.getWindowManager().getDefaultDisplay();
                    }


                    if (display.getOrientation() == Surface.ROTATION_0) {   // landscape oriented devices
                        if (orientation >= 315 || orientation < 45) {
                            if (mOrientation != ORIENTATION_LANDSCAPE_NORMAL) {
                                mOrientation = ORIENTATION_LANDSCAPE_NORMAL;
                            }
                        } else if (orientation < 315 && orientation >= 225) {
                            if (mOrientation != ORIENTATION_PORTRAIT_INVERTED) {
                                mOrientation = ORIENTATION_PORTRAIT_INVERTED;
                            }
                        } else if (orientation < 225 && orientation >= 135) {
                            if (mOrientation != ORIENTATION_LANDSCAPE_INVERTED) {
                                mOrientation = ORIENTATION_LANDSCAPE_INVERTED;
                            }
                        } else if (orientation <135 && orientation > 45) {
                            if (mOrientation != ORIENTATION_PORTRAIT_NORMAL) {
                                mOrientation = ORIENTATION_PORTRAIT_NORMAL;
                            }
                        }
                    } else {  // portrait oriented devices
                        if (orientation >= 315 || orientation < 45) {
                            if (mOrientation != ORIENTATION_PORTRAIT_NORMAL) {
                                mOrientation = ORIENTATION_PORTRAIT_NORMAL;
                            }
                        } else if (orientation < 315 && orientation >= 225) {
                            if (mOrientation != ORIENTATION_LANDSCAPE_NORMAL) {
                                mOrientation = ORIENTATION_LANDSCAPE_NORMAL;
                            }
                        } else if (orientation < 225 && orientation >= 135) {
                            if (mOrientation != ORIENTATION_PORTRAIT_INVERTED) {
                                mOrientation = ORIENTATION_PORTRAIT_INVERTED;
                            }
                        } else if (orientation <135 && orientation > 45) {
                            if (mOrientation != ORIENTATION_LANDSCAPE_INVERTED) {
                                mOrientation = ORIENTATION_LANDSCAPE_INVERTED;
                            }
                        }
                    }
                }
            };
        }
        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }
    }
*/

    private static final int ORIENTATION_PORTRAIT_NORMAL = 1;
    private static final int ORIENTATION_PORTRAIT_INVERTED = 2;
    private static final int ORIENTATION_LANDSCAPE_NORMAL = 3;
    private static final int ORIENTATION_LANDSCAPE_INVERTED = 4;
}
