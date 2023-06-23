package com.bdo.shrey.instrumentmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;

import com.bdo.shrey.instrumentmanager.Models.User;
import com.bdo.shrey.instrumentmanager.Models.feedback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class AccountActivity extends AppCompatActivity {

    AppCompatButton button;
    Button button2;
    RelativeLayout feedback, logout_rl;
    ImageView back, profile;
    TextView username, useremail;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference database_ref;

    String u_name, u_email, u_number, u_password;
    String uemail, umobile, uname, upassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        button = findViewById(R.id.editbtn);
        feedback = findViewById(R.id.feedback);
        logout_rl = findViewById(R.id.logout_rl);

        back = findViewById(R.id.back);
        profile = findViewById(R.id.profile);

        username = findViewById(R.id.username);
        useremail = findViewById(R.id.useremail);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid());

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");

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
                                u_email = user.getEmail();
                                u_number = user.getNumber();
                                u_password = user.getPassword();
                                username.setText(u_name);
                                useremail.setText(u_email);
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

        logout_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), FirebaseUIActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });


        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogPlus dialogPlus0 = DialogPlus.newDialog(AccountActivity.this)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50, 0, 50, 0)
                        .setContentHolder(new ViewHolder(R.layout.feedback_dialog))
                        .setContentBackgroundResource(com.google.android.material.R.color.cardview_dark_background)
                        .setExpanded(false)
                        .create();

                View holderView = dialogPlus0.getHolderView();
                TextInputEditText p_name = (TextInputEditText) holderView.findViewById(R.id.name);
                TextInputEditText p_email = (TextInputEditText) holderView.findViewById(R.id.email);
                TextInputEditText p_company = (TextInputEditText) holderView.findViewById(R.id.company);
                TextInputEditText p_mobile = (TextInputEditText) holderView.findViewById(R.id.mobile);
                SearchView action_search = holderView.findViewById(R.id.search);
                ImageView scanQR = holderView.findViewById(R.id.scanQR);
                ImageView back = holderView.findViewById(R.id.back);
                TextView title = holderView.findViewById(R.id.title);
                Button button2 = holderView.findViewById(R.id.button2);

                title.setText("Give Feedback");
                action_search.setVisibility(View.INVISIBLE);
                scanQR.setVisibility(View.INVISIBLE);


                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogPlus0.dismiss();
                    }
                });

                DatabaseReference mref = database.getReference().child("Feedback");


                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String uname = p_name.getText().toString().trim().toLowerCase();
                        String uemail = p_email.getText().toString().trim().toLowerCase();
                        String ucompany = p_company.getText().toString().trim().toLowerCase();
                        String umobile = p_mobile.getText().toString().trim().toLowerCase();

                        feedback feedback1 = new feedback(uname, uemail, ucompany, umobile);


                        mref.push().setValue(feedback1)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                                        dialogPlus0.dismiss();
                                    }
                                });
                    }
                });

                dialogPlus0.show();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);*/
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogPlus dialogPlus = DialogPlus.newDialog(AccountActivity.this)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50, 0, 50, 0)
                        .setContentHolder(new ViewHolder(R.layout.profile_dialog))
                        .setContentBackgroundResource(com.google.android.material.R.color.cardview_dark_background)
                        .setExpanded(false)
                        .create();

                View holderView = dialogPlus.getHolderView();
                TextInputEditText name = holderView.findViewById(R.id.name);
                TextInputEditText email = holderView.findViewById(R.id.email);
                TextInputEditText password = holderView.findViewById(R.id.password_profile);
                TextInputEditText mobile = holderView.findViewById(R.id.mobile);
                SearchView action_search = holderView.findViewById(R.id.search);
                ImageView scanQR = holderView.findViewById(R.id.scanQR);
                ImageView back = holderView.findViewById(R.id.back);
                TextView title = holderView.findViewById(R.id.title);
                Button button2 = holderView.findViewById(R.id.button2);

                title.setText("Edit Profile");
                action_search.setVisibility(View.INVISIBLE);
                scanQR.setVisibility(View.INVISIBLE);


                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogPlus.dismiss();
                    }
                });


                String name_txt = u_name;
                String email_txt = u_email;
                String number_txt = u_number;

                useremail.setText(email_txt);
                username.setText(name_txt);

                name.setText(name_txt);
                email.setText(email_txt);
                mobile.setText(number_txt);

                uname = name.getText().toString().trim();
                uemail = email.getText().toString().trim();
                upassword = password.getText().toString().trim();
                umobile = mobile.getText().toString().trim();

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String old_mail = mAuth.getCurrentUser().getEmail();
                        if (!(old_mail.equals(uemail))) {
                            mAuth.getCurrentUser().updateEmail(uemail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getApplicationContext(), "Email changed successfully", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });
                        }

                        if (!(upassword.isEmpty())) {
                            mAuth.getCurrentUser().updatePassword(upassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getApplicationContext(), "Password changed successfully", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });
                        } else {
                            upassword = u_password;
                        }

                        User user = new User(uemail, uname, umobile, upassword);
                        myRef.child(userID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AccountActivity.this, "Update Successful.",
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), FirebaseUIActivity.class));
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AccountActivity.this, e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                dialogPlus.show();

            }
        });


    }
}