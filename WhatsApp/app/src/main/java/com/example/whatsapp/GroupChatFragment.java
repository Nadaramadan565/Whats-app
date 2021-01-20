package com.example.whatsapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Adapter.AdapterGroupChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class GroupChatFragment extends Fragment {
private RecyclerView groupRv;
private ArrayList<ModelGroupChat> groupChatlists;
private AdapterGroupChat adapterGroupChat;
private FirebaseAuth firebaseAuth;
    public GroupChatFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_group_chat, container, false);
        groupRv=view.findViewById(R.id.groupsRv);
        firebaseAuth=FirebaseAuth.getInstance();

        loadGroupChatLists();

        return view;
    }

    private void loadGroupChatLists() {
        groupChatlists=new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatlists.clear();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    //if user id exsists n the listr of partcipants then show that group
                    if(ds.child("Participants").child(firebaseAuth.getUid()).exists())
                    {
                        ModelGroupChat model=ds.getValue(ModelGroupChat.class);
                        groupChatlists.add(model);
                    }
                }
                adapterGroupChat=new AdapterGroupChat(getActivity(),groupChatlists);
                groupRv.setAdapter(adapterGroupChat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void searchGroupChatLists(final String query) {
        groupChatlists=new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatlists.clear();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    //if user id exsists n the listr of partcipants then show that group
                    if(ds.child("Participants").child(firebaseAuth.getUid()).exists())
                    {
                        //search by grob name
                        if(ds.child("groupTitle").toString().toLowerCase().contains(query.toLowerCase()))
                        {
                            ModelGroupChat model=ds.getValue(ModelGroupChat.class);
                            groupChatlists.add(model);
                        }

                    }
                }
                adapterGroupChat=new AdapterGroupChat(getActivity(),groupChatlists);
                groupRv.setAdapter(adapterGroupChat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);// to show menu options in fragment
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        //search view

        MenuItem item=menu.findItem(R.id.search_action);
        SearchView searchView = new SearchView(((MainActivity)getActivity()).getSupportActionBar().getThemedContext());
        item.setActionView(searchView);
        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //called when ever usr press search button from keyboard
                //if search query is not empty then search
                if(!TextUtils.isEmpty(query.trim()))
                {
                    searchGroupChatLists(query);
                }
                else
                    {
                        loadGroupChatLists();
                    }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
              // called whenever user press any single letter
                //if search query not empty the search
                if(TextUtils.isEmpty(newText.trim()))
                {
                    searchGroupChatLists(newText);
                }
                else
                {
                    loadGroupChatLists();
                }
                return false;
            }

        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}