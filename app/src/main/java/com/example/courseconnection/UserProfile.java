package com.example.courseconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class UserProfile extends AppCompatActivity {

    private String userName;
    private ArrayList<String> reviews = new ArrayList<>();
    private ArrayAdapter<String> courseNamesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        int index = email.indexOf('@');
        userName = email.substring(0,index);
    }
}