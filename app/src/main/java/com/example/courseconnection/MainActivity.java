package com.example.courseconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 101;
    private static final String TAG = "SignInActivity";
    private View.OnClickListener listener;

    EditText email,password;
    Button loginBtn;
    TextView invalidCreds;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInBtn;
    GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    // user clicked the google sign in button
                    case R.id.sign_in_button:
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                        break;
                    // user clicked other sign in button
                    case R.id.loginBtn:
                        if (email.getText().toString().equals("admin") && password.getText().toString().equals("admin")) {
                            //correct password
                            invalidCreds.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(getApplicationContext(),Home.class);
                            startActivity(intent);
                        } else {
                            //wrong password
                            invalidCreds.setVisibility(View.VISIBLE);
                        }
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        invalidCreds = (TextView)findViewById(R.id.invalidCreds);
        loginBtn = (Button)findViewById(R.id.loginBtn);
        signInBtn = (SignInButton)findViewById(R.id.sign_in_button);

        signInBtn.setOnClickListener(listener);

        loginBtn.setOnClickListener(listener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null){
            signIn();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            signIn();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            invalidCreds.setVisibility(View.VISIBLE);
        }
    }

    private void signIn(){
        invalidCreds.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(getApplicationContext(),Home.class);
        intent.putExtra("EMAIL",account.getEmail());
        startActivity(intent);
    }
}