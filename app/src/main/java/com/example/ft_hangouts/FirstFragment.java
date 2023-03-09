package com.example.ft_hangouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import androidx.navigation.fragment.NavHostFragment;

import com.example.ft_hangouts.databinding.FragmentFirstBinding;

import java.util.ArrayList;

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

    @Override
    public void onPause()
    {
        FragmentManager fm = getFragmentManager();
        if(fm == null)
            return;

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if(fragmentTransaction == null)
            return;

        Fragment frag = fm.findFragmentById(R.id.ScrollViewContacts);
        if(frag != null)
            fragmentTransaction.remove(frag);

        fragmentTransaction.commit();

        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        loadContacts();
    }


    public void loadContacts()
    {
        FragmentManager fm = getFragmentManager();
        if(fm == null)
            return;

        if(fm.findFragmentById(R.id.ScrollViewContacts) != null)
            return;

        DBHelper db = new DBHelper(getContext());
        ArrayList<Contact> contacts = db.getAllContacts();
        db.close();
        if(contacts == null || contacts.isEmpty() == true || contacts.size() <= 0)
            return;

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if(fragmentTransaction == null)
            return;

        fragmentTransaction.replace(R.id.ScrollViewContacts, new ContactsPreviewFragment(contacts));
        fragmentTransaction.commit();
    }
}