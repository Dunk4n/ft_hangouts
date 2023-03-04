package com.example.ft_hangouts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ContactsPreviewFragment extends Fragment
{
    ArrayList<Contact> contacts;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        loadContacts();
        return inflater.inflate(R.layout.fragment_contacts_preview, container, false);
    }

    public void loadContacts()
    {
        if(contacts == null || contacts.isEmpty() == true || contacts.size() == 0)
            return;

        FragmentManager fm = getFragmentManager();
        if(fm == null)
            return;

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if(fragmentTransaction == null)
            return;

        ContactPreviewFragment contactPreviewFragment;

        int cnt = 0;
        while(cnt < contacts.size())
        {
            contactPreviewFragment = new ContactPreviewFragment(contacts.get(cnt));
            fragmentTransaction.add(R.id.LinearLayout1, contactPreviewFragment, "CONTACT_PREVIEW");
            cnt++;
        }
        fragmentTransaction.commit();
    }
}