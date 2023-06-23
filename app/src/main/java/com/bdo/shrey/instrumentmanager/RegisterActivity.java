package com.bdo.shrey.instrumentmanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bdo.shrey.instrumentmanager.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText edit_name, edit_number, edit_email, edit_password;
    Button btn_register;
    TextView text_sign_in;
    ProgressBar progress_bar;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Realtime Database connection started.
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");

        //Authentication Instance started.
        mAuth = FirebaseAuth.getInstance();

        edit_name = findViewById(R.id.name);
        edit_number = findViewById(R.id.phone_number);
        edit_email = findViewById(R.id.email);
        edit_password = findViewById(R.id.password);
        btn_register = findViewById(R.id.btn_register);
        text_sign_in = findViewById(R.id.text_sign_in);
        progress_bar = findViewById(R.id.progress_bar);

        text_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), FirebaseUIActivity.class));
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_bar.setVisibility(View.VISIBLE);
                btn_register.setVisibility(View.INVISIBLE);
                String name, number, email, password;
                name = edit_name.getText().toString();
                number = edit_number.getText().toString();
                email = edit_email.getText().toString();
                password = String.valueOf(edit_password.getText());

                if (TextUtils.isEmpty(name)) {
                    edit_name.setError("Number Cannot Be Empty!");
                    Toast.makeText(RegisterActivity.this, "Name Cannot Be Empty!", Toast.LENGTH_SHORT).show();
                    progress_bar.setVisibility(View.INVISIBLE);
                    btn_register.setVisibility(View.VISIBLE);
                    return;
                }

                if (TextUtils.isEmpty(number)) {
                    edit_number.setError("Number Cannot Be Empty!");
                    Toast.makeText(RegisterActivity.this, "Number Cannot Be Empty!", Toast.LENGTH_SHORT).show();
                    progress_bar.setVisibility(View.INVISIBLE);
                    btn_register.setVisibility(View.VISIBLE);
                    return;
                }

                if (number.length() != 10) {
                    edit_number.setError("Number Must Have 10 Digits.");
                }

                if (TextUtils.isEmpty(email)) {
                    edit_email.setError("email Cannot Be Empty!");
                    Toast.makeText(RegisterActivity.this, "Email Cannot Be Empty!", Toast.LENGTH_SHORT).show();
                    progress_bar.setVisibility(View.INVISIBLE);
                    btn_register.setVisibility(View.VISIBLE);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    edit_password.setError("Password Cannot Be Empty!");
                    Toast.makeText(RegisterActivity.this, "Password Cannot Be Empty!", Toast.LENGTH_SHORT).show();
                    progress_bar.setVisibility(View.INVISIBLE);
                    btn_register.setVisibility(View.VISIBLE);
                    return;
                }

                createAccount(name, number, email, password);


            }
        });
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
    // [END on_start_check_user]

    private void createAccount(String name, String number, String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Creating a user profile in the realtime database
                        progress_bar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            DatabaseReference myRef = database.getReference("Users");
                            User user = new User(email, name, number, password);

                            mAuth = FirebaseAuth.getInstance();
                            String userID = mAuth.getCurrentUser().getUid();

                            myRef.child(userID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Registration Successful.",
                                                Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), FirebaseUIActivity.class));
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterActivity.this, e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                            edit_name.setText("");
                            edit_number.setText("");
                            edit_email.setText("");
                            edit_password.setText("");
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration Failed.",
                                    Toast.LENGTH_SHORT).show();
                            edit_name.setText("");
                            edit_number.setText("");
                            edit_email.setText("");
                            edit_password.setText("");
                        }
                    }
                });
        // [END create_user_with_email]


    }


}