package com.bdo.shrey.instrumentmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.bdo.shrey.instrumentmanager.Models.Assign;
import com.bdo.shrey.instrumentmanager.Models.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewStudentActivity extends AppCompatActivity {
    TextView title;
    SearchView action_search;
    ImageView back, scanQR;

    TextView name, code, location, status, current, assigned, history;
    Button assign_btn, receive_btn, edit_btn, delete_btn;
    ImageButton generate_btn;

    DatabaseReference myRef;
    FirebaseDatabase database;

    String s_name, s_id, s_loc, s_stat, s_curr, s_assigned, s_hist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student);

        database = FirebaseDatabase.getInstance();

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

        name = findViewById(R.id.s_name);
        code = findViewById(R.id.s_id);
        location = findViewById(R.id.s_loc);
        status = findViewById(R.id.s_status);
        current = findViewById(R.id.s_current);
        assigned = findViewById(R.id.i_assign);
        history = findViewById(R.id.s_hist);

        assign_btn = findViewById(R.id.assignbtn);
        receive_btn = findViewById(R.id.receivebtn);
        edit_btn = findViewById(R.id.editbtn);
        delete_btn = findViewById(R.id.deletebtn);
        generate_btn = findViewById(R.id.generatebtn);


        Bundle extras = getIntent().getExtras();
        String id = extras.getString("id");
        String stat = extras.getString("status");

        myRef = database.getReference("Students");

        if (id == null) {
            Toast.makeText(this, "Student Not Found", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), AllStudentsActivity.class);
            intent.putExtra("location", "");
            startActivity(intent);
            finish();
        } else {
            if (stat.equals("Inactive")) {
                assign_btn.setEnabled(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    assign_btn.getBackground().setTint(Color.GRAY);
                }
                receive_btn.setEnabled(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    receive_btn.getBackground().setTint(Color.GRAY);
                }
            }
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(id).exists()) {
                        Student student = snapshot.child(id).getValue(Student.class);
                        name.setText(student.getName());
                        code.setText(student.getId());
                        location.setText(student.getLocation());
                        status.setText(student.getStatus());
                        current.setText("Currently Learning: " + student.getCurrent());
                        history.setText("Nothing yet");


                        if (student.getAssigned() != null) {
                            s_assigned = student.getAssigned();
                            Toast.makeText(getApplicationContext(), "Return " + s_assigned +  " before assigning!", Toast.LENGTH_SHORT).show();
                            assigned.setText("Assigned: " + s_assigned);
                            assign_btn.setEnabled(false);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                assign_btn.getBackground().setTint(Color.RED);
                            }
                        }else {
                            assigned.setText("");
                            receive_btn.setEnabled(false);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                receive_btn.getBackground().setTint(Color.RED);
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Student does not exist!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), AllStudentsActivity.class);
                        intent.putExtra("location", "");
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}