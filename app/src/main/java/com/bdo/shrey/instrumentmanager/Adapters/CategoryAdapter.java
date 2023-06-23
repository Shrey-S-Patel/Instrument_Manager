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
import com.bdo.shrey.instrumentmanager.Models.Category;
import com.bdo.shrey.instrumentmanager.R;
import com.bdo.shrey.instrumentmanager.StocktakeActivity;
import com.bdo.shrey.instrumentmanager.ViewInstrumentActivity;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    Context context;
    ArrayList<Category> category_list;

    public CategoryAdapter(Context context, ArrayList<Category> category_list) {
        this.context = context;
        this.category_list = category_list;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.category_viewholder, parent, false);
        return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = category_list.get(position);
        holder.name.setText(category.getCat_name());
        holder.code.setText(category.getCat_code());
        holder.count.setText(String.valueOf(category.getCat_count()));

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), AllInstrumentsActivity.class);
                intent.putExtra("category", category.getCat_name());
                context.startActivity(intent);
                Toast.makeText(context.getApplicationContext(), "View Instruments", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                Toast.makeText(context, "I was pressed too long ;)", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context.getApplicationContext(), StocktakeActivity.class);
                intent.putExtra("category", category.getCat_name());
                context.startActivity(intent);
                Toast.makeText(context.getApplicationContext(), "Stocktake", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return category_list.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView btn;
        TextView name, code, count;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            btn = itemView.findViewById(R.id.img_btn);
            name = itemView.findViewById(R.id.cat_name_vh);
            code = itemView.findViewById(R.id.cat_code_vh);
            count = itemView.findViewById(R.id.cat_count_vh);
        }
    }
}
