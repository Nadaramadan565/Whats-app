package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.Adapter.AdapterGroupMessages;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private String groupId;
    private Toolbar Toolbar;
    private ImageView groupIconIv;
    private TextView groupTitleTv;
    private ImageButton attchbtn,sendBtn;
    private EditText messageEt;
    private RecyclerView chatRv;
    private ArrayList<Model_message_Chat> modelmessagechatslist;
    private AdapterGroupMessages adapterGroupMessages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        Toolbar=findViewById(R.id.Toolbar);
        groupIconIv= (CircleImageView)findViewById(R.id.groupIconIv);
        groupTitleTv=findViewById(R.id.groupTitleTv);
        attchbtn=findViewById(R.id.attchbtn);
        messageEt=findViewById(R.id.messageEt);
        sendBtn=findViewById(R.id.sendBtn);
        chatRv=findViewById(R.id.chatRv);


        //get the id of the group
        Intent intent=getIntent();
        groupId=intent.getStringExtra("groupId");

        firebaseAuth=FirebaseAuth.getInstance();
        loadGroupInfo();
        loadGroupMessages();




        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//input data
                String message=messageEt.getText().toString().trim();
                if(TextUtils.isEmpty(message))
                {
                    //empty ,don't send
                    Toast.makeText(GroupChatActivity.this,"Can't send empty message...",Toast.LENGTH_LONG).show();

                }else
                {
                    //send message
                    SendMessage(message);
                }

            }
        });

    }

    private void loadGroupMessages() {
        //init list
        modelmessagechatslist=new ArrayList<>();
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modelmessagechatslist.clear();
                        for(DataSnapshot ds :snapshot.getChildren())
                        {
                            Model_message_Chat model=ds.getValue(Model_message_Chat.class);
                            modelmessagechatslist.add(model);
                        }
                        //adapter
                        adapterGroupMessages=new AdapterGroupMessages(GroupChatActivity.this,modelmessagechatslist);
//set on recycler view
                        chatRv.setAdapter(adapterGroupMessages);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void SendMessage(final String message)
    {
        //Timestamp
        String timestamp=""+System.currentTimeMillis();
        //setup message data
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender","" + firebaseAuth.getUid());
        hashMap.put("message","" + message );
        hashMap.put("timestamp","" + timestamp );
        hashMap.put("type","" + "text" ); //text,image,file
        //add in dp
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages").child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //message sent
                        //clear messageEt
                        messageEt.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//message sending failed
                        Toast.makeText(GroupChatActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });


    }

    private void loadGroupInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren())
                        {
                            String groupTitle=""+ds.child("groupTitle").getValue();
                            String groupDescription=""+ds.child("groupDescription").getValue();
                            String groupIcon=""+ds.child("groupIcon").getValue();
                            String TimeStamp=""+ds.child("TimeStamp").getValue();
                            String createdBy=""+ds.child("createdBy").getValue();

                            groupTitleTv.setText(groupTitle);
                            try
                            {
                                Picasso.get().load(groupIcon).placeholder(R.drawable.ic_g_white).into(groupIconIv);
                            }
                            catch (Exception e)
                            {
                                groupIconIv.setImageResource(R.drawable.ic_g_white);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}