package com.example.courseconnection;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.viewpager.widget.ViewPager;

        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;

        import com.google.android.material.tabs.TabItem;
        import com.google.android.material.tabs.TabLayout;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {

    private TabLayout tablayout;
    private ViewPager viewPager;
    private TabItem leaderboard, reviews, forums;
    private PageAdapter pageAdapter;
    private Button logOutBtn;
    private TextView userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userInfo = findViewById(R.id.userInfoTxt);
        if (user != null){
            String name = user.getDisplayName();
            String email = user.getEmail();
            userInfo.setText("Currently signed in as: " + name + " using email: " + email);
        }

        logOutBtn = findViewById(R.id.signOutBtn);
        tablayout = findViewById(R.id.tabs);
        leaderboard = findViewById(R.id.Leaderboard);
        reviews = findViewById(R.id.Reviews);
        forums = findViewById(R.id.Forums);
        viewPager = findViewById(R.id.viewpager);

        pageAdapter = new PageAdapter(getSupportFragmentManager(), tablayout.getTabCount());
        viewPager.setAdapter(pageAdapter);
        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0){
                    pageAdapter.notifyDataSetChanged();
                } else if (tab.getPosition() == 1){
                    pageAdapter.notifyDataSetChanged();
                } else if (tab.getPosition() == 2){
                    pageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        finish();
    }
}