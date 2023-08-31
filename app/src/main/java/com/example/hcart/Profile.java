package com.example.hcart;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Profile extends Fragment {


    View view;
    SimpleDraweeView profile_image;
    DatabaseReference reference;
    Context contextNullSafe;
    FirebaseUser user;
    FirebaseAuth auth;
    SwipeRefreshLayout mSwipeRefreshLayout;

    ImageView male,female,edit,mail,call,back;
    String dp_uri = "",nam = "",num = "",em = "",ad = "",gen = "",email_body,pos,total="",pres="";
    TextView name,number,email,adhaar,working,present,performance;
    ProgressBar progressBar;
    String uid_of_user,addtostack;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_profile, container, false);

        if (contextNullSafe == null) getContextNullSafety();


        try {
//            assert getArguments() != null;
            addtostack=getArguments().getString("sending_user_from_sync");
            uid_of_user = getArguments().getString("uid_sending_profile");

        } catch (Exception e) {
            e.printStackTrace();
        }

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if(uid_of_user==null) {
            assert user != null;
            uid_of_user = user.getUid();//check for uid bundle if yes then don't do this and vice-versa.
        }

        name =view.findViewById(R.id.name);
        profile_image = view.findViewById(R.id.profile_image);
        edit = view.findViewById(R.id.edit);
        number = view.findViewById(R.id.number);
        email = view.findViewById(R.id.email);
        adhaar = view.findViewById(R.id.adhaar);
        working = view.findViewById(R.id.working);
        present = view.findViewById(R.id.present);
        performance = view.findViewById(R.id.performance);
        male = view.findViewById(R.id.gender_male);
        female = view.findViewById(R.id.gender_female);
        mail = view.findViewById(R.id.mail);
        call = view.findViewById(R.id.call);
        progressBar = view.findViewById(R.id.progress_bar);
        back = view.findViewById(R.id.next);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout);

        reference = FirebaseDatabase.getInstance().getReference().child("users");

        mSwipeRefreshLayout.setOnRefreshListener(this::valueGetting);
        valueGetting();



        number.setOnClickListener(v->{
            callNumber(num);
        });

        email.setOnClickListener(v->{
            sendEmail(em,email_body);
        });


        if (uid_of_user.equals(user.getUid())) {
            edit.setVisibility(View.VISIBLE);
            edit.setOnClickListener(v->{
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment myFragment = new EditProfile();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_main, myFragment).addToBackStack(null).commit();
            });
        }

        back.setOnClickListener(v->{
            back();
        });


        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(addtostack!=null){
                    FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                    FragmentTransaction ft=fm.beginTransaction();
                    if(fm.getBackStackEntryCount()>0) {
                        fm.popBackStack();
                    }
                    ft.commit();
                }
                else {
                    if (((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.drawer) != null) {
                        ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                                .beginTransaction().
                                remove(Objects.requireNonNull(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.drawer))).commit();
                    }
                    ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.drawer, new EmployeeList())
                            .commit();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);


        reference = FirebaseDatabase.getInstance().getReference().child("working");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                total = snapshot.getValue(String.class);
                working.setText(total);
                Log.e("float_check",pres);
                Log.e("float_check1",total);
                 getProgress(total,pres);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });



        return view;
    }



    public void callNumber(String num){
        String phone = "+91" +  num;
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        getContextNullSafety().startActivity(intent);
    }

    public void back(){
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().remove(Profile.this).commit();
    }


    public void sendEmail(String mail,String mail_body){

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:"
                + mail
                + "?subject=" + "" + "&body=" + mail_body);
        intent.setData(data);
        startActivity(intent);
    }

    public void getProgress(String total,String present){
        float At = Integer.parseInt(present);
        float Tl = Integer.parseInt(total);

        float percen = (At/Tl)*100;
        performance.setText(String.valueOf(String.format("%.0f", percen)+"%"));
        progressBar.setProgress((int) percen);
    }
    private void valueGetting() {

        reference = FirebaseDatabase.getInstance().getReference().child("users").child(uid_of_user);
        mSwipeRefreshLayout.setRefreshing(true);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {


                    dp_uri = snapshot.child("dp_link").getValue(String.class);
                    nam = snapshot.child("name").getValue(String.class);
                    num = snapshot.child("number").getValue(String.class);
                    em = snapshot.child("email").getValue(String.class);
                    ad = snapshot.child("adhaar").getValue(String.class);
                    gen = snapshot.child("gender").getValue(String.class);
                    pos = snapshot.child("position").getValue(String.class);
                    pres = snapshot.child("present").getValue(String.class);


                    email_body = "Hi! I am " + nam + " (" + pos + ")" + " from Housing Cart,";

                    assert dp_uri != null;
                    if (!dp_uri.equals("")) {
                        try {
                            Uri uri = Uri.parse(dp_uri);
                            profile_image.setImageURI(uri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        try {
                            Uri uri = Uri.parse(String.valueOf(R.drawable.ic_avtar));
                            profile_image.setImageURI(uri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                    assert gen != null;
                    if (gen.equals("Male")) {
                       male.setVisibility(View.VISIBLE);
                       female.setVisibility(View.GONE);
                    }
                    else if (gen.equals("Female")) {
                        male.setVisibility(View.GONE);
                        female.setVisibility(View.VISIBLE);
                    }

                    name.setText(nam);
                    number.setText("+91 " + num);
                    email.setText(em);
                    adhaar.setText(ad);
                    present.setText(pres);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public Context getContextNullSafety() {
        if (getContext() != null) return getContext();
        if (getActivity() != null) return getActivity();
        if (contextNullSafe != null) return contextNullSafe;
        if (getView() != null && getView().getContext() != null) return getView().getContext();
        if (requireContext() != null) return requireContext();
        if (requireActivity() != null) return requireActivity();
        if (requireView() != null && requireView().getContext() != null)
            return requireView().getContext();

        return null;

    }
}