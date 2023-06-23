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

import com.bdo.shrey.instrumentmanager.Models.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateCategoryActivity extends AppCompatActivity {
    TextView title;
    ImageView back, scanQR;
    SearchView action_search;
    TextInputEditText cat_name, cat_code;
    Button btn_create;

    String name, code;

    String count;
    Category cats;
    private FirebaseDatabase database;
    private final DatabaseReference spinner_ref = FirebaseDatabase.getInstance().getReference("cat_spinner");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);
        title = findViewById(R.id.title);
        title.setText(R.string.create_category);

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

        cat_name = findViewById(R.id.category_name);
        cat_code = findViewById(R.id.category_code);
        btn_create = findViewById(R.id.btn_cat_create);

        database = FirebaseDatabase.getInstance();
        DatabaseReference cat_ref = database.getReference().child("Categories");
        DatabaseReference counter_ref = database.getReference().child("Counters").child("cat_code");

        counter_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = (String) snapshot.getValue();
                if (snapshot.exists()) {
//                    counter = count.getCat_code();
                    cat_code.setText(count);
                    Toast.makeText(CreateCategoryActivity.this, count, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateCategoryActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        /*cat_ref.orderByChild("cat_name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cats = snapshot.getValue(Category.class);
                Toast.makeText(CreateCategoryActivity.this, cats.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/


        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = String.valueOf(cat_name.getText());
                code = String.valueOf(cat_code.getText());

                cat_ref.orderByChild("cat_name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(CreateCategoryActivity.this, "Category Exists", Toast.LENGTH_SHORT).show();
                            cat_name.setText("");
                            name = "";
                            cat_name.setError("Category Exists!");
                            return;
                        } else {
                            Toast.makeText(CreateCategoryActivity.this, "Creating Category", Toast.LENGTH_SHORT).show();
                            if (!(name.isEmpty() || code.isEmpty())) {
                                Category category = new Category(name, code, 0, 0);
                                cat_ref.child(name).setValue(category).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            int counter_new = Integer.parseInt(count) + 1;
                                            counter_ref.setValue(String.valueOf(counter_new));

                                            String key = spinner_ref.push().getKey();
                                            spinner_ref.child(name).setValue(name);

                                            Toast.makeText(CreateCategoryActivity.this, "Category Created.", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CreateCategoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CreateCategoryActivity.this, "Activity Cancelled!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }
}