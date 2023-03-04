package com.example.ft_hangouts;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.example.ft_hangouts.databinding.FragmentContactDisplayBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link contactDisplay#newInstance} factory method to
 * create an instance of this fragment.
 */
public class contactDisplay extends Fragment
{
    private FragmentContactDisplayBinding binding;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "id";

    private static final int CALL_REQUESTCODE = 0;

    private long id = -1;
    private Contact contact;

    public contactDisplay()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id_value Parameter 1.
     * @return A new instance of fragment contactDisplay.
     */
    public static contactDisplay newInstance(long id_value)
    {
        contactDisplay fragment = new contactDisplay();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, id_value);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
        {
            this.id = getArguments().getLong(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentContactDisplayBinding.inflate(inflater, container, false);

        if(this.id < 0)
        {
            NavHostFragment.findNavController(this).navigate(R.id.action_contactDisplay_to_FirstFragment);
            return (binding.getRoot());
        }

        DBHelper db = new DBHelper(getContext());
        this.contact = db.getContact(this.id);
        db.close();

        binding.btnEditContact.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                bundle.putLong("id", id);
                NavHostFragment.findNavController(contactDisplay.this).navigate(R.id.action_contactDisplay_to_editContactFragment, bundle);
            }
        });

        binding.btnDeleteContact.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle(getString(R.string.warning_contact_delete_title));
                builder.setMessage(getString(R.string.warning_contact_delete));
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(contact.deletePreviousImage(getContext(), getActivity()) == false)
                            contact.deletePreviousImage(getContext(), getActivity());

                        DBHelper db = new DBHelper(getContext());
                        db.deleteMessagesWithPhoneNumber(contact.getPhoneNumber());
                        if(db.deleteContact(id) == 0)
                        {
                            Toast.makeText(getContext(), getString(R.string.can_not_delete_contact), Toast.LENGTH_LONG).show();
                            db.close();
                            return;
                        }
                        db.close();
                        NavHostFragment.findNavController(contactDisplay.this).navigate(R.id.action_contactDisplay_to_FirstFragment);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        if(this.contact.getFirstname().isEmpty() && this.contact.getLastname().isEmpty())
        {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(this.contact.getPhoneNumber());
        }
        else
        {
            if(this.contact.getFirstname().isEmpty())
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(this.contact.getLastname());
            else if(this.contact.getLastname().isEmpty())
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(this.contact.getFirstname());
            else
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(this.contact.getFirstname() + " " + this.contact.getLastname());
        }

        binding.btnCall.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                phoneCall();
            }
        });

        binding.btnStartMessage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                bundle.putLong("id", id);
                NavHostFragment.findNavController(contactDisplay.this).navigate(R.id.action_contactDisplay_to_messageFragment, bundle);
            }
        });

        if(this.contact.getImage().isEmpty())
            binding.contactImage.setVisibility(View.GONE);
        else
        {
            Bitmap bitmap = this.contact.getImageBitmap(getContext(), getActivity());
            if(bitmap == null)
                bitmap = this.contact.getImageBitmap(getContext(), getActivity());
            if(bitmap != null)
                binding.contactImage.setImageBitmap(bitmap);
        }

        if(this.contact.getFirstname().isEmpty())
        {
            binding.contactFirstnameLabel.setVisibility(View.GONE);
            binding.contactFirstname.setVisibility(View.GONE);
        }
        else
            binding.contactFirstname.setText(this.contact.getFirstname());

        if(this.contact.getLastname().isEmpty())
        {
            binding.contactLastname.setVisibility(View.GONE);
            binding.contactLastnameLabel.setVisibility(View.GONE);
        }
        else
            binding.contactLastname.setText(this.contact.getLastname());

        if(this.contact.getPhoneNumber().isEmpty())
            binding.contactPhoneNumber.setText("NA");
        else
            binding.contactPhoneNumber.setText(this.contact.getPhoneNumber());

        if(this.contact.getEmail().isEmpty())
        {
            binding.contactEmail.setVisibility(View.GONE);
            binding.contactEmailLabel.setVisibility(View.GONE);
        }
        else
            binding.contactEmail.setText(this.contact.getEmail());

        if(this.contact.getAddress().isEmpty())
        {
            binding.contactAddress.setVisibility(View.GONE);
            binding.contactAddressLabel.setVisibility(View.GONE);
        }
        else
            binding.contactAddress.setText(this.contact.getAddress());

        binding.contactBirthday.setText(DBHelper.dateToStringContact(this.contact.getBirthday()));

        if(this.contact.getNotes().isEmpty())
        {
            binding.contactNotes.setVisibility(View.GONE);
            binding.contactNotesLabel.setVisibility(View.GONE);
        }
        else
            binding.contactNotes.setText(contact.getNotes());

        return (binding.getRoot());
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