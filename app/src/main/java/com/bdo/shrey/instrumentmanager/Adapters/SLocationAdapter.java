package com.bdo.shrey.instrumentmanager.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bdo.shrey.instrumentmanager.AllInstrumentsActivity;
import com.bdo.shrey.instrumentmanager.AllStudentsActivity;
import com.bdo.shrey.instrumentmanager.Models.Category;
import com.bdo.shrey.instrumentmanager.Models.StudentLocation;
import com.bdo.shrey.instrumentmanager.R;

import java.util.ArrayList;

public class SLocationAdapter extends RecyclerView.Adapter<SLocationAdapter.LocationViewHolder> {
    Context context;
    ArrayList<StudentLocation> location_list;

    public SLocationAdapter(Context context, ArrayList<StudentLocation> location_list) {
        this.context = context;
        this.location_list = location_list;
    }

    @NonNull
    @Override
    public SLocationAdapter.LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.locations_viewholder, parent, false);
        return new SLocationAdapter.LocationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SLocationAdapter.LocationViewHolder holder, int position) {
        StudentLocation location = location_list.get(position);
        holder.name.setText(location.getLocation());
        holder.code.setText(String.valueOf(location.getCode_count()));
        holder.count.setText(String.valueOf(location.getCount()));

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), AllStudentsActivity.class);
                intent.putExtra("location", location.getLocation());
                context.startActivity(intent);
                Toast.makeText(context.getApplicationContext(), "View Location Students", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return location_list.size();
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {

        ImageView btn;
        TextView name, code, count;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);

            btn = itemView.findViewById(R.id.img_btn);
            name = itemView.findViewById(R.id.loc_name_vh);
            code = itemView.findViewById(R.id.loc_code_vh);
            count = itemView.findViewById(R.id.loc_count_vh);
        }
    }
}
