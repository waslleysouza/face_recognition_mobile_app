package br.com.waslleysouza.recognitionapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.VideoView;

import java.io.File;

import br.com.waslleysouza.recognitionapp.R;
import br.com.waslleysouza.recognitionapp.service.RecognitionService;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class AddActivity extends AppCompatActivity {

    private static final String TAG = AddActivity.class.getSimpleName();

    private static final int REQUEST_VIDEO_CAPTURE = 1;

    @InjectView(R.id.videoView) VideoView mVideoView;
    @InjectView(R.id.nameText) EditText mNameText;
    @InjectView(R.id.addButton) Button mAddButton;

    private File file;
    private int mOrientation;
    private int mPhotoOrientation;
    private OrientationEventListener mOrientationEventListener;
    private static final int ORIENTATION_PORTRAIT_NORMAL =  0;
    private static final int ORIENTATION_PORTRAIT_INVERTED =  180;
    private static final int ORIENTATION_LANDSCAPE_NORMAL =  270;
    private static final int ORIENTATION_LANDSCAPE_INVERTED = 90;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dispatchTakeVideoIntent();

        mOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                mOrientation = orientation;

                if (orientation >= 315 || orientation < 45) {
                    if (mOrientation != ORIENTATION_PORTRAIT_NORMAL) {
                        mOrientation = ORIENTATION_PORTRAIT_NORMAL;
                    }
                }
                else if (orientation < 315 && orientation >= 225) {
                    if (mOrientation != ORIENTATION_LANDSCAPE_NORMAL) {
                        mOrientation = ORIENTATION_LANDSCAPE_NORMAL;
                    }
                }
                else if (orientation < 225 && orientation >= 135) {
                    if (mOrientation != ORIENTATION_PORTRAIT_INVERTED) {
                        mOrientation = ORIENTATION_PORTRAIT_INVERTED;
                    }
                }
                else {
                    if (mOrientation != ORIENTATION_LANDSCAPE_INVERTED) {
                        mOrientation = ORIENTATION_LANDSCAPE_INVERTED;
                    }
                }

                if (file.length() < 5000) {
                    mPhotoOrientation = 90;
                    if (mOrientation == 90) mPhotoOrientation = 180;
                    if (mOrientation == 180) mPhotoOrientation = 270;
                    if (mOrientation == 270) mPhotoOrientation = 0;
                    Log.v("mPhotoOrientation: ",mPhotoOrientation+"");
                }

                //Log.v("mOrientation: ",mOrientation+"");
                //Log.v("mPhotoOrientation: ",mPhotoOrientation+"");
                //Log.v(TAG, "file.length(): " + file.length());
            }
        };

        if(mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }
    }

    private void dispatchTakeVideoIntent() {
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.CAMERA  }, 200 );
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            file = new File(getExternalFilesDir(null), "video.mp4");
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(this,
                            "br.com.waslleysouza.recognitionapp.fileprovider", file));
            startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.activity_add);
            ButterKnife.inject(this);

            // Get the Intent that started this activity and extract the string
            //file = (File) getIntent().getSerializableExtra(MediaStore.EXTRA_OUTPUT);

            Uri videoUri = intent.getData();
            mVideoView.setVideoURI(videoUri);

            mAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAddButton.setEnabled(false);
                    new Handler().post(new AddActivity.WorkerThread(
                            file, ""+mPhotoOrientation, mNameText.getText().toString().trim()));
                }
            });
        }
    }

    private class WorkerThread implements Runnable {
        File mFile;
        String mRotate;
        String mName;

        public WorkerThread(File file, String rotate, String name) {
            mFile = file;
            mRotate = rotate;
            mName = name;
        }

        @Override
        public void run() {
            RecognitionService.add(getApplicationContext(), mFile, mRotate, mName);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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