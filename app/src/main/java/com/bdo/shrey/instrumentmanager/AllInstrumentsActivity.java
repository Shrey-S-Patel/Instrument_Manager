package com.bdo.shrey.instrumentmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bdo.shrey.instrumentmanager.Adapters.InstrumentAdapter;
import com.bdo.shrey.instrumentmanager.Models.Instrument;
import com.google.android.material.button.MaterialButton;
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
import java.util.Objects;

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
    private int k_count = 0;
    private int s_list_size = 0;
    private HorizontalScrollView locations;
    private ArrayList<String> loc_spinner_list;
    LinearLayout linearLayout ;
    LinearLayout.LayoutParams linearParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_instruments);
        linearLayout = new LinearLayout(this);
        linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(linearParams);
        linearLayout.setPadding(20,10,20,10);

        loc_spinner_list  = new ArrayList<>();

        locations = findViewById(R.id.locations);
        ShowData();
        locations.addView(linearLayout);

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


//        Toast.makeText(this, "Main instruments = " + String.valueOf(k_count), Toast.LENGTH_SHORT).show();

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

    private void ShowData() {
        FirebaseDatabase.getInstance().getReference().child("Locations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    loc_spinner_list.add(String.valueOf(item.getValue()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
            }
        });

        for (int i = 0; i<loc_spinner_list.size(); i++) {
            String location = loc_spinner_list.get(i).toString();
            Button btn = new Button(new ContextThemeWrapper(this, com.google.android.material.R.style.ThemeOverlay_Material3_Button_TonalButton));
            btn.setId(ViewCompat.generateViewId());
            btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            btn.setText(location);
            btn.setVisibility(View.VISIBLE);
            btn.setClickable(true);
            btn.setPadding(50,50,10,0);
            btn.setBackgroundResource(R.drawable.rounded_rectangle);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchList(location);
                }
            });
            linearLayout.addView(btn);
        }
    }

    private void showData() {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                instrument_list.clear();
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
//        s_list_size = instrument_adapter.getItemCount();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
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
                ShowData();
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