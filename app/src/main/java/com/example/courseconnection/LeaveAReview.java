package com.example.courseconnection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class LeaveAReview extends AppCompatActivity {


    private TextView courseLabel, teacherLabel, commentsLabel, reviewDisplayText;
    private EditText teacherTextBox, commentsTextBox;
    private RatingBar ratingBar;
    private Button submitButton;
    private float ratingValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_a_review);

        Intent intent = getIntent();
        String className = intent.getStringExtra("EXTRA_MESSAGE");


        courseLabel = findViewById(R.id.courseLabel);
        teacherTextBox = findViewById(R.id.teacherTextBox);
        commentsTextBox = findViewById(R.id.commentsTextBox);
        reviewDisplayText = findViewById(R.id.reviewDisplayText);
        ratingBar = findViewById(R.id.ratingBar);
        submitButton = findViewById(R.id.submitButton);

        courseLabel.setText(className);


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingValue = ratingBar.getRating();
                reviewDisplayText.setText(ratingValue + "/5.0");
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}