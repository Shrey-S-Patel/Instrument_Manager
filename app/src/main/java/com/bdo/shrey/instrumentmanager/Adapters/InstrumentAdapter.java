package com.bdo.shrey.instrumentmanager.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bdo.shrey.instrumentmanager.Models.Assign;
import com.bdo.shrey.instrumentmanager.Models.Instrument;
import com.bdo.shrey.instrumentmanager.R;
import com.bdo.shrey.instrumentmanager.ViewInstrumentActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class InstrumentAdapter extends RecyclerView.Adapter<InstrumentAdapter.InstrumentViewHolder> {

    private final DatabaseReference assign_ref = FirebaseDatabase.getInstance().getReference("Assignments");
    Context context;
    ArrayList<Instrument> instrument_list;

    public InstrumentAdapter(Context context, ArrayList<Instrument> instrument_list) {
        this.context = context;
        this.instrument_list = instrument_list;
    }

    @NonNull
    @Override
    public InstrumentAdapter.InstrumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.instrument_viewholder, parent, false);
        return new InstrumentAdapter.InstrumentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InstrumentAdapter.InstrumentViewHolder holder, int position) {
        Instrument instrument = instrument_list.get(position);
        String imageUrl = null;
        imageUrl = instrument.getImg();
        Picasso.get().load(imageUrl).into(holder.qr);
        holder.code.setText(instrument.getId());
        holder.cat.setText(instrument.getCategory());
        holder.loc.setText(instrument.getLocation());
        holder.status.setText(instrument.getStatus());


        if (instrument.getAssigned() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.stat.getBackground().setTint(Color.RED);
            }
            holder.stat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Assigned to " + instrument.getAssigned().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.stat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Ready to be Assigned!", Toast.LENGTH_SHORT).show();
                }
            });
        }


        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), ViewInstrumentActivity.class);
                intent.putExtra("id", instrument.getId());
                intent.putExtra("category", instrument.getCategory());
                intent.putExtra("image", instrument.getImg());
                intent.putExtra("location", instrument.getLocation());
                intent.putExtra("status", instrument.getStatus());
                context.startActivity(intent);
                Toast.makeText(context.getApplicationContext(), "View Instrument", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return instrument_list.size();
    }

    public void searchData(ArrayList<Instrument> search_list) {
        instrument_list = search_list;
        notifyDataSetChanged();
    }

    public static class InstrumentViewHolder extends RecyclerView.ViewHolder {

        ImageView qr;
        TextView code, cat, loc, status;
        Button view, delete, stat;

        public InstrumentViewHolder(@NonNull View itemView) {
            super(itemView);
            qr = itemView.findViewById(R.id.img_qr);
            code = itemView.findViewById(R.id.i_code);
            cat = itemView.findViewById(R.id.i_cat);
            loc = itemView.findViewById(R.id.i_loc);
            status = itemView.findViewById(R.id.i_stat);
            view = itemView.findViewById(R.id.viewbtn);
            delete = itemView.findViewById(R.id.deletebtn);
            stat = itemView.findViewById(R.id.btn_stat);
        }
    }
}
