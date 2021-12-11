package aidooo.spydo.com.chitchat.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import aidooo.spydo.com.chitchat.Fragments.ChatFragment;
import aidooo.spydo.com.chitchat.Fragments.ContactsFragment;
import aidooo.spydo.com.chitchat.Fragments.GroupsFragment;

public class TabsAccessorsAdapter extends FragmentPagerAdapter {


    public TabsAccessorsAdapter(@NonNull FragmentManager fm,int behavior) {
        super(fm,behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){

            case 0 :
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 1 :
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            case 2 :
                ContactsFragment contactsFragment= new ContactsFragment();
                return contactsFragment;

            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){

            case 0 :
                return "Chats";
            case 1 :
                return "Groups";
            case 2 :
                return "Contacts";

            default:
                return null;

        }
    }
}
