package com.example.hcart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import com.example.hcart.Adapter.IntroViewPagerAdapter;
import com.example.hcart.Model.ScreenItem;

public class Onboarding extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    LinearLayout btnNext;
    int position = 0;
    FirebaseAuth auth;
    FirebaseUser user;
    LinearLayout btnGetStarted;
    Animation btnAnim;
    TextView tvSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // when this activity is about to be launch we need to check if its openened before or not
        setContentView(R.layout.activity_onboarding);


        Window window = Onboarding.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(Onboarding.this, R.color.white));

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // ini views
        btnNext = findViewById(R.id.next);
        btnGetStarted = findViewById(R.id.getstarted);
        tabIndicator = findViewById(R.id.tab_indicator);
        //btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);
        tvSkip = findViewById(R.id.tv_skip);
        // fill list screen
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Mark Your Attendance", "Get information of your attendance.", R.drawable.ic_onboardlist));
        mList.add(new ScreenItem("Meetings", "Get notified for any office meetings.", R.drawable.ic_meet));
        mList.add(new ScreenItem("Task", "Get in touch with task assigned to you.", R.drawable.ic_task));
        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);
        // setup tablayout with viewpager
        tabIndicator.setupWithViewPager(screenPager);
        // next button click Listner


        btnNext.setOnClickListener(v -> {
            position = screenPager.getCurrentItem();
            if (position < mList.size()) {
                position++;
                screenPager.setCurrentItem(position);
            }
            if (position == mList.size() - 1) { // when we rech to the last screen
                // TODO : show the GETSTARTED Button and hide the indicator and the next button
                loaddLastScreen();
            }
        });
        // tablayout add change listener
        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size() - 1) {
                    loaddLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        // Get Started button click listener
        btnGetStarted.setOnClickListener(v -> {
            //open main activity
            Intent mainActivity = new Intent(getApplicationContext(), Login.class);
            startActivity(mainActivity);
            // also we need to save a boolean value to storage so next time when the user run the app
            // we could know that he is already checked the intro screen activity
            // i'm going to use shared preferences to that process
            //savePrefsData();
            finish();
        });
        // skip button click listener
        tvSkip.setOnClickListener(v -> screenPager.setCurrentItem(mList.size()));

    }


    private void loaddLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        //tabIndicator.setVisibility(View.INVISIBLE);
        // TODO : ADD an animation the getstarted button
        // setup animation
        //btnGetStarted.setAnimation(btnAnim);
    }

    @Override
    protected void onStart() {
        super.onStart();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        String login =getSharedPreferences("login?",MODE_PRIVATE)
                .getString("Yes","none");


        if (user != null) {
            if (login.equals("login_done")) {
                startActivity(new Intent(Onboarding.this, Dashboard.class));
                finish();
            }
             else {
                startActivity(new Intent(Onboarding.this, Login.class));
                finish();
            }
        }

    }
}