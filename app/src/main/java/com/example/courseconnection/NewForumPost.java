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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewForumPost extends AppCompatActivity {

    private static final String TAG = "----------";
    private EditText commentsTextBox, courseNum;
    private Button submitButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_forum_post);
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        int index = email.indexOf('@');
        String id = email.substring(0,index);

        courseNum = findViewById(R.id.courseNumText);
        commentsTextBox = findViewById(R.id.commentsTextBox);
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

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (courseCodes.contains(textView.getText().toString()) && !(textView.getText().toString().equals(""))) {
                    Map<String, Object> post = new HashMap<>();
                    String courseTitle = textView.getText().toString() + "-" + courseNum.getText().toString();
                    post.put("text", commentsTextBox.getText().toString());
                    post.put("user", id);
                    post.put("course", courseTitle);
                    post.put("date", FieldValue.serverTimestamp());

                    Log.wtf(TAG, "logging");

                    // add review to review collection
                    db.collection("forum_posts").add(post)
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
        }
    });
}}