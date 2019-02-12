package br.com.waslleysouza.recognitionapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import br.com.waslleysouza.recognitionapp.R;
import br.com.waslleysouza.recognitionapp.util.Utils;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @InjectView(R.id.usernameText) EditText mUsernameText;
    @InjectView(R.id.passwordText) EditText mPasswordText;
    @InjectView(R.id.serverURLText) EditText mServerURLText;
    @InjectView(R.id.identityText) EditText mIdentityText;
    @InjectView(R.id.loginButton) Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        mUsernameText.setText("waslley.souza@oracle.com");
        mPasswordText.setText("testeteste");
        mServerURLText.setText("http://132.145.132.230:5000");
        mIdentityText.setText("gse00014123");

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        mLoginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(
                LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String username = mUsernameText.getText().toString();
        String password = mPasswordText.getText().toString();
        String serverURL = mServerURLText.getText().toString();
        String identity = mIdentityText.getText().toString();
        Utils.setLoginSharedPreferences(this, username, password, serverURL, identity);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        mLoginButton.setEnabled(true);
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        mLoginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String username = mUsernameText.getText().toString();
        String password = mPasswordText.getText().toString();
        String serverURL = mServerURLText.getText().toString();
        String identity = mIdentityText.getText().toString();

        if (username.isEmpty()) {
            mUsernameText.setError("enter a valid username");
            valid = false;
        } else {
            mUsernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 2 || password.length() > 10) {
            mPasswordText.setError("between 2 and 10 alphanumeric characters");
            valid = false;
        } else {
            mPasswordText.setError(null);
        }

        if (serverURL.isEmpty()) {
            mServerURLText.setError("enter a valid server URL");
            valid = false;
        } else {
            mServerURLText.setError(null);
        }

        if (identity.isEmpty()) {
            mIdentityText.setError("enter a valid identity");
            valid = false;
        } else {
            mIdentityText.setError(null);
        }

        return valid;
    }
}