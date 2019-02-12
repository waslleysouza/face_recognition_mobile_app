package br.com.waslleysouza.recognitionapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;

import br.com.waslleysouza.recognitionapp.R;
import br.com.waslleysouza.recognitionapp.util.ImageUtils;
import br.com.waslleysouza.recognitionapp.service.RecognitionService;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ClassifyActivity extends AppCompatActivity {

    private static final String TAG = ClassifyActivity.class.getSimpleName();

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @InjectView(R.id.imageView) ImageView mImageView;
    @InjectView(R.id.responseMessageText) TextView mResponseMessageText;

    private File photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photo = new File(getExternalFilesDir(null), "Pic1.jpg");
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(this,
                            "br.com.waslleysouza.recognitionapp.fileprovider", photo));
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setContentView(R.layout.activity_classify);
            ButterKnife.inject(this);

            // Get the Intent that started this activity and extract the string
            //File file = (File) getIntent().getSerializableExtra(MediaStore.EXTRA_OUTPUT);
            final Bitmap bitmap = ImageUtils.convertToBitmap(photo);

            new Handler().post(new ClassifyActivity.WorkerThread(bitmap, mResponseMessageText));
        }
    }

    private class WorkerThread implements Runnable {
        Bitmap mBitmap;
        TextView mResponseMessageText;

        public WorkerThread(Bitmap bitmap, TextView responseMessageText) {
            mBitmap = bitmap;
            mResponseMessageText = responseMessageText;
        }

        @Override
        public void run() {
            callFaceRecognitionAPI(mBitmap, mResponseMessageText);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void callFaceRecognitionAPI(Bitmap bitmap, TextView responseMessageText) {
        FaceDetector faceDetector = new
                FaceDetector.Builder(this)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        if (!faceDetector.isOperational()) {
            new AlertDialog.Builder(this).setMessage("Could not set up the face detector!").show();
            return;
        }

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);

        Log.d(TAG, "Faces: " + faces.size());
        for (int i = 0; i < faces.size(); i++) {
            Face face = faces.valueAt(i);
            bitmap = ImageUtils.getFace(face, bitmap);
            RecognitionService.recognize(getApplicationContext(), bitmap,responseMessageText);
        }

        mImageView.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {
                onBackPressed();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}