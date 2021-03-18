package com.example.courseconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText email,password;
    Button loginBtn;
    TextView invalidCreds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        invalidCreds =(TextView)findViewById(R.id.invalidCreds);
        loginBtn = (Button)findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
    }

}