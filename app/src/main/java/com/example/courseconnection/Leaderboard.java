package com.example.courseconnection;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Leaderboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Leaderboard extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "";
    private List<String> courses = new ArrayList<>();
    private ArrayAdapter listAdapter;
    private Spinner departmentSpinner, entriesSpinner;
    private ListView lvCourses;
    private FirebaseFirestore db;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Leaderboard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Leaderboard.
     */
    // TODO: Rename and change types and number of parameters
    public static Leaderboard newInstance(String param1, String param2) {
        Leaderboard fragment = new Leaderboard();
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
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        departmentSpinner = (Spinner)view.findViewById(R.id.departmentSpinner2);
        entriesSpinner = (Spinner)view.findViewById(R.id.entriesSpinner);

        //Setting up department spinner
        ArrayAdapter<CharSequence> coursesAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.courseCodes, android.R.layout.simple_spinner_item);
        coursesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(coursesAdapter);

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                populateList(departmentSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        List<Integer> entryNums = new ArrayList<>();
        entryNums.add(5);
        entryNums.add(10);
        entryNums.add(15);
        entryNums.add(20);

        ArrayAdapter<CharSequence> entriesAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, entryNums);
        entriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        entriesSpinner.setAdapter(entriesAdapter);

        lvCourses = view.findViewById(R.id.lvCourses);
        listAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, courses);
        lvCourses.setAdapter(listAdapter);

        return view;
    }

    private void populateList(String courseCode)
    {
        courses.clear();
        int size = (int)entriesSpinner.getSelectedItem();
        db = FirebaseFirestore.getInstance();

        db.collection("courses")
                .whereEqualTo("code", courseCode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String course = (String) document.get("Name");
                                String score = document.get("avgRating").toString();
                                String listing = course +": "+score+" /5";
                                courses.add(listing);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    };
}