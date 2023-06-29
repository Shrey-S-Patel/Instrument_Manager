package com.bdo.shrey.instrumentmanager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.bdo.shrey.instrumentmanager.Models.Category;
import com.bdo.shrey.instrumentmanager.Models.History;
import com.bdo.shrey.instrumentmanager.Models.Student;
import com.bdo.shrey.instrumentmanager.Models.StudentLocation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CreateStudentActivity extends AppCompatActivity {

    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference("Students");
    private final DatabaseReference loc_ref = FirebaseDatabase.getInstance().getReference("s_location");
    private final DatabaseReference spinner_ref = FirebaseDatabase.getInstance().getReference("Locations");
    private DatabaseReference cat_ref = FirebaseDatabase.getInstance().getReference("Categories");
    TextView title;
    ImageView back, scanQR;
    SearchView action_search;
    EditText name, student_id;
    Button btn_create;
    private Spinner loc_spinner, stat_spinner, current_spinner;
    private ArrayList<StudentLocation> locnum_spin_list;
    private ArrayList<String> loc_spinner_list, stat_spinner_list, current_spinner_list;
    private ArrayAdapter<String> loc_spinner_adapter, stat_spinner_adapter, current_spinner_adapter;
    private String s_name, s_code, s_location, s_status, s_curr;
    private String sl_location;
    private int count, code_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_student);

        title = findViewById(R.id.title);
        title.setText("Create Student");

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

        name = findViewById(R.id.s_name);
        student_id = findViewById(R.id.edit_id);

        loc_spinner = findViewById(R.id.s_location);

        locnum_spin_list = new ArrayList<>();
        loc_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    StudentLocation loc = dataSnapshot.getValue(StudentLocation.class);
                    locnum_spin_list.add(loc);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        loc_spinner_list = new ArrayList<>();
        loc_spinner_adapter = new ArrayAdapter<String>(CreateStudentActivity.this, android.R.layout.simple_spinner_dropdown_item, loc_spinner_list);
        loc_spinner.setAdapter(loc_spinner_adapter);
        showLocation();

        loc_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s_location = loc_spinner_list.get(i);
                StudentLocation loc = locnum_spin_list.get(i);
                code_count = loc.getCode_count();
                sl_location = loc.getLocation();
                count = loc.getCount();
                generateCode(s_location, code_count);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(), "Select A Location!", Toast.LENGTH_SHORT).show();
            }
        });

        stat_spinner = findViewById(R.id.s_status);

        stat_spinner_list = new ArrayList<>();
        stat_spinner_list.add("Active");
        stat_spinner_list.add("Inactive");
        stat_spinner_adapter = new ArrayAdapter<String>(CreateStudentActivity.this, android.R.layout.simple_spinner_dropdown_item, stat_spinner_list);
        stat_spinner.setAdapter(stat_spinner_adapter);

        stat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s_status = stat_spinner_list.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(CreateStudentActivity.this, "Status is Required!", Toast.LENGTH_SHORT).show();
            }
        });

        current_spinner = findViewById(R.id.s_curr);
        current_spinner_list = new ArrayList<>();
        current_spinner_adapter = new ArrayAdapter<String>(CreateStudentActivity.this, android.R.layout.simple_spinner_dropdown_item, current_spinner_list);
        current_spinner.setAdapter(current_spinner_adapter);
        cat_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category cat = dataSnapshot.getValue(Category.class);
                    current_spinner_list.add(cat.getCat_name());
                }
                current_spinner_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        current_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s_curr = current_spinner_list.get(i);
//                ((TextView) view).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        loc_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                s_location = loc_spinner_list.get(i);
                StudentLocation loc = locnum_spin_list.get(i);
                code_count = loc.getCode_count();
                sl_location = loc.getLocation();
                count = loc.getCount();
                generateCode(s_location, code_count);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(), "Select A Location!", Toast.LENGTH_SHORT).show();
            }
        });

        btn_create = findViewById(R.id.btn_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s_name = name.getText().toString().trim();
                s_code = student_id.getText().toString().trim();

                if (s_name.isEmpty() || s_curr.isEmpty()) {
                    name.setError("Enter name!");
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());
                    Student student = new Student(s_code, s_name, s_location, "",s_status, s_curr, currentDateandTime);
                    History history = new History(s_curr, currentDateandTime, "");
                    root.child(s_code).setValue(student).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            FirebaseDatabase.getInstance().getReference("Students1").child(s_location).child(s_code).setValue(student);
                            FirebaseDatabase.getInstance().getReference("S_History").child(s_code).child(s_curr).setValue(history);
                            StudentLocation s_loc = new StudentLocation(sl_location, count, code_count);
                            loc_ref.child(sl_location).setValue(s_loc).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(CreateStudentActivity.this, "Student created!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CreateStudentActivity.this, "Count not updated!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateStudentActivity.this, "Failed to create!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        });

    }

    private void showLocation() {
        spinner_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    loc_spinner_list.add(String.valueOf(item.getValue()));
                }
                loc_spinner_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateCode(String loc_select, int locnum_select) {
        locnum_select = locnum_select + 1;
        count = count + 1;
        code_count += 1;
        String s_code = loc_select + "_" + locnum_select;
        student_id.setText(s_code);
    }

}