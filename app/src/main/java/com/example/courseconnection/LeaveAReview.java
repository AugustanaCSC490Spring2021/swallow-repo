package com.example.courseconnection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaveAReview extends AppCompatActivity {

    private static final String TAG = "";
    private TextView teacherLabel, commentsLabel, reviewDisplayText;
    private EditText teacherTextBox, commentsTextBox, courseNum;
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

        courseNum = findViewById(R.id.courseNumText);
        teacherTextBox = findViewById(R.id.teacherTextBox);
        commentsTextBox = findViewById(R.id.commentsTextBox);
        reviewDisplayText = findViewById(R.id.reviewDisplayText);
        ratingBar = findViewById(R.id.ratingBar);
        submitButton = findViewById(R.id.submitButton);
        List<String> courseCodes = Arrays.asList(getResources().getStringArray(R.array.courseCodes));

        /*Code below found on Android's Development Documentation.
        Used for the drop down box that opens when specifying a course code.
         */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, courseCodes);
        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.courseCodeText);
        textView.setAdapter(adapter);
        textView.setThreshold(0);


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
                if (courseCodes.contains(textView.getText().toString()) && !(textView.getText().toString().equals(""))) {
                    Map<String, Object> review = new HashMap<>();
                    review.put("comment", commentsTextBox.getText().toString());
                    review.put("profName", teacherTextBox.getText().toString());
                    review.put("rating", ratingBar.getRating());
                    review.put("user", id);
                    review.put("course", textView.getText().toString() +"-"+ courseNum.getText().toString());

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
                }else{
                    Toast.makeText(LeaveAReview.this, "Invalid Course Code", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}