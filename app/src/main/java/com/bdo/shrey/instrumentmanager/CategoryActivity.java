package com.bdo.shrey.instrumentmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bdo.shrey.instrumentmanager.Adapters.CategoryAdapter;
import com.bdo.shrey.instrumentmanager.Models.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {
    TextView title;
    ImageView back, scanQR;
    SearchView action_search;
    FloatingActionButton add_cat;
    RecyclerView recyclerView;
    FirebaseDatabase database;
    CategoryAdapter cat_adapter;
    ArrayList<Category> cat_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        title = findViewById(R.id.title);
        title.setText(R.string.categories);

        back = findViewById(R.id.back);
        action_search = findViewById(R.id.search);
        scanQR = findViewById(R.id.scanQR);

        action_search.setVisibility(View.INVISIBLE);
        scanQR.setVisibility(View.INVISIBLE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        add_cat = findViewById(R.id.fab_add_cat);
        add_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateCategoryActivity.class);
                startActivity(intent);
            }
        });

        //Code for all Categories to be added into the recyclerview

        recyclerView = findViewById(R.id.recyclerview_all_cat);
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Categories");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cat_list = new ArrayList<>();
        cat_adapter = new CategoryAdapter(this, cat_list);
        recyclerView.setAdapter(cat_adapter);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Category cat = dataSnapshot.getValue(Category.class);
                    cat_list.add(cat);
                }
                cat_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CategoryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}