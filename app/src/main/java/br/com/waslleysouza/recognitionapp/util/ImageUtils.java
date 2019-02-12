package br.com.waslleysouza.recognitionapp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import com.google.android.gms.vision.face.Face;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    private static final String TAG = "ImageUtils";

    private final static int IMAGE_SIZE = 182;
    private final static int IMAGE_QUALITY = 80;

    /*
     * The method is taking Bitmap as an argument
     * then it will return the byte[] array for the given bitmap
     * and we will send this array to the server
     * 0 means worse quality
     * 100 means best quality
     * */
    public static byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static Bitmap getFace(Face face, Bitmap bitmap) {
        int x = (int) face.getPosition().x;
        int y = (int) face.getPosition().y;
        int width = (int) face.getWidth();
        int height = (int) face.getHeight();

        bitmap = Bitmap.createBitmap(bitmap, x, y, width, height);
        bitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, false);
        return bitmap;
    }

    public static Bitmap convertToBitmap(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        options.inMutable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        // Get image orientation
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Log.d(TAG, "Orientation: " + orientation);

        // Rotate image
        Matrix matrix = new Matrix();
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        matrix.postRotate(rotationAngle);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }
}