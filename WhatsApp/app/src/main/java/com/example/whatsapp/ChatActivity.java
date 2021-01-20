package com.example.whatsapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Adapter.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

//import android.widget.Toolbar;
public class ChatActivity extends AppCompatActivity {
    //views from xml
/* Toolbar toolbar ;
 RecyclerView recyclerView;
 ImageView profile_image;
 TextView nameTv,userstatusTv;
 EditText messageEt;
 ImageButton sendBtn;
    FirebaseAuth mAuth;
    String hisUid ;
    String muUid ;*/
    private String mChatUser ;
    private Toolbar mChatToolbar;
    private DatabaseReference mRootRef;
    private TextView mTitleView;
    private TextView mLastSeenView ;
    private CircleImageView mProfileImage;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;
    private RecyclerView mMessagesList ;
    private final List<Message> MessagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout ;
    private MessageAdapter mAdapter ;
    private DatabaseReference mMessageDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //init views
        mChatToolbar =(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mChatToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mChatUser = getIntent().getStringExtra("user_id");
        String userName = getIntent().getStringExtra("user_name");
        // getSupportActionBar().setTitle(userName);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar_view);
        mTitleView = (TextView)findViewById(R.id.custom_bar_title);
        mLastSeenView = (TextView)findViewById(R.id.custom_bar_seen);
        mProfileImage = (CircleImageView)findViewById(R.id.custom_bar_profile);
       // mChatSendBtn = (ImageButton)findViewById(R.id.chat_send_btn);
        //mChatMessageView = (EditText)findViewById(R.id.chat_message_view);
        mAdapter = new MessageAdapter(MessagesList);
        mMessagesList = (RecyclerView) findViewById(R.id.messages_List);
        mLinearLayout = new LinearLayoutManager(this);
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);
    //    mMessagesList.setAdapter(mAdapter);
        LoadMessages();

        mTitleView.setText(userName);
        mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String online = snapshot.child("online").toString();
                String image = snapshot.child("image").toString();
                if (online.equals("true")){
                    mLastSeenView.setText("Online");
                }
                else {
                    gettimeAgo gettime = new gettimeAgo();
                    long LastTime = Long.parseLong(online);
                //    String LastSeenTime = gettime.gettimeAgo(LastTime,getApplicationContext());
                  //  mLastSeenView.setText(LastSeenTime);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        mRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(mChatUser)) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);
                    Map chatUserMap = new HashMap();
                    chatAddMap.put("chat/" + mCurrentUserId + "/" + mChatUser, chatAddMap);
                    chatAddMap.put("chat/" + mChatUser + "/" + mCurrentUserId, chatAddMap);
                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null) {
                                Log.d("CHAT_LOG", error.getMessage().toString());

                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();

            }

            private void sendMessage() {
                String message = mChatMessageView.getText().toString();
                if (!TextUtils.isEmpty(message)){
                    String current_user_ref = "messages/" + mCurrentUserId + "/" +mChatUser;
                    String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;
                    DatabaseReference user_message_push = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser).push();
                    String push_id = user_message_push.getKey();
                    Map messageMap = new HashMap();
                    messageMap.put( "message" , message );
                    messageMap.put("seen"  , false);
                    messageMap.put("type"  , "text");
                    messageMap.put("time" , ServerValue.TIMESTAMP);
                    messageMap.put("from" , mCurrentUserId);
                    Map messageUserMap = new HashMap();
                    messageMap.put(current_user_ref + "/" + push_id , messageMap);
                    messageUserMap.put(chat_user_ref + "/" + push_id , messageMap);
                    mChatMessageView.setText("");
                    mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null) {
                                Log.d("CHAT_LOG", error.getMessage().toString());

                            }
                        }
                    });
                }
            }
        });
        // Intent Chatintent = new Intent(ChatActivity.this,SettingsActivity.class);
        //Chatintent .putExtra("online" , "false");
    }

    private void LoadMessages() {
        mRootRef.child("messages").child(mCurrentUserId).child(mChatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                MessagesList.add(message);
              //  mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // MenuInflater inflater = getMenuInflater().inflate(R.menu.menu,menu);
//menu.findItem(R.id.ac)
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
                return super.onOptionsItemSelected(item);

    }*/
}
