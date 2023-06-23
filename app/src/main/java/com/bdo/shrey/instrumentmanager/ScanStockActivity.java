package com.bdo.shrey.instrumentmanager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bdo.shrey.instrumentmanager.Models.Instrument;
import com.bdo.shrey.instrumentmanager.Models.Stock;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Objects;

public class ScanStockActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    Instrument instrument;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_stock);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Instruments1");
        scanCode();
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to turn flash ON.");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        QRLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> QRLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null){

            String str = result.getContents();
            Intent i = getIntent();
            String i_id = i.getStringExtra("i_id");
            String[] stringArray = str.split("[: \\r?\\n|\\r]");
            String id = stringArray[2].trim();
            String cat = stringArray[5].trim();
//           builder.setMessage(id);

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if ((Objects.equals(i_id, id)) && (snapshot.child(cat).child(id).exists())){
                        instrument = snapshot.child(cat).child(id).getValue(Instrument.class);
                    }else {
                        finish();
                        Toast.makeText(ScanStockActivity.this, "Instrument not found!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ScanStockActivity.this, "Activity Cancelled!", Toast.LENGTH_SHORT).show();
                }
            });

            Dialog diag = new Dialog(this);
            diag.setCancelable(false);
            diag.setContentView(R.layout.custom_scan_dialog);
            TextView code = diag.findViewById(R.id.i_code);
            Button btn_okay, btn_view;
            btn_okay = diag.findViewById(R.id.btn_ok);
            btn_view = diag.findViewById(R.id.btn_view);
            btn_okay.setVisibility(View.INVISIBLE);
            btn_view.setText("OK");
            code.setText(id + ", " + cat);

            btn_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    diag.dismiss();
                    finish();
                }
            });

           /* btn_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ViewInstrumentActivity.class);
                    intent.putExtra("id", stock.getId());
                    intent.putExtra("category", stock.getCategory());
                    intent.putExtra("image", stock.getImg());
                    intent.putExtra("location", stock.getLocation());
                    intent.putExtra("status", stock.getStatus());
//                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "View Instrument", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });*/
            diag.show();
        }
    });
}