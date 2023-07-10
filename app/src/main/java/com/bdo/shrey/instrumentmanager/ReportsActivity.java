package com.bdo.shrey.instrumentmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.ViewCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bdo.shrey.instrumentmanager.Models.Category;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReportsActivity extends AppCompatActivity {

    TextView title;
    ImageView back, scanQR;
    SearchView action_search;
    private TextView tv2;
    private ChipGroup group1, group2, group3;
    private Button btn_generate;
    private String choice1, choice2, choice3;
    private Boolean has_choice;
    private final DatabaseReference spinner_ref = FirebaseDatabase.getInstance().getReference().child("Categories");
    private ArrayList<String> cat_spinner_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        title = findViewById(R.id.title);
        title.setText("Create Report");

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


        cat_spinner_list = new ArrayList<>();
        ShowData();

        group1 = findViewById(R.id.chip_group);
        group1.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                Chip chip = group.findViewById(checkedIds.get(0));
                choice1 = chip.getText().toString();
                Toast.makeText(ReportsActivity.this, "Your choice is " + choice1, Toast.LENGTH_SHORT).show();
            }
        });

        group2 = findViewById(R.id.chip_group_1);
        group2.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                Chip chip = group.findViewById(checkedIds.get(0));
                choice2 = chip.getText().toString();
                Toast.makeText(ReportsActivity.this, "Your choice is " + choice2, Toast.LENGTH_SHORT).show();
                group3.removeAllViews();
                showGroup3(choice2);
            }
        });

        tv2 = findViewById(R.id.textView2);
        tv2.setVisibility(View.INVISIBLE);
        group3 = findViewById(R.id.chip_group_2);
        group3.setVisibility(View.INVISIBLE);

        btn_generate = findViewById(R.id.btn_generate);
        btn_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choice1.equals("Instruments") && choice2.equals("Master")){
                    startActivity(new Intent(getApplicationContext(), InsMasterActivity.class));
                }

                /*Intent intent = new Intent(getApplicationContext(), ReportsActivity.class);
                intent.putExtra("choice1", choice1);
                intent.putExtra("choice2", choice2);
                intent.putExtra("choice3", choice3);*/
            }
        });
    }
    private void ShowData() {
        spinner_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    Category cat = item.getValue(Category.class);
                    assert cat != null;
                    cat_spinner_list.add(cat.getCat_name());
                }
                Toast.makeText(ReportsActivity.this, cat_spinner_list.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NewApi")
    private void showGroup3(String choice2) {
        tv2.setVisibility(View.VISIBLE);
        group3.setVisibility(View.VISIBLE);
        
        if (Objects.equals(choice2, "Location")){
            has_choice = true;
            ArrayList<String> loc_spinner_list;
            loc_spinner_list = new ArrayList<>();
            loc_spinner_list.add("Korogocho");
            loc_spinner_list.add("Mukuru Reuben");
            loc_spinner_list.add("Main Office");
            loc_spinner_list.add("Mombasa");

            for (String loc: loc_spinner_list) {
                Chip chip = new Chip(new ContextThemeWrapper(this, com.google.android.material.R.style.Widget_Material3_Chip_Filter));
                chip.setId(ViewCompat.generateViewId());
                chip.setCheckable(true);
                chip.setCheckedIconVisible(true);
                chip.setCheckedIconResource(R.drawable.check);
                chip.setClickable(true);
                chip.setChipStrokeColor(ColorStateList.valueOf(getColor(R.color.teal_700)));
                chip.setChipStrokeWidth(8);
                chip.setText(loc);
                group3.addView(chip);
            }

            group3.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                @Override
                public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                    Chip chip = group.findViewById(checkedIds.get(0));
                    choice3 = chip.getText().toString();
                    Toast.makeText(ReportsActivity.this, "Your choice is " + choice3, Toast.LENGTH_SHORT).show();
                }
            });
            
        } else if (Objects.equals(choice2, "Instrument Category")) {
            has_choice = true;
            Toast.makeText(this, cat_spinner_list.toString(), Toast.LENGTH_SHORT).show();
            for (String cat: cat_spinner_list) {
                Chip chip = new Chip(new androidx.appcompat.view.ContextThemeWrapper(this, com.google.android.material.R.style.Widget_Material3_Chip_Filter));
                chip.setId(ViewCompat.generateViewId());
                chip.setCheckable(true);
                chip.setCheckedIconVisible(true);
                chip.setCheckedIconResource(R.drawable.check);
                chip.setClickable(true);
                chip.setChipStrokeColor(ColorStateList.valueOf(getColor(R.color.teal_700)));
                chip.setChipStrokeWidth(8);
                chip.setText(cat);
                group3.addView(chip);
            }
        }else {
            tv2.setVisibility(View.INVISIBLE);
            group3.setVisibility(View.INVISIBLE);
            choice3 = "none";
            has_choice = false;
        }

    }

}