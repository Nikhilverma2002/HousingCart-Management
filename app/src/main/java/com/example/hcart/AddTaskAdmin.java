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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import com.example.hcart.Adapter.UserAdapter;
import com.example.hcart.DB.TinyDB;
import com.example.hcart.Model.UserModel;

public class AddTaskAdmin extends Fragment {


    View view;
    Context contextNullSafe;
    TextView assign_to;
    RecyclerView recyclerView;
    DatabaseReference reference,query;
    UserAdapter userAdapter;

    ImageView back;
    ArrayList<String> selection_list=new ArrayList<>();
    FirebaseAuth auth;
    FirebaseUser user;
    String pushkey;
    TextView submit;
    ArrayList<UserModel> list;
    TinyDB tinyDB;
    EditText title,body;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_task_admin, container, false);

        auth =  FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        submit = view.findViewById(R.id.submit);
        assign_to = view.findViewById(R.id.assign_to);
        recyclerView = view.findViewById(R.id.rv);
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        query = FirebaseDatabase.getInstance().getReference().child("task");
        list = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContextNullSafety());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setItemViewCacheSize(500);
        recyclerView.setLayoutManager(layoutManager);

        title = view.findViewById(R.id.title);
        body = view.findViewById(R.id.body);
        back = view.findViewById(R.id.back);

        tinyDB=new TinyDB(getContextNullSafety());
        selection_list = tinyDB.getListString("districts_list");

        submit.setOnClickListener(v->{
           /* pushkey = reference.push().getKey();

            if (!title.getText().toString().equals("")) {
                if (!body.getText().toString().equals("")){
                    query.child(selection_list.get(0)).child(pushkey).child("title").setValue(title.getText().toString().trim());
                    query.child(selection_list.get(0)).child(pushkey).child("body").setValue(body.getText().toString().trim());
                }
            }
            else {
                Toast.makeText(getActivity(), "Please fill all the details!", Toast.LENGTH_SHORT).show();
            }
*/
            Toast.makeText(requireActivity(), "DATA SENT", Toast.LENGTH_SHORT).show();
        });

        back.setOnClickListener(v -> {
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().remove(AddTaskAdmin.this).commit();
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm = ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        getList();
        assign_to.setOnClickListener(v->{
            recyclerView.setVisibility(View.VISIBLE);
        });

        return view;
    }

    public void getList() {
        list.clear();
        //mylist.clear();


        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(UserModel.class));
                }

                    /*loadimage.setVisibility(View.GONE);
                    loadText.setVisibility(View.GONE);*/
                userAdapter = new UserAdapter(getContextNullSafety(), list);
                userAdapter.notifyDataSetChanged();
                if (recyclerView != null)
                    recyclerView.setAdapter(userAdapter);
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