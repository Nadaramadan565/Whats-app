package com.example.whatsapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Model_message_Chat;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdapterGroupMessages extends RecyclerView.Adapter<AdapterGroupMessages.HolderGroupMessages>
{
    private static  final int MSG_TYPE_LEFT=0;
    private static  final int MSG_TYPE_RIGHT=1;
    private Context context;
    private ArrayList<Model_message_Chat>modelmessagechats;
    private FirebaseAuth firebaseAuth;

    public AdapterGroupMessages(Context context, ArrayList<Model_message_Chat> modelmessagechats) {
        this.context = context;
        this.modelmessagechats = modelmessagechats;
        firebaseAuth=FirebaseAuth.getInstance();

    }

    @NonNull
    @Override
    public HolderGroupMessages onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layouts
        if(viewType==MSG_TYPE_RIGHT)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.roe_groupchat_right,parent,false);
            return new HolderGroupMessages(view);
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.row_groupchat_left,parent,false);
            return new HolderGroupMessages(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupMessages holder, int position)
    {
        //get data
        Model_message_Chat model=modelmessagechats.get(position);
        String message=model.getMessage();
        String timestamp=model.getTimestamp();
        String senderUid=model.getSender();

        // convert time stamp to dd/mm/yyyy
        Calendar cal =Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        String datetime=format.format(cal.getTime());

        //set data
        holder.messageTv.setText(message);
        holder.timeTv.setText(datetime);
        setUserName(model,holder);

    }


    private void setUserName(Model_message_Chat model, final HolderGroupMessages holder)
    {
        //get sender info from uid
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(model.getSender())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren())
                        {
                            String name=""+ds.child("name").getValue();
                            holder.nameTv.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return modelmessagechats.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if(modelmessagechats.get(position).getSender().equals(firebaseAuth.getUid()))
        {
            return MSG_TYPE_RIGHT;

        }
        else
        {
            return MSG_TYPE_LEFT;

        }
    }

    class HolderGroupMessages extends RecyclerView.ViewHolder {
        private TextView nameTv,timeTv,messageTv;

        public HolderGroupMessages(@NonNull View itemView) {
            super(itemView);
            nameTv=itemView.findViewById(R.id.nameTv);
            messageTv=itemView.findViewById(R.id.messageTv);
            timeTv=itemView.findViewById(R.id.timeTv);


        }
    }
}