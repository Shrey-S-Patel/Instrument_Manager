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

public class StudentActivity extends AppCompatActivity {

    TextView title;
    ImageView back, scanQR;
    SearchView action_search;

    CardView all, add, locations, assignments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        title = findViewById(R.id.title);
        title.setText("Students");

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

        all = findViewById(R.id.students_i_text);
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AllStudentsActivity.class);
                intent.putExtra("location", "");
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "All Students", Toast.LENGTH_SHORT).show();
            }
        });

        locations = findViewById(R.id.locations_s_text);
        locations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LocationsActivity.class));
                Toast.makeText(getApplicationContext(), "Students by Location", Toast.LENGTH_SHORT).show();
            }
        });

        assignments = findViewById(R.id.assignments_i_text);
        assignments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
                Toast.makeText(getApplicationContext(), "All Assignments", Toast.LENGTH_SHORT).show();
            }
        });

        add = findViewById(R.id.add_i_txt);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateStudentActivity.class));
                Toast.makeText(getApplicationContext(), "Add Students", Toast.LENGTH_SHORT).show();
            }
        });

    }
}