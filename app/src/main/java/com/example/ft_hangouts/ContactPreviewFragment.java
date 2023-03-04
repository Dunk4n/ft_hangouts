package com.example.ft_hangouts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.navigation.fragment.NavHostFragment;

import com.example.ft_hangouts.databinding.FragmentContactPreviewBinding;

public class ContactPreviewFragment extends Fragment
{
    private FragmentContactPreviewBinding binding;

    private Contact contact;

    private static final int CALL_REQUESTCODE = 0;

    public ContactPreviewFragment()
    {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ContactPreviewFragment(Contact contact)
    {
        this.contact = contact;
    }

    public static ContactPreviewFragment newInstance()
    {
        ContactPreviewFragment fragment = new ContactPreviewFragment();
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
        binding = FragmentContactPreviewBinding.inflate(inflater, container, false);

        if(this.contact != null)
        {
            if(this.contact.getFirstname().isEmpty() == false)
                binding.txtContact.setText(this.contact.getFirstname());
            else if(this.contact.getLastname().isEmpty() == false)
                binding.txtContact.setText(this.contact.getLastname());
            else
                binding.txtContact.setText(this.contact.getPhoneNumber());

            if(!this.contact.getImage().isEmpty())
            {
                Bitmap bitmap = this.contact.getImageBitmap(getContext(), getActivity());
                if(bitmap == null)
                    bitmap = this.contact.getImageBitmap(getContext(), getActivity());
                if(bitmap != null)
                {
                    binding.imgAvatar.setImageBitmap(bitmap);
                    binding.imgAvatar.setForeground(null);
                }
            }
        }

        binding.contact.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                bundle.putLong("id", contact.getId());
                NavHostFragment.findNavController(ContactPreviewFragment.this).navigate(R.id.action_FirstFragment_to_contactDisplay, bundle);
            }
        });

        binding.btnSms.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                bundle.putLong("id", contact.getId());
                NavHostFragment.findNavController(ContactPreviewFragment.this).navigate(R.id.action_FirstFragment_to_messageFragment, bundle);
            }
        });

        binding.btnCall.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                phoneCall();
            }
        });

                // Inflate the layout for this fragment
        return binding.getRoot();
    }

    public void phoneCall()
    {
        if(contact == null)
            return;
        if(contact.getPhoneNumber().isEmpty())
        {
            Toast.makeText(getContext(), "Phone number is empty", Toast.LENGTH_LONG).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
        {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.getPhoneNumber()));
            startActivity(intent);
        }
        else
        {
            requestPermissions(new String[] { android.Manifest.permission.CALL_PHONE }, CALL_REQUESTCODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(requestCode == CALL_REQUESTCODE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            phoneCall();
        else
            Toast.makeText(getContext(), "Missing call permission", Toast.LENGTH_LONG).show();
    }
}