package com.bdo.shrey.instrumentmanager.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.bdo.shrey.instrumentmanager.AllStudentsActivity;
import com.bdo.shrey.instrumentmanager.Models.Instrument;
import com.bdo.shrey.instrumentmanager.Models.SDeletes;
import com.bdo.shrey.instrumentmanager.Models.Student;
import com.bdo.shrey.instrumentmanager.Models.User;
import com.bdo.shrey.instrumentmanager.R;
import com.bdo.shrey.instrumentmanager.ViewInstrumentActivity;
import com.bdo.shrey.instrumentmanager.ViewStudentActivity;
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

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private final DatabaseReference assign_ref = FirebaseDatabase.getInstance().getReference("Assignments");
    Context context;
    ArrayList<Student> student_list;
    String u_name,password;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String userID = mAuth.getCurrentUser().getUid();
    private final DatabaseReference database_ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
    private final DatabaseReference delete_ref = FirebaseDatabase.getInstance().getReference();

    public StudentAdapter(Context context, ArrayList<Student> student_list) {
        this.context = context;
        this.student_list = student_list;
    }

    @NonNull
    @Override
    public StudentAdapter.StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.student_viewholder, parent, false);
        return new StudentAdapter.StudentViewHolder(v);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.StudentViewHolder holder, int position) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");
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
                                Toast.makeText(context.getApplicationContext(), "This user object is null!", Toast.LENGTH_LONG).show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context.getApplicationContext(), "Activity was cancelled!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context.getApplicationContext(), "Activity was cancelled!", Toast.LENGTH_SHORT).show();
            }
        });

        Student student = student_list.get(position);
        holder.name.setText(student.getName());
        holder.code.setText(student.getId());
        holder.location.setText(student.getLocation());
        holder.status.setText(student.getStatus());

        if (student.getStatus().equals("Active")){
            holder.status.setTextColor(Color.GREEN);
        }else {
            holder.status.setTextColor(Color.RED);
        }

        if (!(student.getAssigned().equals(""))) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.stat.getBackground().setTint(Color.RED);
            }
            holder.stat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String i_id = student.getAssigned();
                    Toast.makeText(view.getContext(), "Assigned " + i_id + " to " + student.getName(), Toast.LENGTH_SHORT).show();
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Return instrument before deleting!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.stat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Ready to be Assigned!", Toast.LENGTH_SHORT).show();
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseDatabase.getInstance().getReference().child("Admin").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            password = snapshot.child("delete_pswd").getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context.getApplicationContext(), "Delete cancelled!", Toast.LENGTH_SHORT).show();
                            ((AllStudentsActivity) context).finish();
                        }
                    });
                    DialogPlus dialogPlus = DialogPlus.newDialog(context)
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

                    holder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context.getApplicationContext(), ViewStudentActivity.class);
                            intent.putExtra("id", student.getId());
                            intent.putExtra("status", student.getId());
                            context.startActivity(intent);
                        }
                    });

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!(edit_password.getText().toString().equals(password))) {
                                edit_password.setError("Enter the correct password");
                                Toast.makeText(context.getApplicationContext(), "Invalid Password!", Toast.LENGTH_SHORT).show();
                            } else {
                                SDeletes deleted_s = new SDeletes(student.getId(), student.getName(), student.getLocation(), student.getAssigned(), u_name);
                                delete_ref.child("Students").child(student.getId()).removeValue();
                                delete_ref.child("Students1").child(student.getLocation()).child(student.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        FirebaseDatabase.getInstance().getReference().child("Deleted_S").child(student.getId()).setValue(deleted_s).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(context.getApplicationContext(), "Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(context.getApplicationContext(), AllStudentsActivity.class);
                                                intent.putExtra("location", "");
                                                ((AllStudentsActivity) context).finish();
                                                context.startActivity(intent);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Delete Failed!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Delete Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    });
                    dialogPlus.show();
                }
            });
        }
        ;
    };

    @Override
    public int getItemCount() {
        return student_list.size();
    }

    public void searchData(ArrayList<Student> search_list){
        student_list = search_list;
        notifyDataSetChanged();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {

        TextView name, code, location, status;
        Button view, delete, stat;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.s_name);
            code = itemView.findViewById(R.id.s_code);
            location = itemView.findViewById(R.id.s_location);
            status = itemView.findViewById(R.id.s_status);
            view = itemView.findViewById(R.id.viewbtn);
            delete = itemView.findViewById(R.id.deletebtn);
            stat = itemView.findViewById(R.id.btn_stat);
        }
    }
}
