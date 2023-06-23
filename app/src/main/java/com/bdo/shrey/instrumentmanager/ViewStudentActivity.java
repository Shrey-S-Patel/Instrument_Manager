package com.bdo.shrey.instrumentmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bdo.shrey.instrumentmanager.Models.Assign;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ViewStudentActivity extends AppCompatActivity {

    SearchView action_search;
    ImageView back, scanQR;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student);

        action_search = findViewById(R.id.search);
        scanQR = findViewById(R.id.scanQR);

        title = findViewById(R.id.title);
        title.setText("View Student");

        action_search.setVisibility(View.INVISIBLE);
        scanQR.setVisibility(View.INVISIBLE);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Bundle extras = getIntent().getExtras();
        String id = extras.getString("id");
        String status = extras.getString("status");

        if (id == null) {
            Toast.makeText(this, "Student Not Found", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), AllStudentsActivity.class);
            intent.putExtra("location", "");
            startActivity(intent);
            finish();
        } /*else {
            if (status.equals("Inactive") || status.equals("Transit")){
                assign_btn.setEnabled(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    assign_btn.getBackground().setTint(Color.GRAY);
                }
                receive_btn.setEnabled(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    receive_btn.getBackground().setTint(Color.GRAY);
                }
            }
            assign_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(id).exists()) {
                        assign_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        });
                        Toast.makeText(ViewInstrumentActivity.this, "Return before assigning!", Toast.LENGTH_SHORT).show();
                        Assign assign = snapshot.child(id).getValue(Assign.class);
                        s_name = assign.getS_name();
                        s_id = assign.getS_id();
                        assign_txt.setText("Assigned to " + s_name);
                        assign_btn.setEnabled(false);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            assign_btn.getBackground().setTint(Color.RED);
                        }
                    } else {
                        assign_txt.setText("");
                        receive_btn.setEnabled(false);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            receive_btn.getBackground().setTint(Color.RED);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            getData(id, cat);
        }*/
    }
}