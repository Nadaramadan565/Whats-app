package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUp extends AppCompatActivity {

    private DatabaseReference mUserdatabase;
    private FirebaseUser mCurrentUser;
    //androidlayout

    private CircleImageView mDisplayImage;
    private EditText mName;
    private EditText mStatus;
    private Button Update;
    private Button Update_image_btn;
    private FirebaseAuth mAuth;
    private String mCurrentUserid;
    private  static final int gallarypick=1;
    private StorageReference userprofileimageref;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        mAuth=FirebaseAuth.getInstance();

        mCurrentUserid=mAuth.getCurrentUser().getUid();
        mUserdatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUserid);

        userprofileimageref= FirebaseStorage.getInstance().getReference();
        Initializefields();

        //  mName.setVisibility(View.VISIBLE);;
        // mDisplayImage.setVisibility(View.VISIBLE);
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();

            }
        });



        Update_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallaryintent=new Intent();

                gallaryintent.setAction(Intent.ACTION_GET_CONTENT);
                gallaryintent.setType("image/*");
                startActivityForResult(Intent.createChooser( gallaryintent,"Select image") ,gallarypick);



            }
        });

        Retrieveuserinfo();


    }

    private void UpdateSettings() {

        String setusername=mName.getText().toString();
        String setuserStatus=mStatus.getText().toString();
        //String setimage=mDisplayImage.toString();


        if(TextUtils.isEmpty(setusername))
        {
            Toast.makeText(this, "Please Enter Your User Name", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(setuserStatus))
        {
            Toast.makeText(this, "Please Enter Your Status", Toast.LENGTH_SHORT).show();
        }
        else
        {

            mUserdatabase.child(mCurrentUserid).setValue(setuserStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful())
                    {

                        Toast.makeText(SetUp.this, "Status Updated Successfully", Toast.LENGTH_SHORT).show();
                        SendUserTomainactivity();
                    }
                    else
                    {
                        String massage=task.getException().toString();

                        Toast.makeText(SetUp.this, "Error"+ massage, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mUserdatabase.child(mCurrentUserid).setValue(setusername).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful())
                    {

                        Toast.makeText(SetUp.this, "Name Updated Successfully", Toast.LENGTH_SHORT).show();
                        SendUserTomainactivity();
                    }
                    else
                    {
                        String massage=task.getException().toString();

                        Toast.makeText(SetUp.this, "Error"+ massage, Toast.LENGTH_SHORT).show();
                    }
                }
            });




        }

    }

    private void SendUserTomainactivity() {
        Intent start=new Intent(SetUp.this,MainActivity.class);
        start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(start);
        finish();
    }

    private void Retrieveuserinfo() {

        mUserdatabase.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot snapshot) {



                //  String c=mUserdatabase.child(mCurrentUserid).toString();
                String retreveusername= snapshot.child("name").getValue().toString();
                String retreveuserstatus= snapshot.child("status").getValue().toString();
                String retreveprofileimage=snapshot.child("image").getValue().toString();

                mName.setText(retreveusername);
                mStatus.setText(retreveuserstatus);

                Picasso.get().load(retreveprofileimage).into(mDisplayImage);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void Initializefields() {

        mDisplayImage=(CircleImageView)findViewById(R.id.profile_image);
        mName=(EditText)findViewById(R.id.settind_name);
        mStatus=(EditText) findViewById(R.id.settings_states);

        Update=(Button)findViewById(R.id.Update_btn);
        Update_image_btn=(Button)findViewById(R.id.Update_image_btn);

        loadingbar=new ProgressDialog(this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==gallarypick && resultCode==RESULT_OK )
        {
            Uri uriimage=data.getData();


            CropImage.activity(uriimage)
                    .setAspectRatio(1,1)
                    .start(SetUp.this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode== RESULT_OK)
            {
                final Uri resulturi=result.getUri();
                //  String userid =mCurrentUser.getUid();
                loadingbar.setTitle("set Profile Image");
                loadingbar.setMessage("Please Wait until until your profile image is Updateing ......");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();



                StorageReference filepath=userprofileimageref.child("profile Image").child("profile image.jpg");
                filepath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {

                            String download_url=resulturi.toString();
                            Toast.makeText(SetUp.this, "Profile Image Upload Successfully", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                            mUserdatabase.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    { loadingbar.dismiss();

                                        Toast.makeText(SetUp.this, " Image Uploaded ... ", Toast.LENGTH_SHORT).show();

                                    }


                                }
                            });
                        }
                        else {
                            String massage=task.getException().toString();

                            Toast.makeText(SetUp.this, "Error"+ massage, Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }

                    }
                });
            }

        }
    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

}
//esraaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa/