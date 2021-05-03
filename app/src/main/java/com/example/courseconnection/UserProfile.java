package com.example.courseconnection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserProfile extends AppCompatActivity {

    private static final String TAG = "";
    private String userName;
    private ListView lvReviews;
    private List<String> reviewSummaries = new ArrayList<>();
    private ArrayList<String> reviews = new ArrayList<>();
    private ArrayAdapter<String> courseNamesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setTitle(userName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        int index = email.indexOf('@');
        userName = email.substring(0,index);
        lvReviews = findViewById(R.id.lvReviews);


        // fill list
        populateList();

        // display list on app
        courseNamesAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, reviewSummaries);
        lvReviews.setAdapter(courseNamesAdapter);
        setupListViewListener();
    }

    private void populateList()
    {
        reviewSummaries.clear();
        reviews.clear();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("reviews")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.get("user").equals(userName)) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    String courseCode = (String) document.get("courseCode");
                                    String courseNum = (String) document.get("courseNum");
                                    String course = courseCode + "-" + courseNum;
                                    String score = document.get("rating").toString();
                                    String comment = (String) document.get("comment");
                                    String user = (String) document.get("user");
                                    String reviewSummary = course + ":        " + score + " stars\n";
                                    String review = course + "\nUser " + user + " said: " + score + " stars\n\"" + comment + "\"";
                                    reviews.add(review);
                                    reviewSummaries.add(reviewSummary);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        courseNamesAdapter.notifyDataSetChanged();
                    }
                });

    };

    // Attaches a click listener to the ListView
    private void setupListViewListener() {
        lvReviews.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter,
                                            View item, int pos, long id) {
                        // View review within array at position pos
                        String viewedReview = reviews.get(pos);
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
                        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // user is done viewing contact
                            }
                        });
                        builder.setTitle("Review");
                        builder.setMessage(viewedReview);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
    }

    public void end(View v){
        finish();
    }
}