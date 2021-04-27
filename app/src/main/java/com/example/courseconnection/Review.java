package com.example.courseconnection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Review#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Review extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "";
    private Spinner deptSpinner;
    private Button addReviewBtn;
    private ListView lvCourses;
    private List<String> reviewSummaries = new ArrayList<>();
    private List<String> reviews = new ArrayList<>();
    private ArrayAdapter listAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Review() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Review.
     */
    // TODO: Rename and change types and number of parameters
    public static Review newInstance(String param1, String param2) {
        Review fragment = new Review();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        reviewSummaries.clear();
        reviews.clear();
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        deptSpinner = (Spinner)view.findViewById(R.id.departmentSpinner);
        addReviewBtn = (Button)view.findViewById(R.id.addReviewBtn);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.courseCodes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deptSpinner.setAdapter(adapter);
        deptSpinner.setOnItemSelectedListener(this);
        lvCourses = view.findViewById(R.id.lvCourses);
        listAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, reviewSummaries);
        lvCourses.setAdapter(listAdapter);
        setupListViewListener();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("reviews")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String review = "";
                            String course = "";
                            String score = "";
                            String comment = "";
                            String reviewSummary = "";
                            String user = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                course = (String) document.get("course");
                                score = document.get("rating").toString();
                                comment = (String) document.get("comment");
                                user = (String) document.get("user");
                                reviewSummary = course + ":        " + score + " stars\n";
                                review = course +"\nUser " + user + " said: " + score + " stars\n\"" + comment + "\"";
                                reviews.add(review);
                                reviewSummaries.add(reviewSummary);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        listAdapter.notifyDataSetChanged();
                    }
                });

        addReviewBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LeaveAReview.class);
                String message = "Class Name";
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);

            }
        });

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedSpinner = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), selectedSpinner, Toast.LENGTH_SHORT);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // Attaches a click listener to the listview
    private void setupListViewListener() {
        lvCourses.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter,
                                            View item, int pos, long id) {
                        // View review within array at position pos
                        String viewedReview = reviews.get(pos);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
}