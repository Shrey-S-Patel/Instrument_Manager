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

import com.bdo.shrey.instrumentmanager.Adapters.InstrumentAdapter;
import com.bdo.shrey.instrumentmanager.Adapters.StocktakeAdapter;
import com.bdo.shrey.instrumentmanager.Models.Instrument;
import com.bdo.shrey.instrumentmanager.Models.Stock;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class StocktakeActivity extends AppCompatActivity {

    TextView title;
    ImageView back, scanQR;
    SearchView action_search;
    RecyclerView i_recyclerView;
    FirebaseDatabase database;
    StocktakeAdapter stocktake_adapter;
    ArrayList<Instrument> instrument_list;
    ArrayList<Stock> ins_copy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocktake);

        title = findViewById(R.id.title);
        title.setText("Stocktake");

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

        i_recyclerView = findViewById(R.id.recyclerview_st_i);
        database = FirebaseDatabase.getInstance();
        Bundle extras = getIntent().getExtras();
        String cat = extras.getString("category");
        if (cat.equals("")){
            cat = "Instruments";
        }else {
            cat = "Instruments1/"+extras.getString("category");
        }

        DatabaseReference myRef = database.getReference(cat);
        Query query = myRef.orderByChild("location");

        i_recyclerView.setHasFixedSize(true);
        i_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        instrument_list = new ArrayList<>();
        ins_copy = new ArrayList<>();
        stocktake_adapter = new StocktakeAdapter(this, instrument_list);
        i_recyclerView.setAdapter(stocktake_adapter);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Instrument instrument = dataSnapshot.getValue(Instrument.class);
                    instrument_list.add(instrument);
                }
                stocktake_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StocktakeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}