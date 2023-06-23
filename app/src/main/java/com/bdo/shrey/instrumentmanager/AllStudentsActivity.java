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
import com.bdo.shrey.instrumentmanager.Adapters.StudentAdapter;
import com.bdo.shrey.instrumentmanager.Models.Instrument;
import com.bdo.shrey.instrumentmanager.Models.Student;
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

public class AllStudentsActivity extends AppCompatActivity implements View.OnClickListener{

    TextView title;
    ImageView back, scanQR;
    SearchView action_search, search_btn;
    Button f_loc, f_name, f_assigned, f_za;
    DatabaseReference myRef;
    Query query;
    RecyclerView i_recyclerView;
    FloatingActionButton add;
    FirebaseDatabase database;
    StudentAdapter student_adapter;
    ArrayList<Student> students_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_students);

        title = findViewById(R.id.title);
        title.setText("All Students");

        back = findViewById(R.id.back);
        action_search = findViewById(R.id.search);
        scanQR = findViewById(R.id.scanQR);

        action_search.setVisibility(View.INVISIBLE);
        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ScanActivity.class));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        search_btn = findViewById(R.id.search_btn);
        search_btn.clearFocus();

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

        f_name = findViewById(R.id.filter_name);
        f_loc = findViewById(R.id.filter_loc);
        f_assigned = findViewById(R.id.filter_assigned);
        f_za = findViewById(R.id.filter_za);

        f_name.setOnClickListener(this::onClick);
        f_loc.setOnClickListener(this::onClick);
        f_assigned.setOnClickListener(this::onClick);
        f_za.setOnClickListener(this::onClick);

        add = findViewById(R.id.fab_s_add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateStudentActivity.class);
                startActivity(intent);
            }
        });

        i_recyclerView = findViewById(R.id.recyclerview_all_s);
        database = FirebaseDatabase.getInstance();
        Bundle extras = getIntent().getExtras();
        String loc = extras.getString("location");
        if (loc.equals("")){
            loc = "Students";
        }else {
            loc = "Students1/"+extras.getString("location");
        }

        myRef = database.getReference(loc);

        i_recyclerView.setHasFixedSize(true);
        i_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        students_list = new ArrayList<>();
        student_adapter = new StudentAdapter(this, students_list);
        i_recyclerView.setAdapter(student_adapter);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Student student = dataSnapshot.getValue(Student.class);
                    students_list.add(student);
                }
                student_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AllStudentsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void searchList(String text) {
        ArrayList<Student> search_list = new ArrayList<>();
        for (Student student : students_list) {
            if (student.getId().toLowerCase().contains(text.toLowerCase())) {
                search_list.add(student);
            } else if (student.getLocation().toLowerCase().contains(text.toLowerCase())) {
                search_list.add(student);
            } else if (student.getName().toLowerCase().contains(text.toLowerCase())) {
                search_list.add(student);
            }
        }
        student_adapter.searchData(search_list);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch(id){
            case R.id.filter_name:
                Collections.sort(students_list, new Comparator<Student>() {
                    @Override
                    public int compare(Student t1, Student t2) {
                        return t1.getName().compareTo(t2.getName());
                    }
                });
                student_adapter.notifyDataSetChanged();
                break;
            case R.id.filter_loc:
                Collections.sort(students_list, new Comparator<Student>() {
                    @Override
                    public int compare(Student t1, Student t2) {
                        return t1.getLocation().compareTo(t2.getLocation());
                    }
                });
                student_adapter.notifyDataSetChanged();
                break;
            case R.id.filter_assigned:
                Collections.sort(students_list, new Comparator<Student>() {
                    @Override
                    public int compare(Student t1, Student t2) {
                        return t1.getAssigned().compareTo(t2.getAssigned());
                    }
                });
                student_adapter.notifyDataSetChanged();
                break;
            case R.id.filter_za:
                Collections.reverse(students_list);
                student_adapter.notifyDataSetChanged();
                break;
        }
    }
}