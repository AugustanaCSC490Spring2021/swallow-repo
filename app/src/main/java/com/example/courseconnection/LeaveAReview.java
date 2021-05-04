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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaveAReview extends AppCompatActivity {

    private static final String TAG = "---------------------";
    private TextView reviewDisplayText;
    private EditText teacherTextBox, commentsTextBox, courseNum;
    private RatingBar ratingBar;
    private Button submitButton;
    private float ratingValue;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_a_review);
        db = FirebaseFirestore.getInstance();

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
                    Map<String, Object> course = new HashMap<>();
                    String courseTitle = textView.getText().toString() + "-" + courseNum.getText().toString();
                    Float rating = ratingBar.getRating();
                    review.put("comment", commentsTextBox.getText().toString());
                    review.put("profName", teacherTextBox.getText().toString());
                    review.put("rating", rating);
                    review.put("user", id);
                    review.put("courseCode", textView.getText().toString());
                    review.put("courseNum", courseNum.getText().toString());

                    Log.wtf(TAG, "logging");

                    DocumentReference courseRef = db.collection("courses").document(courseTitle);
                    // if the course document doesn't exist initialize the number of ratings and average rating
                    courseRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // if the course already exists in the collection, add the new rating
                                    // following code based on example from https://github.com/firebase/snippets-android/blob/7484726ae6d9d2b1cfe9d0545e1c80f98a359a71/firestore/app/src/main/java/com/google/example/firestore/SolutionAggregation.java#L50-L82
                                    db.runTransaction(new Transaction.Function<Void>() {
                                        @Override
                                        public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                            DocumentSnapshot snapshot = transaction.get(courseRef);
                                            long numRatings = (long) snapshot.get("numRatings");

                                            // Compute new number of ratings
                                            long newNumRatings = numRatings + 1;

                                            // Compute new average rating
                                            double oldRatingTotal =  (double) snapshot.get("avgRating") * numRatings;
                                            double newAvgRating = (oldRatingTotal + rating) / newNumRatings;

                                            // Update course
                                            transaction.update(courseRef, "numRatings",newNumRatings);
                                            transaction.update(courseRef,"avgRating",newAvgRating);

                                            return null;
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "Transaction success!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Transaction failure.", e);
                                                }
                                            });
                                } else {
                                    Log.wtf(TAG, "loser!");

                                    // if the course doesn't exist in the collection, initialize all values of the course
                                    course.put("code", textView.getText().toString());
                                    course.put("num", courseNum.getText().toString());
                                    course.put("name", courseTitle);
                                    course.put("avgRating", rating);
                                    course.put("numRatings", 1);

                                    courseRef.set(course);
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });

                    // add review to review collection
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