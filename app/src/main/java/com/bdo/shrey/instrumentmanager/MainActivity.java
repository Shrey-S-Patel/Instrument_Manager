package com.bdo.shrey.instrumentmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bdo.shrey.instrumentmanager.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    ImageView menu;
    CardView instruments, students, add, scan, account, reports, logout;
    TextView user_name;
    DatabaseReference database_ref;
    String name_txt;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu = findViewById(R.id.menu);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Account", Toast.LENGTH_SHORT).show();
            }
        });

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("Users");

        //Authentication Instance started.
        mAuth = FirebaseAuth.getInstance();

        user_name = findViewById(R.id.name_space);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(MainActivity.this, "Logged Out.", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "User must be signed in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            finish();
        }

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
                                String u_name = "Hello, " + user.getName();
                                user_name.setText(u_name);
                            } else {
                                Toast.makeText(MainActivity.this, "This user object is null!", Toast.LENGTH_LONG).show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Activity was cancelled!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        instruments = findViewById(R.id.instrument_txt);
        instruments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InstrumentsActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Instruments", Toast.LENGTH_SHORT).show();
            }
        });

        students = findViewById(R.id.students);
        students.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), StudentActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Students", Toast.LENGTH_SHORT).show();
            }
        });

        add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateInstrumentActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Create Instrument", Toast.LENGTH_SHORT).show();
            }
        });

        scan = findViewById(R.id.scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Scan", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), ScanActivity.class));
            }
        });

        account = findViewById(R.id.account);
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Account", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), AccountActivity.class));
            }
        });

        reports = findViewById(R.id.reports);
        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Reports", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), ReportsActivity.class));
            }
        });

        logout = findViewById(R.id.logout_txt);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Logged Out.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });

    }
    private long pressedTime;

    @Override
    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
//            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();

    }
}