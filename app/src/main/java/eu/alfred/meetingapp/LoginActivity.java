package eu.alfred.meetingapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import eu.alfred.internal.wrapper.authentication.AuthenticatedUser;
import eu.alfred.internal.wrapper.authentication.AuthenticationException;
import eu.alfred.internal.wrapper.authentication.AuthenticationServerWrapper;
import eu.alfred.internal.wrapper.authentication.login.LoginData;
import eu.alfred.internal.wrapper.authentication.login.LoginDataException;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailView, mPasswordView;
    private View mProgressView, mLoginFormView;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(preferences.contains("id")) {
            Intent goToMainIntent = new Intent(getApplicationContext(), MainActivity.class);
            //goToMainIntent.putExtra("User", authenticatedUser.getUserId());
            startActivity(goToMainIntent);
            finish();
        }

        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                LoginData.Builder loginBuilder = new LoginData.Builder();

                try { loginBuilder.setEmail(mEmailView.getText().toString()); }
                catch (LoginDataException e) { Log.e("LoginDataException", e.toString()); return; }

                try { loginBuilder.setPassword(mPasswordView.getText().toString()); }
                catch (LoginDataException e) { Log.e("LoginDataException", e.toString()); return; }

                try {
                    AuthenticationServerWrapper authWrapper = new AuthenticationServerWrapper();
                    AuthenticatedUser authenticatedUser = authWrapper.login(loginBuilder.create());
                    //result.setText(authenticatedUser.toString());
                    Log.d("AuthenticatedUser", authenticatedUser.toString());

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("id", authenticatedUser.getUserId());
                    Log.d("User Id", authenticatedUser.getUserId());
                    editor.putString("token", authenticatedUser.getAccessToken());
                    editor.commit();

                    Intent goToMainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(goToMainIntent);
                    finish();
                } catch (AuthenticationException e) { Log.e("AuthenticationException", e.getMessage()); }
            }
        }).start();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
