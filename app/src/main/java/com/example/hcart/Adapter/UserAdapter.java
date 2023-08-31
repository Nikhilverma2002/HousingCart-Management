package com.example.hcart.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hcart.R;

import java.util.ArrayList;

import com.example.hcart.DB.TinyDB;
import com.example.hcart.Model.UserModel;

public class UserAdapter  extends RecyclerView.Adapter<UserAdapter.ViewHolder>{


    Context context;
    ArrayList<UserModel> list ;
    String value;
    TinyDB tinydb;

    public UserAdapter(Context context,ArrayList<UserModel> list){
        this.context = context;
        this.list = list;
        tinydb=new TinyDB(context);
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.task_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {


        holder.checkBox.setText(list.get(position).getName());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.check);
        }
    }
}
