package com.example.hcart.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hcart.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.hcart.Model.DetailsModel;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder> {

    List<DetailsModel> list;
    Context context;
    String district;
    String time_in;
    String time_out;
    TextView yes,no;
    boolean isadmin;
    private Timer timer;


    public DetailsAdapter(Context context, List<DetailsModel> list, String district,String  time_in, String time_out) {
        this.context = context;
        this.list = list;
        this.district = district;
        this.time_in = time_in;
        this.time_out = time_out;
        isadmin=context.getSharedPreferences("isAdmin_or_not", MODE_PRIVATE)
                .getBoolean("authorizing_admin",false);
    }

    public void setTasks(List<DetailsModel> todoList) {
        this.list = todoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_details, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String name = list.get(position).getName();


        holder.name.setText(name);
        holder.in_time.setText(list.get(position).getIn_time());
        holder.out_time.setText(list.get(position).getOut_time());
        //holder.out_time.setText(time_out);


        String in = list.get(position).getIn_str();
        String out = list.get(position).getOut_str();

        if ((in.equals("OnTime"))&&(out.equals("Late"))) {
            holder.layout.setBackgroundResource(R.drawable.bg_card_orange);
        }
        else if ((in.equals("Late"))&&(out.equals("Late"))) {
            holder.layout.setBackgroundResource(R.drawable.bg_card_red);
        }
        else if((in.equals("Late"))&&(out.equals("OnTime"))){
            holder.layout.setBackgroundResource(R.drawable.bg_card_orange);
        }
        //else
           // holder.layout.setBackgroundResource(R.drawable.bg_card_orange);


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,in_time,out_time;
        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            in_time = itemView.findViewById(R.id.in_time);
            out_time = itemView.findViewById(R.id.out_time);
            layout = itemView.findViewById(R.id.layout);
        }
    }

    public void searchNotes(final String searchKeyword){
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {

                if (searchKeyword.trim().isEmpty()){
                    ArrayList<DetailsModel> xyz = new ArrayList<>();
                    for (DetailsModel mode : list){
                        xyz.add(mode);
                    }
                    list = xyz;
                }
                else{
                    ArrayList<DetailsModel> temp = new ArrayList<>();
                    for (DetailsModel note: list){
                        if (note.getName().toLowerCase().contains(searchKeyword.toLowerCase()))
                        {
                            temp.add(note);
                        }
                    }
                    list = temp;
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

