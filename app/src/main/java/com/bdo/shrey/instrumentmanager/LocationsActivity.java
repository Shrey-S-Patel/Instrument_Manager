package com.bdo.shrey.instrumentmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bdo.shrey.instrumentmanager.Adapters.CategoryAdapter;
import com.bdo.shrey.instrumentmanager.Adapters.SLocationAdapter;
import com.bdo.shrey.instrumentmanager.Models.Category;
import com.bdo.shrey.instrumentmanager.Models.StudentLocation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LocationsActivity extends AppCompatActivity {

    TextView title;
    ImageView back, scanQR;
    SearchView action_search;
    RecyclerView recyclerView;
    FirebaseDatabase database;
    SLocationAdapter loc_adapter;
    ArrayList<StudentLocation> loc_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);title = findViewById(R.id.title);
        title.setText("Locations");

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

        //Code for all Categories to be added into the recyclerview

        recyclerView = findViewById(R.id.recyclerview_all_loc);
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("s_location");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loc_list = new ArrayList<>();
        loc_adapter = new SLocationAdapter(this, loc_list);
        recyclerView.setAdapter(loc_adapter);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    StudentLocation sl = dataSnapshot.getValue(StudentLocation.class);
                    loc_list.add(sl);
                }
                loc_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}