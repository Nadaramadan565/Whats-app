package com.example.whatsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.GroupChatActivity;
import com.example.whatsapp.ModelGroupChat;
import com.example.whatsapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterGroupChat extends RecyclerView.Adapter<AdapterGroupChat.HolderGroupChatlist> {
    private Context context;
    private ArrayList<ModelGroupChat> groupChatsLists;

    public AdapterGroupChat(Context context, ArrayList<ModelGroupChat> groupChatsLists) {
        this.context = context;
        this.groupChatsLists = groupChatsLists;
    }

    @NonNull
    @Override
    public HolderGroupChatlist onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view= LayoutInflater.from(context).inflate(R.layout.row_groupchats_list,parent,false);
        return new HolderGroupChatlist(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChatlist holder, int position) {
        //Get data
        //Encapsulatiion
        ModelGroupChat model=groupChatsLists.get(position);
        final String GroupId=model.getGroupId();
        final String GroupIcon=model.getGroupIcon();
        String GroupTitle=model.getGroupTitle();

        holder.groubTitleIv.setText(GroupTitle);


        try
        {
            Picasso.get().load(GroupIcon).placeholder(R.drawable.ic_group_primary).into(holder.groubIconIv);



        }
        catch(Exception e)
        {
             holder.groubIconIv.setImageResource(R.drawable.ic_group_primary);

        }
        //handle Groups Click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Opening Group Chat
                Intent intent=new Intent(context, GroupChatActivity.class);
                intent.putExtra("groupId",GroupId);
                context.startActivity(intent);


            }
        });


    }

    @Override
    public int getItemCount() {
        return groupChatsLists.size();
    }

    //view holder class
    class HolderGroupChatlist extends RecyclerView.ViewHolder
    {
        //Ui views
private ImageView groubIconIv;
private TextView groubTitleIv,nameIv,massageIv,timeIv;

        public HolderGroupChatlist(@NonNull View itemView) {
            super(itemView);
            groubIconIv=itemView.findViewById(R.id.groubIconIv);
            groubTitleIv=itemView.findViewById(R.id.groubTitleIv);
            nameIv=itemView.findViewById(R.id.nameIv);
            massageIv=itemView.findViewById(R.id.massageIv);
            timeIv=itemView.findViewById(R.id.timeIv);



        }
    }
}
