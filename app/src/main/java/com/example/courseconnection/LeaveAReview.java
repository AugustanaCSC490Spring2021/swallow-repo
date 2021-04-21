package com.example.courseconnection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LeaveAReview extends AppCompatActivity {

    private static final String TAG = "";
    private TextView teacherLabel, commentsLabel, reviewDisplayText;
    private EditText teacherTextBox, commentsTextBox, courseLabel;
    private RatingBar ratingBar;
    private Button submitButton;
    private float ratingValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_a_review);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        int index = email.indexOf('@');
        String id = email.substring(0,index);

        courseLabel = findViewById(R.id.courseLabel);
        teacherTextBox = findViewById(R.id.teacherTextBox);
        commentsTextBox = findViewById(R.id.commentsTextBox);
        reviewDisplayText = findViewById(R.id.reviewDisplayText);
        ratingBar = findViewById(R.id.ratingBar);
        submitButton = findViewById(R.id.submitButton);


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
                Map<String, Object> review = new HashMap<>();
                review.put("comment", commentsTextBox.getText().toString());
                review.put("profName", teacherTextBox.getText().toString());
                review.put("rating", ratingBar.getRating());
                review.put("user", id);
                review.put("course", courseLabel.getText().toString());

                db.collection("reviews").add(review)
                        .addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }

                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });

                finish();
            }
        });

    }
}