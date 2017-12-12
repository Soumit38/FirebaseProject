package com.soumit.firebaseauthentication.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.soumit.firebaseauthentication.Others.BookingActivity;
import com.soumit.firebaseauthentication.Models.Doctor;
import com.soumit.firebaseauthentication.R;

import java.util.List;

/**
 * Created by Soumit on 11/23/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private Context context;
    private List<Doctor> listitems;

    public MyAdapter(Context context, List listItem){
        this.context = context;
        this.listitems = listItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        Doctor item = listitems.get(position);
        holder.name.setText(item.name);
        holder.description.setText(item.email);
    }

    @Override
    public int getItemCount() {
        return listitems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView name;
        private TextView description;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            name = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            Doctor item = listitems.get(position);

            Toast.makeText(context, item.name, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, BookingActivity.class);
            intent.putExtra("userid", item.userid);
            intent.putExtra("name", item.name);
            intent.putExtra("address", item.address);
            intent.putExtra("qualifications", item.qualifications);
            intent.putExtra("visitingHours", item.visitingHours);
            intent.putExtra("description", item.email);

            context.startActivity(intent);
            listitems.clear();
        }
    }

}










