package com.example.ft_hangouts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ContactsPreviewFragment extends Fragment
{
    ArrayList<Contact> contacts;
    ArrayList<Fragment> fragments;

    public ContactsPreviewFragment()
    {
        // Required empty public constructor
    }

    public ContactsPreviewFragment(ArrayList<Contact> contacts)
    {
        this.contacts = contacts;
    }

    public static ContactsPreviewFragment newInstance()
    {
        ContactsPreviewFragment fragment = new ContactsPreviewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.fragments = new ArrayList<Fragment>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        loadContacts();
        return inflater.inflate(R.layout.fragment_contacts_preview, container, false);
    }

    @Override
    public void onPause()
    {
        FragmentManager fm = getFragmentManager();
        if(fm == null)
            return;

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if(fragmentTransaction == null)
            return;

        int cnt = 0;
        while(cnt < this.fragments.size())
        {
            fragmentTransaction.remove(this.fragments.get(cnt));
            cnt++;
        }
        fragmentTransaction.commit();

        super.onPause();
    }


    public void loadContacts()
    {
        if(contacts == null || contacts.isEmpty() == true || contacts.size() == 0)
            return;

        FragmentManager fm = getFragmentManager();
        if(fm == null)
            return;

        if(fm.findFragmentById(R.id.LinearLayout1) != null)
            return;

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if(fragmentTransaction == null)
            return;

        Log.d("AAAA", "Contacts =======");
        ContactPreviewFragment contactPreviewFragment;

        int cnt = 0;
        while(cnt < contacts.size())
        {
            contactPreviewFragment = new ContactPreviewFragment(contacts.get(cnt));
            this.fragments.add(contactPreviewFragment);
            fragmentTransaction.add(R.id.LinearLayout1, contactPreviewFragment);
            cnt++;
        }
        fragmentTransaction.commit();
    }
}