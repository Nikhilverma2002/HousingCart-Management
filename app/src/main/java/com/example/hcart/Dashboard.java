package com.example.hcart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.example.hcart.databinding.ActivityDashboardBinding;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import www.sanju.motiontoast.MotionToast;

public class Dashboard extends AppCompatActivity {

    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;
    private long pressedTime;
    ActivityDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = FirebaseDatabase.getInstance().getReference().child("admin");
        auth =  FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        connection();

        setStatusBarTransparent();

        Fresco.initialize(
                Dashboard.this,
                ImagePipelineConfig.newBuilder(Dashboard.this)
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());

        binding.personal.setOnClickListener(v->{

            startActivity(new Intent(Dashboard.this, PersonalTask.class));
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        });


        if (user != null){
            //Display User Image from Google Account
            //Objects.requireNonNull() prevents getPhotoUrl() from returning a NullPointerException
            String personImage = Objects.requireNonNull(user.getPhotoUrl()).toString();

            Glide.with(Dashboard.this).load(personImage).into(binding.profileImage);
        }

        binding.profileImage.setOnClickListener(v->{
            Dashboard.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,new Profile())
                    .addToBackStack(null)
                    .commit();
        });

        binding.scanAdmin.setOnClickListener(v->{
            /*Dashboard.this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.drawer, new QRCode())
                    .commit();*/

            Dashboard.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,new QRCode())
                    .addToBackStack(null)
                    .commit();
        });

        binding.scanUser.setOnClickListener(v->{
            /*Dashboard.this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.drawer, new Scanner())
                    .commit();*/

            Dashboard.this.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,new Scanner())
                    .addToBackStack(null)
                    .commit();
        });



        binding.list.setOnClickListener(v->{
            Dashboard.this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.drawer, new EmployeeList())
                    .commit();
        });


        /*sp_of=getContextNullSafety().getSharedPreferences("Is_SP",MODE_PRIVATE)
                .getString("Yes_of","none");*/

        String admin=getSharedPreferences("Is_admin",MODE_PRIVATE)
                .getString("Yes_of","none");

        if(admin.equals("Admin")){
            binding.scanAdmin.setVisibility(View.VISIBLE);
            binding.scanUser.setVisibility(View.GONE);
            binding.office.setOnClickListener(v->{
                /*Dashboard.this.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.drawer, new NoticeAdmin())
                        .commit();*/

                Dashboard.this.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                        .add(R.id.drawer,new NoticeAdmin())
                        .addToBackStack(null)
                        .commit();
            });

            binding.getDetails.setOnClickListener(v->{
            /*Dashboard.this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.drawer, new Attendance())
                    .commit();*/

                Dashboard.this.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                        .add(R.id.drawer,new Attendance())
                        .addToBackStack(null)
                        .commit();
            });

            binding.addTask.setOnClickListener(v->{
                /*Dashboard.this.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.drawer, new AddTaskAdmin())
                        .commit();*/

                Dashboard.this.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                        .add(R.id.drawer,new AddTaskAdmin())
                        .addToBackStack(null)
                        .commit();
            });

        }
        else{
            binding.scanUser.setVisibility(View.VISIBLE);
            binding.scanAdmin.setVisibility(View.GONE);
            binding.office.setOnClickListener(v->{
                /*Dashboard.this.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.drawer, new NoticeUser())
                        .commit();*/

                Dashboard.this.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                        .add(R.id.drawer,new NoticeUser())
                        .addToBackStack(null)
                        .commit();
            });

            binding.addTask.setOnClickListener(v->{
               /* Dashboard.this.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.drawer, new AddTaskAdmin())
                        .commit();*/
                //Toast.makeText(this, "Admin panel completed user ongoing", Toast.LENGTH_SHORT).show();

                Dashboard.this.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                        .add(R.id.drawer,new TaskUser())
                        .addToBackStack(null)
                        .commit();
            });


            binding.getDetails.setOnClickListener(v->{

                Dashboard.this.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                        .add(R.id.drawer,new Attendance())
                        .addToBackStack(null)
                        .commit();
            });

        }


    }

    @Override
    protected void onStart() {
        super.onStart();

/*
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (Objects.requireNonNull(Objects.requireNonNull(ds.getKey()).trim()).equals(user.getUid().trim())) {
                        binding.scanAdmin.setVisibility(View.VISIBLE);
                        binding.scanUser.setVisibility(View.GONE);
                    }
                    else {
                        binding.scanUser.setVisibility(View.VISIBLE);
                        binding.scanAdmin.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    private void connection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

        if (!connected){
            MotionToast.Companion.darkColorToast(Dashboard.this,
                    "No Internet",
                    "Connect with mobile network",
                    MotionToast.TOAST_NO_INTERNET,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(Dashboard.this, R.font.lexend));
        }
    }
    private void setStatusBarTransparent () {
        Window window = Dashboard.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }
}