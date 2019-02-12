package br.com.waslleysouza.recognitionapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import br.com.waslleysouza.recognitionapp.R;
import br.com.waslleysouza.recognitionapp.service.RecognitionService;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = MenuActivity.class.getSimpleName();

    @InjectView(R.id.addButton) Button mAddButton;
    @InjectView(R.id.classifyButton) Button mClassifyButton;
    @InjectView(R.id.trainButton) Button mTrainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.inject(this);
        createEvents();
    }

    private void createEvents() {
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMainActivity = new Intent(getApplicationContext(), AddActivity.class);
                startActivity(intentMainActivity);
            }
        });

        mClassifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMainActivity = new Intent(getApplicationContext(), ClassifyActivity.class);
                startActivity(intentMainActivity);
            }
        });

        mTrainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrainButton.setEnabled(false);
                new Handler().post(new MenuActivity.WorkerThread());
                mTrainButton.setEnabled(true);
            }
        });
    }

    private class WorkerThread implements Runnable {

        public WorkerThread() {
        }

        @Override
        public void run() {
            RecognitionService.train(getApplicationContext());
        }
    }
}
