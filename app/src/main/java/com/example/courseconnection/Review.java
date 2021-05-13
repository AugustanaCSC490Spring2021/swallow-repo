package com.example.courseconnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Review#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Review extends Fragment implements AdapterView.OnItemSelectedListener {

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
    private EditText courseNumberEdit;
    private TextView emptyText;
    private FirebaseFirestore db;
    private ListenerRegistration registration;
    private final int LEAVE_REVIEW = 1;

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
        reviewSummaries.clear();
        reviews.clear();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        deptSpinner = (Spinner)view.findViewById(R.id.departmentSpinner);
        addReviewBtn = (Button)view.findViewById(R.id.addReviewBtn);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.courseCodes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deptSpinner.setAdapter(adapter);
        deptSpinner.setOnItemSelectedListener(this);
        lvCourses = view.findViewById(R.id.lvCourses);
        emptyText = (TextView)view.findViewById(R.id.empty);
        lvCourses.setEmptyView(emptyText);
        listAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, reviewSummaries);
        lvCourses.setAdapter(listAdapter);
        setupListViewListener();

        courseNumberEdit = (EditText)view.findViewById(R.id.courseNumberView);
        courseNumberEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    String courseCode = deptSpinner.getSelectedItem().toString();
                    String courseNum = courseNumberEdit.getText().toString();
                    if (courseNum.equals(""))
                    {
                        populateList(courseCode);
                    }
                    else {
                        populateList(courseCode, courseNum);
                    }
                    return true;
                }
                return false;
            }
        });

        addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LeaveAReview.class);
                startActivityForResult(intent,LEAVE_REVIEW);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (LEAVE_REVIEW) : {
                if (resultCode == Activity.RESULT_OK) {
                    // if the review was left successfully, do this
                    reviewSummaries.clear();
                    reviews.clear();
                    listAdapter.notifyDataSetChanged();
                }
                break;
            }
        }
    }

    // runs when something is selected on spinner, filters reviews to only show ones matching user input
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        reviewSummaries.clear();
        reviews.clear();
        listAdapter.notifyDataSetChanged();

        if(position == 0)
        {
            populateList();
        }
        else
        {
            String selectedSpinner = parent.getItemAtPosition(position).toString();
            // clear any selected course number
            courseNumberEdit.setText("");
            // fill list with only courses from the desired course code
            populateList(selectedSpinner);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // unused override
    }

    // Attaches a click listener to the ListView
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
                            }
                        });
                        builder.setTitle("Review");
                        builder.setMessage(viewedReview);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
    }

    private void populateList()
    {
        db = FirebaseFirestore.getInstance();
        Query query = db.collection("reviews").orderBy("courseCode");
        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                reviewSummaries.clear();
                reviews.clear();
                listAdapter.notifyDataSetChanged();

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                for (QueryDocumentSnapshot document : value) {
                    String courseCode = (String) document.get("courseCode");
                    String courseNum = (String) document.get("courseNum");
                    String course = courseCode + "-" + courseNum;
                    String score = document.get("rating").toString();
                    String comment = (String) document.get("comment");
                    String user = (String) document.get("user");
                    if (document.get("date") != null){
                        com.google.firebase.Timestamp time = (com.google.firebase.Timestamp) document.get("date");
                        long milliseconds = time.getSeconds() * 1000 + time.getNanoseconds()/1000000;
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(milliseconds);

                        int mYear = calendar.get(Calendar.YEAR);
                        int mMonth = calendar.get(Calendar.MONTH) + 1;
                        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                        String date = String.format("%s/%s/%s", mMonth,mDay,mYear);
                        String review = course + "\nDate of review: " + date + "\nUser " + user + " said: " + score + " stars\n\"" + comment + "\"";
                        String reviewSummary = course + ":        " + score + " stars on " + date +"\n";
                        reviewSummaries.add(reviewSummary);
                        reviews.add(review);
                    }
                }
                listAdapter.notifyDataSetChanged();
            }
        });
    };

    private void populateList(String courseCode)
    {
        db = FirebaseFirestore.getInstance();
        Query query = db.collection("reviews")
                .whereEqualTo("courseCode", courseCode).orderBy("courseNum");
        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                reviewSummaries.clear();
                reviews.clear();
                listAdapter.notifyDataSetChanged();

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                for (QueryDocumentSnapshot document : value) {
                    String courseCode = (String) document.get("courseCode");
                    String courseNum = (String) document.get("courseNum");
                    String course = courseCode + "-" + courseNum;
                    String score = document.get("rating").toString();
                    String comment = (String) document.get("comment");
                    String user = (String) document.get("user");
                    if (document.get("date") != null){
                        com.google.firebase.Timestamp time = (com.google.firebase.Timestamp) document.get("date");
                        long milliseconds = time.getSeconds() * 1000 + time.getNanoseconds()/1000000;
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(milliseconds);

                        int mYear = calendar.get(Calendar.YEAR);
                        int mMonth = calendar.get(Calendar.MONTH) + 1;
                        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                        String date = String.format("%s/%s/%s", mMonth,mDay,mYear);
                        String review = course + "\nDate of review: " + date + "\nUser " + user + " said: " + score + " stars\n\"" + comment + "\"";
                        String reviewSummary = course + ":        " + score + " stars on " + date +"\n";
                        reviewSummaries.add(reviewSummary);
                        reviews.add(review);
                    }
                }
                listAdapter.notifyDataSetChanged();
            }
        });
    };

    private void populateList(String courseCode, String courseNum)
    {
        db = FirebaseFirestore.getInstance();
        Query query = db.collection("reviews")
                .whereEqualTo("courseCode", courseCode)
                .whereEqualTo("courseNum", courseNum);
        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                reviewSummaries.clear();
                reviews.clear();
                listAdapter.notifyDataSetChanged();

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                for (QueryDocumentSnapshot document : value) {
                    String courseCode = (String) document.get("courseCode");
                    String courseNum = (String) document.get("courseNum");
                    String course = courseCode + "-" + courseNum;
                    String score = document.get("rating").toString();
                    String comment = (String) document.get("comment");
                    String user = (String) document.get("user");
                    if (document.get("date") != null){
                        com.google.firebase.Timestamp time = (com.google.firebase.Timestamp) document.get("date");
                        long milliseconds = time.getSeconds() * 1000 + time.getNanoseconds()/1000000;
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(milliseconds);

                        int mYear = calendar.get(Calendar.YEAR);
                        int mMonth = calendar.get(Calendar.MONTH) + 1;
                        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                        String date = String.format("%s/%s/%s", mMonth,mDay,mYear);
                        String review = course + "\nDate of review: " + date + "\nUser " + user + " said: " + score + " stars\n\"" + comment + "\"";
                        String reviewSummary = course + ":        " + score + " stars on " + date +"\n";
                        reviewSummaries.add(reviewSummary);
                        reviews.add(review);
                    }
                }
                listAdapter.notifyDataSetChanged();
            }
        });
    };
}