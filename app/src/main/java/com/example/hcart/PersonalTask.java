package com.example.hcart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.hcart.Personal_Note.AddNewTask;
import com.example.hcart.Personal_Note.DialogCloseListener;
import com.example.hcart.Personal_Note.RecyclerItemTouchHelper;
import com.example.hcart.Personal_Note.ToDoAdapter;
import com.example.hcart.Personal_Note.ToDoModel;
import com.example.hcart.Personal_Note.Utils.DataBaseHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class PersonalTask extends AppCompatActivity implements DialogCloseListener {

    TextView date, heading;
    /*private boolean isFABOpen = false;
    private boolean isfab3Open = false;*/
    String message;
    private DataBaseHandler db;
    private List<ToDoModel> taskList;

    private ToDoAdapter tasksAdapter;
    EditText editText_create;
    LottieAnimationView lottieAnimationView, plus, lottieAnimationView_empty;
    Dialog dialog;
    ImageView delete, eye, eye_cut;
    String today;
    ImageView back;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_task);

        //getting task data

        db = new DataBaseHandler(this);
        db.openDatabase();

        Window window = PersonalTask.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(PersonalTask.this, R.color.white));

        back = findViewById(R.id.back);
        lottieAnimationView_empty = findViewById(R.id.animation_empty);
        textView = findViewById(R.id.text_lottie);

        RecyclerView tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new ToDoAdapter(taskList, db, PersonalTask.this);
        tasksRecyclerView.setAdapter(tasksAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        taskList = db.getAllTasks();
        Collections.reverse(taskList);

        tasksAdapter.setTasks(taskList);

        if (taskList.size() != 0) {
            lottieAnimationView_empty.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }


        back.setOnClickListener(v->{
            finish();
        });


        //delete = findViewById(R.id.delete_task);
        heading = findViewById(R.id.textView5);
        EditText inputSearch = findViewById(R.id.inputSearch_my_task);


        date = findViewById(R.id.current_date);

        Date date_main = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat(" EEEE , dd MMMM yyyy , hh:mm aa");
        today = formatter.format(date_main);

        date.setText(today);

        plus = findViewById(R.id.animation_plus_task);
        plus.setOnClickListener(v -> {

            AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            lottieAnimationView_empty.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        });


        //search
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tasksAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (inputSearch.getText().toString().equals("")) {
                    tasksAdapter.setTasks(taskList);
                } else if (taskList.size() != 0) {
                    tasksAdapter.searchNotes(s.toString());
                }
            }
        });

    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void handleDialogClose(DialogInterface dialog) {
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
    }
}
