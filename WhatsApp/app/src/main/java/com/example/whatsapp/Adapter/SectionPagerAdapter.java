package com.example.whatsapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.whatsapp.ChatsFragment;
import com.example.whatsapp.FreindsFragment;
import com.example.whatsapp.GroupChatFragment;

public class SectionPagerAdapter extends FragmentPagerAdapter {


    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @NonNull
    @Override

    public Fragment getItem(int position) {

        switch (position)
        {
            case 0:
                ChatsFragment chatsFragment=new ChatsFragment();
                return chatsFragment;
            case 1:
                FreindsFragment freindsFragment=new FreindsFragment();
                return freindsFragment;
            case 2:
                GroupChatFragment requistsfregment=new GroupChatFragment();
                return requistsfregment;
            default:
                return null;
        }

    }


    public CharSequence getPageTitle(int position)
    {
        switch (position )
        {
            case 0:
                return "CHATS";
            case 1:
                return "Friends";

            case 2:
                return "Group Chats";
            default:
                return null;


        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}


