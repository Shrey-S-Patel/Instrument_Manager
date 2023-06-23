package com.bdo.shrey.instrumentmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bdo.shrey.instrumentmanager.Adapters.InstrumentAdapter;
import com.bdo.shrey.instrumentmanager.Models.Instrument;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AllInstrumentsActivity extends AppCompatActivity implements View.OnClickListener {
    TextView title;
    ImageView back, scanQR;
    SearchView action_search, search_btn;
    Button f_loc, f_cat, f_stat, f_za;
    DatabaseReference myRef;
    Query query;
    String filter;
    RecyclerView i_recyclerView;
    FloatingActionButton add;
    FirebaseDatabase database;
    InstrumentAdapter instrument_adapter;
    ArrayList<Instrument> instrument_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_instruments);

        title = findViewById(R.id.title);
        title.setText("All Instruments");

        back = findViewById(R.id.back);
        action_search = findViewById(R.id.search);
        search_btn = findViewById(R.id.search_btn);
        search_btn.clearFocus();

        scanQR = findViewById(R.id.scanQR);

        action_search.setVisibility(View.INVISIBLE);
        scanQR.setVisibility(View.INVISIBLE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        add = findViewById(R.id.fab_i_add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateInstrumentActivity.class);
                startActivity(intent);
            }
        });

        f_cat = findViewById(R.id.filter_cat);
        f_loc = findViewById(R.id.filter_loc);
        f_stat = findViewById(R.id.filter_stat);
        f_za = findViewById(R.id.filter_za);

        f_cat.setOnClickListener(this::onClick);
        f_loc.setOnClickListener(this::onClick);
        f_stat.setOnClickListener(this::onClick);
        f_za.setOnClickListener(this::onClick);

        filter = "id";

        i_recyclerView = findViewById(R.id.recyclerview_all_i);
        database = FirebaseDatabase.getInstance();
        Bundle extras = getIntent().getExtras();
        String cat = extras.getString("category");
        if (cat.equals("")) {
            cat = "Instruments";
        } else {
            cat = "Instruments1/" + extras.getString("category");
        }

        myRef = database.getReference(cat);
        query = myRef.orderByChild(filter);

        i_recyclerView.setHasFixedSize(true);
        i_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        instrument_list = new ArrayList<>();
        instrument_adapter = new InstrumentAdapter(this, instrument_list);
        i_recyclerView.setAdapter(instrument_adapter);
        showData();

        search_btn.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
    }

    private void showData(){
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Instrument instrument = dataSnapshot.getValue(Instrument.class);
                    instrument_list.add(instrument);
                }
                instrument_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AllInstrumentsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void searchList(String text) {
        ArrayList<Instrument> search_list = new ArrayList<>();
        for (Instrument instrument : instrument_list) {
            if (instrument.getId().toLowerCase().contains(text.toLowerCase())) {
                search_list.add(instrument);
            } else if (instrument.getLocation().toLowerCase().contains(text.toLowerCase())) {
                search_list.add(instrument);
            }
        }
        instrument_adapter.searchData(search_list);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch(id){
            case R.id.filter_cat:
                filter = "category";
                Collections.sort(instrument_list, new Comparator<Instrument>() {
                    @Override
                    public int compare(Instrument t1, Instrument t2) {
                        return t1.getCategory().compareTo(t2.getCategory());
                    }
                });
                instrument_adapter.notifyDataSetChanged();
                break;
            case R.id.filter_loc:
                filter = "location";
                Collections.sort(instrument_list, new Comparator<Instrument>() {
                    @Override
                    public int compare(Instrument t1, Instrument t2) {
                        return t1.getLocation().compareTo(t2.getLocation());
                    }
                });
                instrument_adapter.notifyDataSetChanged();
                break;
            case R.id.filter_stat:
                filter = "status";
                Collections.sort(instrument_list, new Comparator<Instrument>() {
                    @Override
                    public int compare(Instrument t1, Instrument t2) {
                        return t1.getStatus().compareTo(t2.getStatus());
                    }
                });
                instrument_adapter.notifyDataSetChanged();
                break;
            case R.id.filter_za:
                Collections.reverse(instrument_list);
                instrument_adapter.notifyDataSetChanged();
                break;
            default:
                filter = "id";
                instrument_adapter.notifyDataSetChanged();
        }
    }
}