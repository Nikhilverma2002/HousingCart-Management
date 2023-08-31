package com.example.hcart.Personal_Note;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.hcart.Personal_Note.Utils.DataBaseHandler;
import com.example.hcart.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private Button newTaskSaveButton;
    String today;
    //Simple date format
    Date date = Calendar.getInstance().getTime();
    @SuppressLint("SimpleDateFormat")
    DateFormat formatter = new SimpleDateFormat(" EEEE , dd MMMM yyyy , hh:mm aa");

    private DataBaseHandler db;

    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_task, container, false);
        Objects.requireNonNull(getDialog()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskText = requireView().findViewById(R.id.newTaskText);
        TextView textView_new = requireView().findViewById(R.id.new_date);
        today = formatter.format(date);
        textView_new.setText(today);
        newTaskSaveButton = requireView().findViewById(R.id.newTaskButton);
        boolean isUpdate = false;


        boolean choice;
/*
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (locked) {
//maybe you want to check it by getting the sharedpreferences. Use this instead if (locked)
// if (prefs.getBoolean("locked", locked) {
            prefs.edit().putBoolean("locked", true).commit();
        }*/
        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            //String date = bundle.getString("date");
            newTaskText.setText(task);
           // textView_new.setText(date);
            assert task != null;
            if(task.length()>0)
                newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
        }

        db = new DataBaseHandler(getActivity());
        db.openDatabase();

        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                }
                else{
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(v -> {

            String text = newTaskText.getText().toString();
            //String xyz = textView_new.getText().toString();
            if(newTaskText.getText().toString().equals("")){
                Toast.makeText(getActivity(), "No Task Added!", Toast.LENGTH_SHORT).show();
            }
            else {
                if (finalIsUpdate) {
                    db.updateTask(bundle.getInt("id"), text);
                    //db.updateDate(bundle.getInt("date"),xyz);
                } else {
                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    //task.setDate(xyz);
                    task.setStatus(0);
                    db.insertTask(task);
                }
            }
            dismiss();
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener)
            ((DialogCloseListener)activity).handleDialogClose(dialog);
    }
}