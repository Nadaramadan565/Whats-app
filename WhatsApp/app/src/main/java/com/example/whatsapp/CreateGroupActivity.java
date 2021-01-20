package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class CreateGroupActivity extends AppCompatActivity {
    //permission
    private static final int Camera_request_code=100;
    private static final int storage_request_code=200;
    //image pick constants
    private static final int Image_pick_Camera_code=300;
    private static final int Image_pick_gallary_code=400;
    // permission arrays
    private String [] camera_permissions;
    private String [] storage_permissions;
    //pickes image URI
    private Uri Uri_image=null;

    private ActionBar actionbar;
    private FirebaseAuth firebaseAuth;
    private ImageView groupIconIv;
    private EditText groupTitle, groupDescribtion;
    private FloatingActionButton createBtn;
    private FloatingActionButton change_img;
    private ProgressDialog progressDialog;
    private Toolbar main_tool ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);


        main_tool=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(main_tool);
        getSupportActionBar().setTitle("Create Group");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        groupIconIv=findViewById(R.id.groupIconIv);
        groupTitle=findViewById(R.id.groupTitle);
        groupDescribtion=findViewById(R.id.groupDescribtion);
        createBtn=findViewById(R.id.createBtn);
        change_img=findViewById(R.id.change_ImgGroup_btn);
        //init permissions arrays
        camera_permissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storage_permissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth=FirebaseAuth.getInstance();
        CheckUser();


        //picking image
        change_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();

            }
        });
        //click on button
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreatingGroup();
            }
        });

    }

    private void startCreatingGroup() {
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Creating Group");

        //input title description
         final String strgroupTitle=groupTitle.getText().toString().trim();
         final String strDescription=groupDescribtion.getText().toString().trim();
        //to make edit boxes is not empty
        if(TextUtils.isEmpty(strgroupTitle))
        {
            Toast.makeText(getApplicationContext(), "Please enter group title...", Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.show();

        //timeStamp: for group icon,droup id,timethat group was created in
        final String g_timeStamp=""+System.currentTimeMillis();
        if(Uri_image==null)
        {
            //creating group without image
            createGroup(""+g_timeStamp,""+strgroupTitle,""+strDescription,""+Uri_image);
        }
        else
        {
            //creating group with image
            //Upload Image
            //image name and path
            String fileNameandPath="Group_Imgs/"+"image"+g_timeStamp;

            StorageReference storageReference= FirebaseStorage.getInstance().getReference(fileNameandPath);
            storageReference.putFile(Uri_image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //image uploaded
                    Task<Uri> p_uriTask=taskSnapshot.getStorage().getDownloadUrl();
                    while(!p_uriTask.isSuccessful());
                    Uri p_downloadUri=p_uriTask.getResult();
                    if(p_uriTask.isSuccessful())
                    {
                        createGroup(""+g_timeStamp,""+strgroupTitle,""+strDescription,""+p_downloadUri);

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //faileduploading image
                    Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });


        }
    }

    private void createGroup(final String g_timeStamp, String title, String description, String groupIcon)
    {
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("groupId",""+g_timeStamp);
        hashMap.put("groupTitle",""+title);
        hashMap.put(" groupDescription",""+description);
        hashMap.put("groupIcon",""+groupIcon);
        hashMap.put("TimeStamp",""+g_timeStamp);
        hashMap.put("createdBy",""+firebaseAuth.getUid());
        //create group
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(g_timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //created successfully
                //setup member info (add current user in group's participants list)
                HashMap<String,String> hash=new HashMap<>();
                hash.put("Uid",firebaseAuth.getUid());
                hash.put("Role","Creator");
                hash.put("Time Stamp",g_timeStamp);
                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
                ref1.child(g_timeStamp).child("Participants").child(firebaseAuth.getUid())
                        .setValue(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//participant added
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Group Created Successfully", Toast.LENGTH_LONG).show();

                    onBackPressed();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//failed dding partcipants
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_LONG).show();


                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//failed
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
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


    private void CheckUser() {
        FirebaseUser Current_user=firebaseAuth.getCurrentUser();
        if(Current_user!=null)
        {

        }
    }
    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return super.onSupportNavigateUp();
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