package com.example.whatsapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PhoneEntry extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText phonetxt,codetxt;
    String codeSent;
    private Toolbar main_tool ;
    private DatabaseReference mdatabase;
    boolean var;
    CountDownTimer timer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_entry);



        mAuth = FirebaseAuth.getInstance();


        phonetxt = findViewById(R.id.Phonetxt);
        codetxt = findViewById(R.id.codetxt);
        final Button btn=(Button) findViewById(R.id.btn_get_verfication);

        main_tool=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(main_tool);
        getSupportActionBar().setTitle("Verification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        findViewById(R.id.btn_get_verfication).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerficationCode();
                if(!TextUtils.isEmpty(phonetxt.getText().toString()) && phonetxt.getText().length()>10) {
                  timer=  new CountDownTimer(60000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            btn.setText("Seconds remaining: " + millisUntilFinished / 1000);
                        }

                        public void onFinish() {
                            btn.setText("Try Again!");
                        }

                    }.start();

                }
                else
                {
                    btn.setText("Get Verification Code");
                }
              }
        });
        findViewById(R.id.signinBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(codetxt.getText()))
                {
                    VerifySignInCode();

                }
                else {
                    codetxt.setError("Please enter Varfication code");
                    codetxt.requestFocus();
                }
            }
        });
    }
    public void VerifySignInCode()
    {
        String code= codetxt.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(),"Login Successfully",Toast.LENGTH_LONG).show();
                            String phoneNum=phonetxt.getText().toString();
                            Intent StartIntent=new Intent(PhoneEntry.this,SettingsActivity.class);
                            StartIntent.putExtra("Phone Number",phoneNum);
                            StartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(StartIntent);
                            finish();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(),"The verification code is not correct ",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            final Button btn=(Button) findViewById(R.id.btn_get_verfication);
            super.onCodeSent(s, forceResendingToken);
            codeSent=s;
            timer.cancel();
            btn.setText("Done!");

        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }
    };

    public void sendVerficationCode() {

        String phone =phonetxt.getText().toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+2"+ phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

        if(phone.isEmpty())
        {
            phonetxt.setError("Phone number is required");
            phonetxt.requestFocus();
            return;
        }
        if(phone.length()<10 && !phone.isEmpty() )
        {
            phonetxt.setError("Please Enter a Valid Phone");
            phonetxt.requestFocus();
            return;

        }
    }
}