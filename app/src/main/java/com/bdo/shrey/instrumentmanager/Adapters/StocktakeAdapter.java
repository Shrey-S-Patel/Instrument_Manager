package com.bdo.shrey.instrumentmanager.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bdo.shrey.instrumentmanager.Models.Assign;
import com.bdo.shrey.instrumentmanager.Models.Instrument;
import com.bdo.shrey.instrumentmanager.Models.Stock;
import com.bdo.shrey.instrumentmanager.R;
import com.bdo.shrey.instrumentmanager.ScanActivity;
import com.bdo.shrey.instrumentmanager.ScanStockActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StocktakeAdapter extends RecyclerView.Adapter<StocktakeAdapter.StocktakeViewHolder>{

    Context context;
    ArrayList<Instrument> instrument_list;
    private final DatabaseReference assign_ref = FirebaseDatabase.getInstance().getReference("Assignments");
    private final DatabaseReference stock = FirebaseDatabase.getInstance().getReference("Instruments1");

    public StocktakeAdapter(Context context, ArrayList<Instrument> instrument_list) {
        this.context = context;
        this.instrument_list = instrument_list;
    }

    @NonNull
    @Override
    public StocktakeAdapter.StocktakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.stock_viewholder, parent, false);
        return new StocktakeAdapter.StocktakeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StocktakeAdapter.StocktakeViewHolder holder, int position) {
        Instrument instrument = instrument_list.get(position);
        String imageUrl = null;
        imageUrl = instrument.getImg();
        Picasso.get().load(imageUrl).into(holder.qr);
        holder.code.setText(instrument.getId());
        holder.cat.setText(instrument.getCategory());
        holder.loc.setText(instrument.getLocation());

        holder.tick.setTag(1);
        holder.tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.tick.getTag().equals(1)){
                Animation animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.blink);
                holder.tick.setImageResource(R.drawable.checked);
                holder.tick.startAnimation(animation);
                holder.tick.setTag(2);
                }else {
                    Animation animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.blink);
                    holder.tick.setImageResource(R.drawable.cancel);
                    holder.tick.startAnimation(animation);
                    holder.tick.setTag(1);
                }
            }
        });


        assign_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(instrument.getId()).exists()){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.stat.getBackground().setTint(Color.RED);
                    }
                    holder.stat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Assign assign = snapshot.child(instrument.getId()).getValue(Assign.class);
                            String s_name = assign.getS_name();
                            String s_id = assign.getS_id();
                            Toast.makeText(view.getContext(), "Assigned to " + s_name + ", " + s_id, Toast.LENGTH_SHORT).show();

                        }
                    });
                }else{
                    holder.stat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(view.getContext(), "Ready to be Assigned!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context.getApplicationContext(), ScanStockActivity.class);
                i.putExtra("i_id",instrument.getId());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return instrument_list.size();
    }

    public static class StocktakeViewHolder extends RecyclerView.ViewHolder{

        ImageView qr, tick;
        TextView code, cat,loc;
        Button check, stat;

        public StocktakeViewHolder(@NonNull View itemView) {
            super(itemView);
            qr = itemView.findViewById(R.id.img_qr);
            code = itemView.findViewById(R.id.i_code);
            cat = itemView.findViewById(R.id.i_cat);
            loc = itemView.findViewById(R.id.i_loc);
            check = itemView.findViewById(R.id.checkbtn);
            tick = itemView.findViewById(R.id.img_tick);
            stat = itemView.findViewById(R.id.btn_stat);
        }
    }
}
