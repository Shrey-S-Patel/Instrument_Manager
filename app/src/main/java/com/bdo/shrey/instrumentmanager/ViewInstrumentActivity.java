package com.bdo.shrey.instrumentmanager;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.bdo.shrey.instrumentmanager.Models.Assign;
import com.bdo.shrey.instrumentmanager.Models.Category;
import com.bdo.shrey.instrumentmanager.Models.Deletes;
import com.bdo.shrey.instrumentmanager.Models.Instrument;
import com.bdo.shrey.instrumentmanager.Models.Student;
import com.bdo.shrey.instrumentmanager.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ViewInstrumentActivity extends AppCompatActivity {
    private static final int Gallery_Code = 1;
    private final DatabaseReference spinner_ref = FirebaseDatabase.getInstance().getReference("Students");
    private final DatabaseReference assign_ref = FirebaseDatabase.getInstance().getReference("Assignments");
    SearchView action_search;
    ImageView back, qr_image, scanQR;
    TextView cat, code, location, status, assign_txt, notes, title;
    Button assign_btn, receive_btn, edit_btn, delete_btn;
    ImageButton generate_btn;
    FirebaseDatabase database;
    DatabaseReference reference, reference1, countRef;
    int numberOfChildren, counter, limit;
    Uri imageUrl = null;
    ArrayList<String> student_list;
    ArrayAdapter<String> stud_spinner_adapter;
    DatabaseReference reference_u;
    DatabaseReference database_ref, delete_ref, cat_ref;
    int cat_count;
    String u_name;
    String s_name = "";
    String i_location, i_status;
    String s_id;
    Dialog dialog;
    private String password;
    //    private Spinner student_spinner;
    private String stud_select, stud_id;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database_u;
    private ArrayList<String> loc_spinner_list, stat_spinner_list;
    private ArrayList<Student> students_details_list;
    private ArrayAdapter<String> loc_spinner_adapter, stat_spinner_adapter;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_instrument);


        database_u = FirebaseDatabase.getInstance();
        reference_u = database_u.getReference().child("Users").child(FirebaseAuth.getInstance().getUid());

        database_u = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database_u.getReference("Users");

        mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();

        database_ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                database_ref.addValueEventListener(new ValueEventListener() {
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


        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Instruments");
        reference1 = database.getReference().child("Instruments1");

        qr_image = findViewById(R.id.img_qr);
        cat = findViewById(R.id.i_cat);
        code = findViewById(R.id.i_code);
        location = findViewById(R.id.i_location);
        status = findViewById(R.id.i_status);
        notes = findViewById(R.id.i_notes);
        assign_txt = findViewById(R.id.i_assign);
        assign_btn = findViewById(R.id.assignbtn);
        receive_btn = findViewById(R.id.receivebtn);
        edit_btn = findViewById(R.id.editbtn);
        delete_btn = findViewById(R.id.deletebtn);
        generate_btn = findViewById(R.id.generatebtn);

        action_search = findViewById(R.id.search);
        scanQR = findViewById(R.id.scanQR);

        title = findViewById(R.id.title);
        title.setText("Instrument View  ");

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
        String cat = extras.getString("category");
        String status = extras.getString("status");

        if (id == null || cat == null) {
            Toast.makeText(this, "Instrument Not Found", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), AllInstrumentsActivity.class);
            startActivity(intent);
            finish();
        } else {
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
        }
    }

    private void ShowData() {
        spinner_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    Student student = item.getValue(Student.class);
                    s_name = String.valueOf(student.getName()) + " - " + String.valueOf(student.getId());
                    student_list.add(s_name);
                    students_details_list.add(student);
                }
                stud_spinner_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData(String id, String category) {

        DatabaseReference mref = reference.child(id);

        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {

                mref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Instrument instrument = snapshot.getValue(Instrument.class);

                        if (snapshot.exists()) {
                            numberOfChildren = (int) snapshot.getChildrenCount();
                        } else {
                            numberOfChildren = 0;
                        }

                        assert instrument != null;
                        if (instrument.getId() == null || instrument.getCategory() == null) {
                            Toast.makeText(getApplicationContext(), "Instrument Not Found", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), AllInstrumentsActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            code.setText(instrument.getId());
                            cat.setText(instrument.getCategory());
                            location.setText(instrument.getLocation());
                            status.setText(String.format("Status: %s", instrument.getStatus()));

                            String imageUrl = null;
                            imageUrl = instrument.getImg();
                            Picasso.get().load(imageUrl).into(qr_image);

                            generate_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    BitmapDrawable bmp = (BitmapDrawable) qr_image.getDrawable();
                                    Bitmap share_bmp = bmp.getBitmap();
                                    
                                    shareImg(share_bmp, instrument.getId());

                                    /*Intent intent = new Intent(getApplicationContext(), generateQR.class);
                                    intent.putExtra("p_type", prod.p_type);
                                    intent.putExtra("p_name", prod.p_name);
                                    intent.putExtra("p_quantity", prod.p_quantity);
                                    startActivity(intent);*/
                                }
                            });

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
                                            Toast.makeText(ViewInstrumentActivity.this, "Delete cancelled!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                    DialogPlus dialogPlus = DialogPlus.newDialog(ViewInstrumentActivity.this).setGravity(Gravity.CENTER).setMargin(50, 0, 50, 0).setContentHolder(new ViewHolder(R.layout.dialog_password)).setContentBackgroundResource(com.google.android.material.R.color.cardview_dark_background).setExpanded(false).create();

                                    View holderView = dialogPlus.getHolderView();
                                    TextView edit_password = holderView.findViewById(R.id.edit_psd);
                                    TextView title = holderView.findViewById(R.id.title);
                                    SearchView action_search = holderView.findViewById(R.id.search);
                                    ImageView scanQR = holderView.findViewById(R.id.scanQR);
                                    ImageView back = holderView.findViewById(R.id.back);
                                    Button button = holderView.findViewById(R.id.button);

                                    title.setText("Delete Instrument");
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
                                                Toast.makeText(ViewInstrumentActivity.this, "Invalid Password!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                delete_ref = FirebaseDatabase.getInstance().getReference().child("Deleted");
                                                cat_ref = FirebaseDatabase.getInstance().getReference().child("Categories");
                                                Deletes delete = new Deletes(instrument.getId(), instrument.getCategory(), instrument.getLocation(), instrument.getStatus(), instrument.getLocation(), u_name);

                                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                                StorageReference storageRef = storage.getReference().child("QRs");
                                                String path_string = "QRs/" + id + ".jpg";
                                                StorageReference delete_img_ref = storageRef.child(id);

                                                cat_ref.child(instrument.getCategory()).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        Category cat = snapshot.getValue(Category.class);

                                                        if (snapshot.exists()) {
                                                            cat_count = cat.getCat_count();
                                                            cat_count = cat_count - 1;
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(ViewInstrumentActivity.this, "Cat Count was cancelled!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                // Delete the file
                                    /*delete_img_ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // File deleted successfully
                                            Toast.makeText(ViewInstrumentActivity.this, "Image Deleted!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Toast.makeText(ViewInstrumentActivity.this, "Image was not deleted1", Toast.LENGTH_SHORT).show();
                                        }
                                    });*/
                                                delete_ref.child(instrument.getId()).setValue(delete).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        reference.child(id).removeValue();
                                                        reference1.child(instrument.getCategory()).child(id).removeValue();
                                                        Map<String, Object> count = new HashMap<>();
                                                        count.put("/cat_count", cat_count);
                                                        cat_ref.child(instrument.getCategory()).updateChildren(count);
                                                        Toast.makeText(getApplicationContext(), id + " Deleted", Toast.LENGTH_SHORT).show();

                                                        finish();
                                                        Intent intent = new Intent(getApplicationContext(), AllInstrumentsActivity.class);
                                                        intent.putExtra("category", "");
                                                        startActivity(intent);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ViewInstrumentActivity.this, "Deleting Failed, Try Again!", Toast.LENGTH_SHORT).show();
                                                        dialogPlus.dismiss();
                                                        finish();
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
                                    DialogPlus dialogPlus = DialogPlus.newDialog(ViewInstrumentActivity.this).setGravity(Gravity.CENTER).setMargin(50, 0, 50, 0).setContentHolder(new ViewHolder(R.layout.dialog_assign)).setContentBackgroundResource(com.google.android.material.R.color.cardview_dark_background).setExpanded(false).create();

                                    View holderView = dialogPlus.getHolderView();
                                    TextView editText = holderView.findViewById(R.id.editTextNumber);
                                    TextView title = holderView.findViewById(R.id.title);
                                    SearchView action_search = holderView.findViewById(R.id.search);
                                    ImageView scanQR = holderView.findViewById(R.id.scanQR);
                                    ImageView back = holderView.findViewById(R.id.back);
                                    Button button = holderView.findViewById(R.id.button);

                                    TextView cat = holderView.findViewById(R.id.cat);
                                    TextView code = holderView.findViewById(R.id.code);
                                    TextView student = holderView.findViewById(R.id.student_view);

                                    student_list = new ArrayList<>();
                                    students_details_list = new ArrayList<>();
                                    stud_spinner_adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, student_list);
                                    ShowData();

                                    student.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog = new Dialog(view.getContext());
                                            dialog.setContentView(R.layout.dialog_searchable_spinner);
                                            dialog.getWindow().setLayout(600, 800);
                                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            dialog.show();


                                            EditText editText = dialog.findViewById(R.id.edit_text);
                                            ListView listView = dialog.findViewById(R.id.list_view);

                                            listView.setAdapter(stud_spinner_adapter);
                                            editText.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                                }

                                                @Override
                                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                    stud_spinner_adapter.getFilter().filter(charSequence);
                                                }

                                                @Override
                                                public void afterTextChanged(Editable editable) {

                                                }
                                            });

                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    // when item selected from list
                                                    // set selected item on textView
                                                    stud_select = String.valueOf(stud_spinner_adapter.getItem(position));
                                                    Student s1 = students_details_list.get(position);
                                                    if (s1.getAssigned().equals("")) {
                                                        stud_id = s1.getId();
                                                        String student_text = stud_spinner_adapter.getItem(position) + " - " + stud_id;
                                                        student.setText(student_text);
                                                        // Dismiss dialog
                                                        dialog.dismiss();
                                                    } else {
                                                        dialog.dismiss();
                                                        student.setError("Student has an instrument");
                                                        Toast.makeText(ViewInstrumentActivity.this, "Student has an instrument!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });

                                    cat.setText(instrument.getCategory());
                                    code.setText(instrument.getId());
                                    editText.setText("1");

                                    title.setText("Assign Instrument");
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
                                            if (student.getText().toString().isEmpty()) {
                                                dialogPlus.dismiss();
                                                Toast.makeText(ViewInstrumentActivity.this, "Student must be selected!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                String newQty = "0";
                                                Map<String, Object> s_map = new HashMap<>();
                                                s_map.put("/assigned", instrument.getId());
                                                Map<String, Object> i_map = new HashMap<>();
                                                i_map.put("/assigned", stud_id + ", " + stud_select);
                                                SimpleDateFormat sdf = new SimpleDateFormat("'On 'dd-MMMM-yy' at 'HH:mm a", Locale.getDefault());
                                                String currentDateandTime = sdf.format(new Date());
                                                Assign assign = new Assign(instrument.getId(), stud_id, stud_select, currentDateandTime, u_name);
                                                spinner_ref.child(stud_id).updateChildren(s_map);
                                                reference.child(instrument.getId()).updateChildren(i_map);
                                                assign_ref.child(instrument.getId()).setValue(assign).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        dialogPlus.dismiss();
                                                        Toast.makeText(ViewInstrumentActivity.this, "Assigned Successfully!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ViewInstrumentActivity.this, "Assignment Failed!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                Intent i = getIntent();
                                                dialogPlus.dismiss();
                                                finish();
                                                startActivity(i);
                                            }
                                        }
                                    });
                                    dialogPlus.show();

                                }
                            });

                            receive_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    DialogPlus dialogPlus = DialogPlus.newDialog(ViewInstrumentActivity.this).setGravity(Gravity.CENTER).setMargin(50, 0, 50, 0).setContentHolder(new ViewHolder(R.layout.dialog_return)).setContentBackgroundResource(com.google.android.material.R.color.cardview_dark_background).setExpanded(false).create();

                                    View holderView = dialogPlus.getHolderView();
                                    TextView editText = holderView.findViewById(R.id.editTextNumber);
                                    TextView title = holderView.findViewById(R.id.title);
                                    SearchView action_search = holderView.findViewById(R.id.search);
                                    ImageView scanQR = holderView.findViewById(R.id.scanQR);
                                    ImageView back = holderView.findViewById(R.id.back);
                                    Button button = holderView.findViewById(R.id.button);

                                    TextView cat = holderView.findViewById(R.id.cat);
                                    TextView code = holderView.findViewById(R.id.code);
                                    TextView student = holderView.findViewById(R.id.student_view);

                                    cat.setText(instrument.getCategory());
                                    code.setText(instrument.getId());
                                    editText.setText("1");
                                    student.setText(s_name);


                                    title.setText("Return Instrument");
                                    action_search.setVisibility(View.INVISIBLE);
                                    scanQR.setVisibility(View.INVISIBLE);

                                    counter = 0;
                                    editText.setText(String.valueOf(counter));

                                    back.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialogPlus.dismiss();
                                        }
                                    });

                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            assign_ref.child(instrument.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Map<String, Object> s_map = new HashMap<>();
                                                    s_map.put("/assigned", "");
                                                    spinner_ref.child(s_id).updateChildren(s_map);
                                                    Map<String, Object> i_map = new HashMap<>();
                                                    i_map.put("/assigned", "");
                                                    reference.child(instrument.getId()).updateChildren(i_map);
                                                    dialogPlus.dismiss();
                                                    Toast.makeText(ViewInstrumentActivity.this, "Returned Successfully!", Toast.LENGTH_SHORT).show();
                                                    Intent i = getIntent();
                                                    dialogPlus.dismiss();
                                                    finish();
                                                    startActivity(i);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(ViewInstrumentActivity.this, "Return Failed!", Toast.LENGTH_SHORT).show();
                                                    dialogPlus.dismiss();
                                                }
                                            });

                                        }
                                    });

                                    dialogPlus.show();

                                }
                            });

                            edit_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    DialogPlus dialogPlus = DialogPlus.newDialog(ViewInstrumentActivity.this).setGravity(Gravity.CENTER).setMargin(50, 50, 50, 50).setContentHolder(new ViewHolder(R.layout.instrument_edit_dialog)).setContentBackgroundResource(com.google.android.material.R.color.cardview_dark_background).setExpanded(false).create();

                                    View holderView = dialogPlus.getHolderView();
                                    ImageView image = holderView.findViewById(R.id.iv_bc);
                                    TextView title = holderView.findViewById(R.id.title);
                                    SearchView action_search = holderView.findViewById(R.id.search);
                                    ImageView scanQR = holderView.findViewById(R.id.scanQR);
                                    ImageView back = holderView.findViewById(R.id.back);
                                    TextView cat = holderView.findViewById(R.id.cat_spinner);
                                    TextView id = holderView.findViewById(R.id.edit_barcode);
                                    Spinner location = holderView.findViewById(R.id.i_location);
                                    Spinner status = holderView.findViewById(R.id.i_status);
                                    EditText notes = holderView.findViewById(R.id.i_notes);
                                    Button save = holderView.findViewById(R.id.btn_upload);

                                    title.setText("Edit Instrument");
                                    action_search.setVisibility(View.INVISIBLE);
                                    scanQR.setVisibility(View.INVISIBLE);

                                    cat.setText(instrument.getCategory());
                                    id.setText(instrument.getId());

                                    String imageUrl = null;
                                    imageUrl = instrument.getImg();
                                    Picasso.get().load(imageUrl).into(image);

                                    loc_spinner_list = new ArrayList<>();
                                    loc_spinner_list.add("Korogocho");
                                    loc_spinner_list.add("Mukuru Reuben");
                                    loc_spinner_list.add("Main Office");
                                    loc_spinner_list.add("Mombasa");

                                    loc_spinner_adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, loc_spinner_list);
                                    location.setAdapter(loc_spinner_adapter);
                                    location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            i_location = loc_spinner_list.get(i);
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });

                                    stat_spinner_list = new ArrayList<>();
                                    stat_spinner_list.add("Active");
                                    stat_spinner_list.add("Inactive");
                                    stat_spinner_list.add("Transit");

                                    stat_spinner_adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, stat_spinner_list);
                                    status.setAdapter(stat_spinner_adapter);
                                    status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            i_status = stat_spinner_list.get(i);
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
                                            if (!Objects.equals(i_location, instrument.getLocation()) || !Objects.equals(i_status, instrument.getStatus())) {
                                                map.put(instrument.getId(), new Instrument(instrument.getId(), instrument.getCategory(), i_location, i_status, instrument.getImg(), notes.getText().toString(), s_name));
                                                reference.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(ViewInstrumentActivity.this, "Changes Saved!", Toast.LENGTH_SHORT).show();
                                                        dialogPlus.dismiss();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ViewInstrumentActivity.this, "Changes Not Saved!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(ViewInstrumentActivity.this, "No changes noticed!", Toast.LENGTH_SHORT).show();
                                                dialogPlus.dismiss();
                                                Toast.makeText(ViewInstrumentActivity.this, "Exiting!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    dialogPlus.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void shareImg(Bitmap share_bmp, String share_name) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        Uri bmp_uri;
        bmp_uri = saveQR(share_name, share_bmp);
        share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.putExtra(Intent.EXTRA_STREAM, bmp_uri);
        share.putExtra(Intent.EXTRA_SUBJECT, "AOM App Share");
        share.putExtra(Intent.EXTRA_TEXT,share_name);
        startActivity(Intent.createChooser(share, "Share QR Code:"));
    }

    private Uri saveQR(String imgName, Bitmap bmp) {
        Uri image_collection = null;
        ContentResolver resolver = getContentResolver();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            image_collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            image_collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imgName + ".jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+ File.separator+"AOM_QRs");

        Uri imageUri = resolver.insert(image_collection, contentValues);

        try {
            OutputStream outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            Objects.requireNonNull(outputStream);
            return imageUri;
        } catch (Exception e) {
            Toast.makeText(this, "Image not saved \n" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return null;
    }

}