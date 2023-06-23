package com.bdo.shrey.instrumentmanager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bdo.shrey.instrumentmanager.Models.Instrument;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Arrays;

public class ScanActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myRef;
    Instrument instrument;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Instruments");
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
           String[] stringArray = str.split("[: \\r?\\n|\\r]");
           String id = stringArray[2].trim();
//           builder.setMessage(id);

           myRef.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if (snapshot.child(id).exists()){
                       instrument = snapshot.child(id).getValue(Instrument.class);
                   }else {
                       Toast.makeText(ScanActivity.this, "Instrument not found!", Toast.LENGTH_SHORT).show();
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {
                   Toast.makeText(ScanActivity.this, "Activity Cancelled!", Toast.LENGTH_SHORT).show();
               }
           });

           Dialog diag = new Dialog(this);
           diag.setCancelable(false);
           diag.setContentView(R.layout.custom_scan_dialog);
           TextView code = diag.findViewById(R.id.i_code);
           Button btn_okay, btn_view;
           btn_okay = diag.findViewById(R.id.btn_ok);
           btn_view = diag.findViewById(R.id.btn_view);

           code.setText(id);

           btn_okay.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   diag.dismiss();
                   finish();
               }
           });

           btn_view.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent intent = new Intent(getApplicationContext(), ViewInstrumentActivity.class);
                   intent.putExtra("id", instrument.getId());
                   intent.putExtra("category", instrument.getCategory());
                   intent.putExtra("image", instrument.getImg());
                   intent.putExtra("location", instrument.getLocation());
                   intent.putExtra("status", instrument.getStatus());
                   startActivity(intent);
                   Toast.makeText(getApplicationContext(), "View Instrument", Toast.LENGTH_SHORT).show();
                   finish();
               }
           });
           diag.show();
       }
    });
}