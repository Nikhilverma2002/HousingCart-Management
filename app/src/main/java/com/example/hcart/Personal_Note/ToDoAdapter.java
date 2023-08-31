package com.example.hcart.Personal_Note;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hcart.PersonalTask;
import com.example.hcart.Personal_Note.Utils.DataBaseHandler;
import com.example.hcart.R;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
    private final DataBaseHandler db;
    private Timer timer;
    private final PersonalTask activity;

    public ToDoAdapter(List<ToDoModel> todoList, DataBaseHandler db, PersonalTask activity) {
        this.db = db;
        this.activity = activity;
        this.todoList = todoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        db.openDatabase();

        final ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTask());
       // holder.textView.setText(item.getDate());
        holder.task.setChecked(toBoolean(item.getStatus()));

        if(holder.task.isChecked()){
            holder.task.setPaintFlags(holder.task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    db.updateStatus(item.getId(), 1);
                    holder.task.setPaintFlags(holder.task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    db.updateStatus(item.getId(), 0);
                    holder.task.setPaintFlags(holder.task.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public Context getContext() {
        return activity;
    }

    public void setTasks(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;
        TextView textView;

        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            //textView = view.findViewById(R.id.date_time_text);
        }
    }
    public void searchNotes(final String searchKeyword){
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (searchKeyword.trim().isEmpty()){
                    ArrayList<ToDoModel> xyz = new ArrayList<>();
                    for (ToDoModel mode : todoList){
                        xyz.add(mode);
                    }
                    todoList = xyz;
                }
                else{
                    ArrayList<ToDoModel> temp = new ArrayList<>();
                    for (ToDoModel note: todoList){
                        if (note.getTask().toLowerCase().contains(searchKeyword.toLowerCase()))
                        {
                            temp.add(note);
                        }
                    }
                    todoList = temp;
                }
                new Handler(Looper.getMainLooper()).post(() -> notifyDataSetChanged());
            }
        }, 100);
    }

    public void cancelTimer(){
        if (timer != null){
            timer.cancel();
        }
    }
}