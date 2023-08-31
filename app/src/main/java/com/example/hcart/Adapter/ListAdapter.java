package com.example.hcart.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hcart.Model.UserModel;
import com.example.hcart.Profile;
import com.example.hcart.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

    Context context;
    ArrayList<UserModel> list ;
    DatabaseReference reference;
    TextView yes,no;
    Dialog dialog;



    public ListAdapter(Context context,ArrayList<UserModel> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_employee,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {

        reference = FirebaseDatabase.getInstance().getReference().child("users");

        if (position < list.size()) {
            if (list.get(position).getDp_link()!=null) {
                try {
                    Uri uri = Uri.parse(list.get(position).getDp_link());
                    holder.image.setImageURI(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                try {
                    Uri uri = Uri.parse(String.valueOf(R.drawable.ic_avtar));
                    holder.image.setImageURI(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            holder.name.setText(list.get(position).getName());
            holder.position.setText(list.get(position).getPosition());

            holder.layout.setOnClickListener(v->{
                Profile profile = new Profile();
                Bundle args = new Bundle();
                args.putString("sending_user_from_sync","addstack");
                args.putString("uid_sending_profile", list.get(position).getUid());
                profile.setArguments(args);
                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .add(R.id.drawer, profile)
                        .addToBackStack(null)
                        .commit();
            });


            /*if (list.get(position).getId().equals("Admin")){
                holder.delete.setVisibility(View.VISIBLE);

                holder.delete.setOnClickListener(v->{
                    dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_delete);
                    dialog.setCancelable(true);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();
                    yes = dialog.findViewById(R.id.yes);
                    no = dialog.findViewById(R.id.no);

                    yes.setOnClickListener(v1->{
                        dialog.dismiss();
                        reference.child(list.get(position).getUid()).removeValue();
                    });


                    no.setOnClickListener(v2->{
                        dialog.dismiss();
                    });
                });
            }*/
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView image;
        TextView name;
        TextView position;
        LinearLayout layout;
        ImageView delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            position = itemView.findViewById(R.id.position);
            image = itemView.findViewById(R.id.my_image_view);
            layout = itemView.findViewById(R.id.layout);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
