package com.bdo.shrey.instrumentmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.bdo.shrey.instrumentmanager.Models.Assign;
import com.bdo.shrey.instrumentmanager.Models.Category;
import com.bdo.shrey.instrumentmanager.Models.History;
import com.bdo.shrey.instrumentmanager.Models.Instrument;
import com.bdo.shrey.instrumentmanager.Models.SDeletes;
import com.bdo.shrey.instrumentmanager.Models.Student;
import com.bdo.shrey.instrumentmanager.Models.StudentLocation;
import com.bdo.shrey.instrumentmanager.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ViewStudentActivity extends AppCompatActivity {
    TextView title;
    SearchView action_search;
    ImageView back, scanQR;

    TextView name, code, location, status, current, assigned, history;
    Button assign_btn, receive_btn, edit_btn, delete_btn;
    ImageButton generate_btn;

    private DatabaseReference myRef;
    private FirebaseDatabase database;

    String s_name, s_id, s_loc, s_stat, s_curr, s_assigned, s_hist;
    private String password;
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private final DatabaseReference user_ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
    private String u_name;
    private int loc_count;
    private ArrayList<String> loc_spinner_list, stat_spinner_list, current_spinner_list;
    private ArrayAdapter<String> loc_spinner_adapter, stat_spinner_adapter, current_spinner_adapter;
    private DatabaseReference cat_ref = FirebaseDatabase.getInstance().getReference("Categories");
    private DatabaseReference stud_ref = FirebaseDatabase.getInstance().getReference("Students");
    private DatabaseReference basic_ref = FirebaseDatabase.getInstance().getReference();


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


                        if (!(student.getAssigned().equals(""))) {
                            s_assigned = student.getAssigned();
                            Toast.makeText(getApplicationContext(), "Return " + s_assigned +  " before assigning!", Toast.LENGTH_SHORT).show();
                            assigned.setText("Assigned: " + s_assigned);
                            assign_btn.setEnabled(false);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                assign_btn.getBackground().setTint(Color.RED);
                            }
                        }else {
                            assigned.setText("Ready to assign!");
                            receive_btn.setEnabled(false);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                receive_btn.getBackground().setTint(Color.RED);
                            }
                        }

                        delete_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FirebaseDatabase.getInstance().getReference().child("Admin").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        password = snapshot.child("delete_pswd").getValue(String.class);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getApplicationContext(), "Delete cancelled!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                DialogPlus dialogPlus = DialogPlus.newDialog(ViewStudentActivity.this)
                                        .setGravity(Gravity.CENTER)
                                        .setMargin(50, 0, 50, 0)
                                        .setContentHolder(new ViewHolder(R.layout.dialog_password))
                                        .setContentBackgroundResource(com.google.android.material.R.color.cardview_dark_background)
                                        .setExpanded(false).create();

                                View holderView = dialogPlus.getHolderView();
                                TextView edit_password = holderView.findViewById(R.id.edit_psd);
                                TextView title = holderView.findViewById(R.id.title);
                                SearchView action_search = holderView.findViewById(R.id.search);
                                ImageView scanQR = holderView.findViewById(R.id.scanQR);
                                ImageView back = holderView.findViewById(R.id.back);
                                Button button = holderView.findViewById(R.id.button);

                                title.setText("Delete Student");
                                action_search.setVisibility(View.INVISIBLE);
                                scanQR.setVisibility(View.INVISIBLE);

                                back.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogPlus.dismiss();
                                    }
                                });


                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!(edit_password.getText().toString().equals(password))) {
                                            edit_password.setError("Enter the correct password");
                                            Toast.makeText(getApplicationContext(), "Invalid Password!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");
                                            myRef.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    user_ref.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            User user = snapshot.getValue(User.class);

                                                            if (snapshot.exists()) {
                                                                if (user != null) {
                                                                    u_name = user.getName();
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(), "This user object is null!", Toast.LENGTH_LONG).show();
                                                                }
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            Toast.makeText(getApplicationContext(), "Activity was cancelled!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(getApplicationContext(), "Activity was cancelled!", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            SDeletes deleted_s = new SDeletes(student.getId(), student.getName(), student.getLocation(), student.getAssigned(), u_name);
                                            database.getReference().child("s_location").child(student.getLocation()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                     StudentLocation loc = snapshot.getValue(StudentLocation.class);

                                                    if (snapshot.exists()) {
                                                        assert loc != null;
                                                        loc_count = loc.getCount();
                                                        loc_count = loc_count - 1;
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(ViewStudentActivity.this, "Location Count was cancelled!", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            database.getReference().child("Students").child(student.getId()).removeValue();
                                            database.getReference().child("Students1").child(student.getLocation()).child(student.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference().child("Deleted_S").child(student.getId()).setValue(deleted_s).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Map<String, Object> count = new HashMap<>();
                                                            count.put("/count", loc_count);
                                                            database.getReference().child("s_location").child(student.getLocation()).updateChildren(count);
                                                            Toast.makeText(getApplicationContext(), "Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), AllStudentsActivity.class);
                                                            intent.putExtra("location", "");
                                                            finish();
                                                            startActivity(intent);
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext(), "Delete Failed!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Delete Failed!", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    }
                                });
                                dialogPlus.show();
                            }
                        });

                        assign_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), AllInstrumentsActivity.class);
                                intent.putExtra("category", student.getCurrent());
                                finish();
                                startActivity(intent);
                            }
                        });

                        receive_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
                                finish();
                                startActivity(intent);
                            }
                        });

                        edit_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DialogPlus dialogPlus = DialogPlus.newDialog(ViewStudentActivity.this)
                                        .setGravity(Gravity.CENTER)
                                        .setMargin(50, 50, 50, 50)
                                        .setContentHolder(new ViewHolder(R.layout.student_edit_dialog))
                                        .setContentBackgroundResource(com.google.android.material.R.color.cardview_dark_background)
                                        .setExpanded(false).create();

                                View holderView = dialogPlus.getHolderView();
                                ImageView image = holderView.findViewById(R.id.iv_bc);
                                TextView title = holderView.findViewById(R.id.title);
                                SearchView action_search = holderView.findViewById(R.id.search);
                                ImageView scanQR = holderView.findViewById(R.id.scanQR);
                                ImageView back = holderView.findViewById(R.id.back);

                                EditText name = holderView.findViewById(R.id.s_name);
                                TextView id = holderView.findViewById(R.id.s_id);
                                Spinner location = holderView.findViewById(R.id.s_location);
                                Spinner status = holderView.findViewById(R.id.s_status);
                                Spinner current = holderView.findViewById(R.id.s_current);
                                Button save = holderView.findViewById(R.id.btn_upload);

                                title.setText("Edit Student");
                                action_search.setVisibility(View.INVISIBLE);
                                scanQR.setVisibility(View.INVISIBLE);

                                name.setText(student.getName());
                                id.setText(student.getId());

                                loc_spinner_list = new ArrayList<>();
                                loc_spinner_list.add("Korogocho");
                                loc_spinner_list.add("Mukuru Reuben");
                                loc_spinner_list.add("Main Office");
                                loc_spinner_list.add("Mombasa");

                                loc_spinner_adapter = new ArrayAdapter<String>(ViewStudentActivity.this, android.R.layout.simple_spinner_dropdown_item, loc_spinner_list);
                                location.setAdapter(loc_spinner_adapter);
                                location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        s_loc = loc_spinner_list.get(i);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                                stat_spinner_list = new ArrayList<>();
                                stat_spinner_list.add("Active");
                                stat_spinner_list.add("Inactive");

                                stat_spinner_adapter = new ArrayAdapter<String>(ViewStudentActivity.this, android.R.layout.simple_spinner_dropdown_item, stat_spinner_list);
                                status.setAdapter(stat_spinner_adapter);
                                status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        s_stat = stat_spinner_list.get(i);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                                current_spinner_list = new ArrayList<>();
                                current_spinner_adapter = new ArrayAdapter<String>(ViewStudentActivity.this, android.R.layout.simple_spinner_dropdown_item, current_spinner_list);
                                current.setAdapter(current_spinner_adapter);
                                cat_ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
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
                                current.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        s_curr = current_spinner_list.get(i);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                                back.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogPlus.dismiss();
                                    }
                                });

                                save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Map<String, Object> map = new HashMap<>();
                                        Map<String, Object> map_hist = new HashMap<>();
                                        if (!Objects.equals(student.getName(), name.getText()) || !Objects.equals(student.getLocation(), s_loc) || !Objects.equals(student.getStatus(), s_stat) || !Objects.equals(student.getCurrent(), s_curr)){
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                                            String currentDateandTime = sdf.format(new Date());
                                            map.put(student.getId(), new Student(student.getId(), name.getText().toString(), s_loc, student.getAssigned(), s_stat, s_curr, currentDateandTime));

                                            History history = new History(s_curr, currentDateandTime, "");
                                            map_hist.put("/to", currentDateandTime);
                                            FirebaseDatabase.getInstance().getReference("S_History").child(student.getId()).child(student.getCurrent()).updateChildren(map_hist);
                                            FirebaseDatabase.getInstance().getReference("S_History").child(student.getId()).child(s_curr).setValue(history);
                                            stud_ref.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    basic_ref.child("Students1").child(student.getLocation()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(getApplicationContext(), "Changes Saved!", Toast.LENGTH_SHORT).show();
                                                            dialogPlus.dismiss();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Changes Not Saved!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }else {
                                            Toast.makeText(getApplicationContext(), "No changes noticed!", Toast.LENGTH_SHORT).show();
                                            dialogPlus.dismiss();
                                            Toast.makeText(getApplicationContext(), "Exiting!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                dialogPlus.show();
                            }
                        });

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