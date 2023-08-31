package com.example.hcart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.example.hcart.Adapter.DetailsAdapter;
import com.example.hcart.Model.DetailsModel;


public class Details extends Fragment {


    View view;
    TextView text,lottie_txt;
    String district;
    EditText inputSearch;
    DatabaseReference reference;
    List<DetailsModel> list;
    String in,out;
    Context contextNullSafe;
    RecyclerView recyclerView;
    ImageView back_btn;
    LinearLayout layout;
    LottieAnimationView lottie;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_details, container, false);
        list = new ArrayList<>();
        text = view.findViewById(R.id.text);
        inputSearch = view.findViewById(R.id.search);
        layout  =  view.findViewById(R.id.layout);
        lottie = view.findViewById(R.id.animation_empty);
        lottie_txt = view.findViewById(R.id.text_lottie);

        if(getArguments()!=null) {
            district = getArguments().getString("Date");
        }
        DetailsAdapter adapter = new DetailsAdapter(getContextNullSafety(), list, district,in,out);
        text.setText(district);

        recyclerView = view.findViewById(R.id.rv2);
        LinearLayoutManager mManager = new LinearLayoutManager(getContextNullSafety());
        recyclerView.setItemViewCacheSize(500);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(mManager);
        reference = FirebaseDatabase.getInstance().getReference().child("Attendance").child(district);
        back_btn = view.findViewById(R.id.back);


        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (inputSearch.getText().toString().equals("")) {
                    adapter.setTasks(list);
                }
                else if(list.size() != 0){
                    adapter.searchNotes(s.toString());
                }
            }
        });

        back_btn.setOnClickListener(v -> {
            FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
            FragmentTransaction ft=fm.beginTransaction();
            if(fm.getBackStackEntryCount()>0) {
                fm.popBackStack();
            }
            ft.commit();
        });

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    DetailsModel stationData = new DetailsModel(snapshot.child(Objects.requireNonNull(ds.getKey())).child("name").getValue(String.class), snapshot.child(Objects.requireNonNull(ds.getKey())).child("time").child("IN").getValue(String.class), snapshot.child(Objects.requireNonNull(ds.getKey())).child("time").child("OUT").getValue(String.class), snapshot.child(Objects.requireNonNull(ds.getKey())).child("in_str").getValue(String.class), snapshot.child(Objects.requireNonNull(ds.getKey())).child("out_str").getValue(String.class));
                    list.add(stationData);
                    //in = snapshot.child(Objects.requireNonNull(ds.getKey())).child("time").child("IN").getValue(String.class);
                    //out = snapshot.child(Objects.requireNonNull(ds.getKey())).child("time").child("OUT").getValue(String.class);
                    //list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(DetailsModel.class));
                }
                Collections.reverse(list);

                lottie.setVisibility(View.GONE);
                lottie_txt.setVisibility(View.GONE);
                DetailsAdapter details =new DetailsAdapter(getContextNullSafety(),list,district,in,out);
                recyclerView.setAdapter(details);
                details.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                if(fm.getBackStackEntryCount()>0) {
                    fm.popBackStack();
                }
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
        return view;
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
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
    }
}