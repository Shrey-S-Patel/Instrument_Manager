package com.bdo.shrey.instrumentmanager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import com.bdo.shrey.instrumentmanager.Models.Category;
import com.bdo.shrey.instrumentmanager.Models.Instrument;
import com.bdo.shrey.instrumentmanager.Models.Stock;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CreateInstrumentActivity extends AppCompatActivity {
    TextView title;
    ImageView back, scanQR;
    SearchView action_search;

    EditText edit_barcode;
    Button btn_generate, btn_upload;
    ImageView iv_bc;
    Bitmap bitmap, bitmap_copy;
    ActivityResultLauncher<String[]> mPermissionLauncher;
    ActivityResultLauncher<Intent> mGetImage;
    private Uri qr_uri;
    private boolean is_read_granted = false;
    private boolean is_write_granted = false;
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference("Instruments");
    private StorageReference reference;

    private Spinner cat_spinner, loc_spinner, stat_spinner;
    private final DatabaseReference spinner_ref = FirebaseDatabase.getInstance().getReference("cat_spinner");
    private final DatabaseReference cat_ref = FirebaseDatabase.getInstance().getReference("Categories");
    private ArrayList<String> cat_spinner_list, loc_spinner_list, stat_spinner_list;
    private ArrayList<Category> catnum_spin_list;
    private ArrayAdapter<String> cat_spinner_adapter, loc_spinner_adapter, stat_spinner_adapter;
    private String cat_select, catnum_select, cat_code, i_code, i_location, i_status;
    private int catnum, cat_code_count, cat_a_count, cat_in_count, cat_t_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_instrument);

        mPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                if (result.get(Manifest.permission.READ_EXTERNAL_STORAGE) != null) {
                    is_read_granted = result.get(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                if (result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != null) {
                    is_write_granted = result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });

        requestPermission();

        title = findViewById(R.id.title);
        title.setText("Create Instrument");

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

        cat_spinner = findViewById(R.id.cat_spinner);
        loc_spinner = findViewById(R.id.i_location);
        stat_spinner = findViewById(R.id.i_status);

        cat_spinner_list = new ArrayList<>();
        catnum_spin_list = new ArrayList<>();
        cat_spinner_adapter = new ArrayAdapter<String>(CreateInstrumentActivity.this, android.R.layout.simple_spinner_dropdown_item, cat_spinner_list);
        cat_spinner.setAdapter(cat_spinner_adapter);
        ShowData();

        cat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cat_select = cat_spinner_list.get(i);
                if (cat_select.equals("Select a Category")) {
                    Toast.makeText(getApplicationContext(), "Please Select a Category", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Category cat = catnum_spin_list.get(i);
                    catnum_select = String.valueOf(cat.getCat_code_count());
                    cat_code = cat.getCat_code();
                    cat_code_count = cat.getCat_count();
                    cat_a_count = cat.getActive();
                    cat_in_count = cat.getInactive();
                    cat_t_count = cat.getTransit();
                    //Toast.makeText(getApplicationContext(), cat_select + " Selected...", Toast.LENGTH_SHORT).show();
                    generateCode(cat_select, catnum_select);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(), "Select A Category!", Toast.LENGTH_SHORT).show();
            }
        });

        loc_spinner_list = new ArrayList<>();
        loc_spinner_list.add("Korogocho");
        loc_spinner_list.add("Mukuru Reuben");
        loc_spinner_list.add("Main Office");
        loc_spinner_list.add("Mombasa");

        loc_spinner_adapter = new ArrayAdapter<>(CreateInstrumentActivity.this, android.R.layout.simple_spinner_dropdown_item, loc_spinner_list);
        loc_spinner.setAdapter(loc_spinner_adapter);
        loc_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        stat_spinner_adapter = new ArrayAdapter<>(CreateInstrumentActivity.this, android.R.layout.simple_spinner_dropdown_item, stat_spinner_list);
        stat_spinner.setAdapter(stat_spinner_adapter);
        stat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                i_status = stat_spinner_list.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        edit_barcode = findViewById(R.id.edit_barcode);
        btn_generate = findViewById(R.id.btn_generate);
        btn_upload = findViewById(R.id.btn_upload);
        iv_bc = findViewById(R.id.iv_bc);

        cat_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Category cat = dataSnapshot.getValue(Category.class);
                    catnum_spin_list.add(cat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btn_generate.setOnClickListener(v -> {
            generateQR();
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String img_name = edit_barcode.getText().toString();
                if (bitmap != null && !img_name.equals("")) {
                    if (saveQR(img_name, bitmap)) {
//                        Toast.makeText(CreateInstrumentActivity.this, "Image Saved Locally!", Toast.LENGTH_SHORT).show();
                        reference = FirebaseStorage.getInstance().getReference().child("QRs/" + img_name);
                        // Code for showing progressDialog while uploading
                        ProgressDialog progressDialog = new ProgressDialog(CreateInstrumentActivity.this);
                        progressDialog.setTitle("Uploading...");
                        progressDialog.show();
                        reference.putFile(qr_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Instrument instrument = new Instrument(i_code, cat_select, i_location, i_status, uri.toString(), "", "");
                                                if (i_status.equals("Active")){
                                                    cat_a_count = cat_a_count + 1;
                                                }else if (i_status.equals("Inactive")){
                                                    cat_in_count = cat_in_count + 1;
                                                } else if (i_status.equals("Transit")) {
                                                    cat_t_count = cat_t_count + 1;
                                                }
                                                Category cat = new Category(cat_select, cat_code, cat_code_count, catnum, cat_a_count,cat_in_count,cat_t_count);
                                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                DatabaseReference myRef = database.getReference("Categories");
                                                myRef.child(cat_select).setValue(cat);
                                                database.getReference("Instruments1").child(cat_select).child(i_code).setValue(instrument);
                                                root.child(i_code).setValue(instrument).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        // Image uploaded successfully
                                                        // Dismiss dialog
                                                        progressDialog.dismiss();
                                                        Toast.makeText(CreateInstrumentActivity.this, "Instrument Created.", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(getApplicationContext(), AllInstrumentsActivity.class);
                                                        intent.putExtra("category", "");
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                })

                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        // Error, Image not uploaded
                                        progressDialog.dismiss();
                                        Toast.makeText(CreateInstrumentActivity.this, "Upload Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(CreateInstrumentActivity.this, "Image Not Saved!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreateInstrumentActivity.this, "Generate the QR first!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void ShowData() {
        spinner_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    cat_spinner_list.add(String.valueOf(item.getValue()));
                }
                cat_spinner_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateInstrumentActivity.this, "Error : " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean saveQR(String imgName, Bitmap bmp) {
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
            bitmap_copy = bmp;
            qr_uri = imageUri;
            return true;
        } catch (Exception e) {
            Toast.makeText(this, "Image not saved \n" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return false;
    }

    private void generateQR() {
        i_code = edit_barcode.getText().toString().trim();
        String deets = "id: " + i_code + "\ncategory: " + cat_select + "\nlocation: " + i_location + "\nstatus: " + i_status;
        String ins_code = deets;
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(ins_code, BarcodeFormat.QR_CODE, 800, 800);

            BarcodeEncoder encoder = new BarcodeEncoder();
            bitmap = encoder.createBitmap(matrix);

            iv_bc.setImageBitmap(bitmap);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    private void requestPermission() {
        boolean minSDK = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;

        is_read_granted = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        is_write_granted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        is_read_granted = is_write_granted || minSDK;

        List<String> permissionRequest = new ArrayList<String>();

        if (!is_read_granted) {
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!is_write_granted) {
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionRequest.isEmpty()) {
            mPermissionLauncher.launch(permissionRequest.toArray(new String[0]));
        }
    }

    private void generateCode(String cat_select, String catnum_select) {
        catnum = Integer.parseInt(catnum_select);
        catnum = catnum + 1;
        cat_code_count = cat_code_count + 1;
        String i_code = "AOM_" + cat_select + "_" + catnum;
        edit_barcode.setText(i_code);
    }
}