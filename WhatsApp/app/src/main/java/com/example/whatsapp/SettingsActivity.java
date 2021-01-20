package com.example.whatsapp;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    //permission
    private static final int Camera_request_code=100;
    private static final int storage_request_code=200;
    //image pick constants
    private static final int Image_pick_Camera_code=300;
    private static final int Image_pick_gallary_code=400;
    private CircleImageView profileImg;
    private Uri imgeSetting;

    //pickes image URI
    private Uri Uri_image=null;


    private Toolbar main_tool ;

    private DatabaseReference mdatabase;
    private FirebaseAuth mAuth;
    String phone;
    private  EditText name;

    // permission arrays
    private String [] camera_permissions;
    private String [] storage_permissions;
    private FloatingActionButton change_img;

    private ImageView groupIconIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        groupIconIv=findViewById(R.id.groupIconIv);
        main_tool=(Toolbar)findViewById(R.id.main_page_toolbar);
        name=(EditText) findViewById(R.id.nametxt);
        mAuth = FirebaseAuth.getInstance();
        phone=getIntent().getStringExtra("Phone Number");

        change_img=findViewById(R.id.change_ImgGroup_btn);
        //init permissions arrays
        camera_permissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storage_permissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        setSupportActionBar(main_tool);
        getSupportActionBar().setTitle("Your Settings");

        //init permissions arrays
        camera_permissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storage_permissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //picking image
        change_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();

            }
        });

        findViewById(R.id.chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!name.getText().toString().isEmpty()) {
                    FirebaseUser current_user=FirebaseAuth.getInstance().getCurrentUser();
                    String UID =current_user.getUid();
                    mdatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(UID);

                    HashMap<String ,String> userMap=new HashMap<>();

                    userMap.put("name",name.getText().toString());
                    userMap.put("status","Hi I'm using Mini WhatsApp");
                    userMap.put("image",""+Uri_image);
                    userMap.put("Phone Number",phone);
                    mdatabase.setValue(userMap);
                    Toast.makeText(getApplicationContext(),"added to db",Toast.LENGTH_LONG).show();


                    Intent start = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(start);
                    finish();
                }
                else
                {

                    name.setError(Html.fromHtml("<font color='#FFFFFF'>Please Enter your name to START</font>"));
                    name.requestFocus();
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Toast.makeText(getApplicationContext(),"Change Image",Toast.LENGTH_LONG).show();
                return true;
            case R.id.item2:
                Toast.makeText(getApplicationContext(),"Change status",Toast.LENGTH_LONG).show();
                return true;


        }
        return false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m,menu);
        return super.onCreateOptionsMenu(menu);

    }


    private void showImagePickDialog()
    {
        String[] options={"Camera","Gallery"};
        //dailog
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Pick Image").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle clicked
                if(which==0)
                {
                    if(!checkCameraPermissions())
                    {
                        reqquestCameraPermissions();
                    }
                    else
                    {
                        pickFromCamera();
                    }
                }
                else
                {
                    //gallery clicked
                    if(!checkStoragePermissions())
                    {
                        reqquestStoragePermissions();
                    }
                    else
                    {
                        pickFromGallery();
                    }
                }
            }
        }).show();
    }

    private void pickFromGallery()
    {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,Image_pick_gallary_code);


    }
    private void pickFromCamera()
    {
        ContentValues cv=new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Group Image Icon Title");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Group Image Icon Description");
        Uri_image=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);

        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri_image);
        startActivityForResult(intent,Image_pick_Camera_code);

    }

    private boolean checkStoragePermissions()
    {
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private boolean checkCameraPermissions()
    {
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);

        boolean  result1= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void reqquestStoragePermissions()
    {
        ActivityCompat.requestPermissions(this,storage_permissions,storage_request_code);
    }



    private void reqquestCameraPermissions()
    {
        ActivityCompat.requestPermissions(this,camera_permissions,Camera_request_code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Camera_request_code: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        //permissions allowed
                        pickFromCamera();
                    } else {
                        //  both or one of them is denied
                        Toast.makeText(getApplicationContext(), "Camera & Storage permissions are required", Toast.LENGTH_LONG).show();

                    }

                }
            }
            break;
            case storage_request_code:
            {
                if (grantResults.length > 0)
                {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted)
                    {
                        //permissions allowed
                        pickFromGallery();
                    } else {
                        //  both or one of them is denied
                        Toast.makeText(getApplicationContext(), "Storage permissions are required", Toast.LENGTH_LONG).show();

                    }
                }


            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //handle image pick result
        if(requestCode==Image_pick_gallary_code)
        {
            //we picked from gallery
            Uri_image=data.getData();
            //set to image view
            groupIconIv.setImageURI(Uri_image);

        }
        else if(requestCode==Image_pick_Camera_code)
        {
            // we picked from camera

            //set to image view
            groupIconIv.setImageURI(Uri_image);


        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}