package com.bdo.shrey.instrumentmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;

public class InstrumentsActivity extends AppCompatActivity {
    TextView title;
    ImageView back, scanQR;
    SearchView action_search;

    CardView instruments, categories, add, scans, stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruments);

        title = findViewById(R.id.title);
        title.setText("Instruments");

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

        instruments = findViewById(R.id.instruments_i_txt);
        instruments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AllInstrumentsActivity.class);
                intent.putExtra("category", "");
                startActivity(intent);
                Toast.makeText(InstrumentsActivity.this, "All Instruments", Toast.LENGTH_SHORT).show();
            }
        });

        categories = findViewById(R.id.categories_i_txt);
        categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
                Toast.makeText(InstrumentsActivity.this, "All Categories", Toast.LENGTH_SHORT).show();
            }
        });

        add = findViewById(R.id.add_i_txt);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateInstrumentActivity.class));
                Toast.makeText(InstrumentsActivity.this, "Add Instruments", Toast.LENGTH_SHORT).show();
            }
        });

        scans = findViewById(R.id.scan_i_txt);
        scans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ScanActivity.class));
                Toast.makeText(InstrumentsActivity.this, "Scan QR", Toast.LENGTH_SHORT).show();
            }
        });
/*
        stock = findViewById(R.id.stock_i_txt);
        stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), StocktakeActivity.class));
                Toast.makeText(InstrumentsActivity.this, "Stocktake", Toast.LENGTH_SHORT).show();
            }
        });*/
    }
}