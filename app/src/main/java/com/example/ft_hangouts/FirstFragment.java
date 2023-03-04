package com.example.ft_hangouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.LinearLayout;

import androidx.navigation.fragment.NavHostFragment;

import com.example.ft_hangouts.databinding.FragmentFirstBinding;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment
{

    private FragmentFirstBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        binding.fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_FirstFragment_to_createNewContactFragment);
            }
        });

        loadContacts();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }

    public void loadContacts()
    {
        FragmentManager fm = getFragmentManager();
        if(fm == null)
            return;

        DBHelper db = new DBHelper(getContext());
        ArrayList<Contact> contacts = db.getAllContacts();
        db.close();
        if(contacts == null || contacts.isEmpty() == true || contacts.size() == 0)
            return;

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if(fragmentTransaction == null)
            return;

        ContactsPreviewFragment contactsPreviewFragment = new ContactsPreviewFragment(contacts);
        fragmentTransaction.replace(R.id.ScrollViewContacts, contactsPreviewFragment, "CONTACTS_PREVIEW");
        fragmentTransaction.commit();
    }
}