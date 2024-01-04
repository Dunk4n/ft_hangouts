package com.example.ft_hangouts;

import static com.example.ft_hangouts.R.string.missing_phone_number;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.fragment.NavHostFragment;

import com.example.ft_hangouts.databinding.FragmentContactDisplayBinding;
import com.example.ft_hangouts.databinding.FragmentEditContactBinding;

import java.text.ParseException;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditContactFragment extends Fragment
{
    private FragmentEditContactBinding binding;
    private boolean imageSet = false;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "id";
    private static final int RESULT_LOAD_IMAGE = 1;

    private long id = -1;

    private Contact contact;

    public EditContactFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id_value Parameter 1.
     * @return A new instance of fragment EditContactFragment.
     */
    public static EditContactFragment newInstance(long id_value)
    {
        EditContactFragment fragment = new EditContactFragment();
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
        binding = FragmentEditContactBinding.inflate(inflater, container, false);

        if(this.id < 0)
        {
            NavHostFragment.findNavController(this).navigate(R.id.action_editContactFragment_to_FirstFragment);
            return (binding.getRoot());
        }

        binding.imageSelect.setOnClickListener(view ->
        {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
        });

        binding.btnSaveContact.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(binding.inputPhoneNumber.getText().toString().isEmpty())
                {
                    Snackbar.make(view, missing_phone_number, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }

                String day;
                day = String.valueOf(binding.inputBirthday.getDayOfMonth());
                if(binding.inputBirthday.getDayOfMonth() < 10)
                    day = "0" + day;

                String month;
                month = String.valueOf(binding.inputBirthday.getMonth() + 1);
                if(binding.inputBirthday.getMonth() + 1 < 10)
                    month = "0" + month;

                String birthday = day + "/" + month + "/" + String.valueOf(binding.inputBirthday.getYear());

                DBHelper db = new DBHelper(getContext());

                if(!contact.getPhoneNumber().equals(binding.inputPhoneNumber.getText().toString()))
                    db.deleteMessagesWithPhoneNumber(contact.getPhoneNumber());

                contact.setPhoneNumber(binding.inputPhoneNumber.getText().toString());
                contact.setFirstname(binding.inputFirstname.getText().toString());
                contact.setLastname(binding.inputLastname.getText().toString());
                contact.setEmail(binding.inputEmail.getText().toString());
                contact.setAddress(binding.inputAddress.getText().toString());
                contact.setBirthday(DBHelper.stringContactToDate(birthday));
                contact.setNotes(binding.inputNotes.getText().toString());
                if(!db.updateContact(contact))
                {
                    Snackbar.make(view, "Can not edit contact", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }

                if(imageSet)
                {
                    if(binding.imageSelect.getDrawable() == null)
                        imageSet  = false;
                    else
                    {
                        if(contact.saveImage(getContext(), getActivity(), ((BitmapDrawable) binding.imageSelect.getDrawable()).getBitmap()) == false)
                        {
                            Snackbar.make(view, "Can not save the image contact", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            return;
                        }
                    }
                }

                NavHostFragment.findNavController(EditContactFragment.this).navigate(R.id.action_editContactFragment_to_FirstFragment);
            }
        });

        DBHelper db = new DBHelper(getContext());
        this.contact = db.getContact(this.id);
        db.close();

        if(contact.getFirstname().isEmpty() && contact.getLastname().isEmpty())
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

        if(!this.contact.getImage().isEmpty())
        {
            Bitmap bitmap = this.contact.getImageBitmap(getContext(), getActivity());
            if(bitmap == null)
                bitmap = this.contact.getImageBitmap(getContext(), getActivity());
            if(bitmap != null)
                binding.imageSelect.setImageBitmap(bitmap);
        }

        if(!this.contact.getFirstname().isEmpty())
            binding.inputFirstname.setText(this.contact.getFirstname());

        if(!this.contact.getLastname().isEmpty())
            binding.inputLastname.setText(this.contact.getLastname());

        if(!this.contact.getPhoneNumber().isEmpty())
            binding.inputPhoneNumber.setText(this.contact.getPhoneNumber());

        if(!this.contact.getEmail().isEmpty())
            binding.inputEmail.setText(this.contact.getEmail());

        if(!this.contact.getAddress().isEmpty())
            binding.inputAddress.setText(this.contact.getAddress());

        String day = (String) DateFormat.format("dd", this.contact.getBirthday());
        String month = (String) DateFormat.format("MM", this.contact.getBirthday());
        String year = (String) DateFormat.format("yyyy", this.contact.getBirthday());

        binding.inputBirthday.updateDate(Integer.parseInt(year),  Integer.parseInt(month) - 1, Integer.parseInt(day));

        if(!this.contact.getNotes().isEmpty())
            binding.inputNotes.setText(this.contact.getNotes());

        return (binding.getRoot());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null)
        {
            Uri uriData = data.getData();
            if(uriData != null)
            {
                binding.imageSelect.setImageURI(uriData);
                this.imageSet = true;
            }
            else
                this.imageSet = false;
        }
    }
}