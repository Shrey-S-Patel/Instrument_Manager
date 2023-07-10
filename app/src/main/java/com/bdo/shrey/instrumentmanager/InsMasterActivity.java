package com.bdo.shrey.instrumentmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bdo.shrey.instrumentmanager.Adapters.CategoryAdapter;
import com.bdo.shrey.instrumentmanager.Models.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InsMasterActivity extends AppCompatActivity {

    TextView title;
    ImageView back, scanQR;
    SearchView action_search;
    DatabaseReference instruments, instruments1, categories;
    TextView total, pcat;
    long i_total;
    ArrayList<Category> cat_list = new ArrayList<Category>();
    CategoryAdapter cat_adapter;
    RecyclerView rv_cat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ins_master);

        rv_cat = findViewById(R.id.rv_ins_cat);
        rv_cat.setHasFixedSize(true);
        rv_cat.setLayoutManager(new LinearLayoutManager(this));

        total = findViewById(R.id.ins_total);
        pcat = findViewById(R.id.ins_pcat);
        cat_adapter = new CategoryAdapter(this,cat_list);
        rv_cat.setAdapter(cat_adapter);

        instruments = FirebaseDatabase.getInstance().getReference("Instruments");
        instruments1 = FirebaseDatabase.getInstance().getReference("Instruments1");
        categories = FirebaseDatabase.getInstance().getReference("Categories");
        categories.addValueEventListener(new ValueEventListener() {
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
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        instruments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    i_total = snapshot.getChildrenCount();
                    total.setText(String.valueOf(i_total));
//                    Toast.makeText(InsMasterActivity.this, "ITotal = " + i_total, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(InsMasterActivity.this, "Snapshot Doesn't Exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        title = findViewById(R.id.title);
        title.setText("Master Report");

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
    }
}