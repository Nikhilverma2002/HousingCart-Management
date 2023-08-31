package com.example.hcart;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class NoticeAdmin extends Fragment {

    View view;
    TextView date,time,submit;
    EditText title;
    String pushkey;
    ImageView back;
    DatabaseReference reference;
    int t_val=0,d_val=0;
    Context contextNullSafe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_notice_admin, container, false);

        date = view.findViewById(R.id.date);
        time = view.findViewById(R.id.time);
        title = view.findViewById(R.id.title);
        submit = view.findViewById(R.id.submit);
        back = view.findViewById(R.id.back);

        time.setOnClickListener(v->{
                    final Calendar c = Calendar.getInstance();
                    t_val = 1;
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(requireActivity(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {
                                    String am_pm = (hourOfDay < 12) ? "AM" : "PM";
                                    time.setText(hourOfDay + ":" + minute +" "+ am_pm);
                                }
                            }, hour, minute, false);

                    timePickerDialog.show();
                }
        );

        date.setOnClickListener(v->{
            final Calendar c = Calendar.getInstance();
            d_val =1;
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireActivity(),
                    new DatePickerDialog.OnDateSetListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    },
                    year, month, day);
            datePickerDialog.show();
        });


        submit.setOnClickListener(v->{
            if ((title.getText().toString().equals("") )&&( t_val==0) && (d_val==0))
                Toast.makeText(requireActivity(), "Please fill all the details!!", Toast.LENGTH_SHORT).show();
            else
                sendData();
        });

        back.setOnClickListener(v -> {
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().remove(NoticeAdmin.this).commit();
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

        return view;
    }

    public void sendData(){

        reference = FirebaseDatabase.getInstance().getReference();
        pushkey = reference.push().getKey();
        reference.child("Meeting").child(pushkey).child("title").setValue(title.getText().toString().trim());
        reference.child("Meeting").child(pushkey).child("time").setValue(time.getText().toString().trim());
        reference.child("Meeting").child(pushkey).child("date").setValue(date.getText().toString().trim());
        Toast.makeText(requireActivity(), "Notified!", Toast.LENGTH_SHORT).show();
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().remove(NoticeAdmin.this).commit();
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