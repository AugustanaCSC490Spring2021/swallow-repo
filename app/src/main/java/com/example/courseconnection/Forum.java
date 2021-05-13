package com.example.courseconnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Forum#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Forum extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "";
    private Spinner deptSpinner;
    private Button addForumPostBtn;
    private ListView lvCourses;
    private List<String> postSummaries = new ArrayList<>();
    private List<String> posts = new ArrayList<>();
    private ArrayAdapter listAdapter;
    private EditText courseNumberEdit;
    private TextView emptyText;
    private FirebaseFirestore db;
    private ListenerRegistration registration;
    private final int NEW_POST = 2;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Forum() {
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
        postSummaries.clear();
        posts.clear();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        deptSpinner = (Spinner)view.findViewById(R.id.departmentSpinner);
        addForumPostBtn = (Button)view.findViewById(R.id.addForumPostBtn);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(), R.array.courseCodes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deptSpinner.setAdapter(adapter);
        lvCourses = view.findViewById(R.id.lvCourses);
        emptyText = (TextView)view.findViewById(R.id.empty);
        lvCourses.setEmptyView(emptyText);
        listAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, postSummaries);
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

                    populateList(courseCode, courseNum);

                    return true;
                }
                return false;
            }
        });

        addForumPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewForumPost.class);
                startActivityForResult(intent,NEW_POST);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (NEW_POST) : {
                if (resultCode == Activity.RESULT_OK) {
                    // if the post was made successfully, do this
                    postSummaries.clear();
                    posts.clear();
                    listAdapter.notifyDataSetChanged();
                }
                break;
            }
        }
    }

    // Attaches a click listener to the ListView
    private void setupListViewListener() {
        lvCourses.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter,
                                            View item, int pos, long id) {
                        // View review within array at position pos
                        String viewedReview = posts.get(pos);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.setTitle("Post");
                        builder.setMessage(viewedReview);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
    }

    private void populateList(String courseCode, String courseNum)
    {
        postSummaries.clear();
        posts.clear();
        listAdapter.notifyDataSetChanged();

        String courseTitle = courseCode + "-" + courseNum;
        db = FirebaseFirestore.getInstance();
        Log.wtf("-----", "populating using course " + courseTitle);
        Query query = db.collection("forum_posts")
                .whereEqualTo("course", courseTitle).orderBy("date", Query.Direction.DESCENDING);
        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                for (QueryDocumentSnapshot document : value) {
                    String course = (String) document.get("course");
                    String text = (String) document.get("text");
                    String user = (String) document.get("user");
                    String textBeginning = text;
                    if (text.length() >= 20) {
                        textBeginning = text.substring(0, 20) + "...";
                    }

                    com.google.firebase.Timestamp time = (com.google.firebase.Timestamp) document.get("date");
                    if (time == null){
                        String postSummary = course + ":\nLess than a minute ago\n" + user + ": " + textBeginning;
                        String post = course + "\nPosted less than a minute ago\nUser " + user + " said: \n\"" + text + "\"";
                        postSummaries.add(postSummary);
                        posts.add(post);
                    }
                    else
                    {
                        long milliseconds = time.getSeconds() * 1000 + time.getNanoseconds()/1000000;
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(milliseconds);
                        int mYear = calendar.get(Calendar.YEAR);
                        int mMonth = calendar.get(Calendar.MONTH) + 1;
                        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                        LocalDateTime localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.of("America/Chicago"));
                        String minute = String.valueOf(localDate.getMinute());
                        if (localDate.getMinute() < 10){
                            minute = "0" + minute;
                        }

                        String postTime = localDate.getHour() + ":" + minute + " CST";

                        String date = String.format("%s/%s/%s", mMonth,mDay,mYear);
                        String postSummary = course + ":\n" + date + " at " + postTime + "\n" + user + ": " + textBeginning;
                        String post = course + "\nPosted on: " + date + " at " + postTime + "\nUser " + user + " said: \n\"" + text + "\"";
                        postSummaries.add(postSummary);
                        posts.add(post);
                    }
                }
                listAdapter.notifyDataSetChanged();
            }
        });
    };
}