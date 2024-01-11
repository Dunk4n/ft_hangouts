package com.example.ft_hangouts;

import static com.example.ft_hangouts.R.string.missing_phone_number;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.fragment.NavHostFragment;

import com.example.ft_hangouts.databinding.FragmentCreateNewContactBinding;

import java.text.DateFormat;
import java.util.Date;
import java.text.ParseException;

public class CreateNewContactFragment extends Fragment
{
    private FragmentCreateNewContactBinding binding;
    private boolean imageSet = false;
    private static final int RESULT_LOAD_IMAGE = 1;

    public CreateNewContactFragment()
    {
        // Required empty public constructor
    }

    public static CreateNewContactFragment newInstance()
    {
        CreateNewContactFragment fragment = new CreateNewContactFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentCreateNewContactBinding.inflate(inflater, container, false);

        binding.imageSelect.setOnClickListener(view ->
        {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
        });

        binding.btnSaveContact.setOnClickListener(view ->
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
            String phone_number = binding.inputPhoneNumber.getText().toString();
            if (phone_number.charAt(0) == '0')
                phone_number = "+33" + phone_number.substring(1);
            long id = db.insertContact(phone_number, binding.inputFirstname.getText().toString(), binding.inputLastname.getText().toString(), binding.inputEmail.getText().toString(), binding.inputAddress.getText().toString(), DBHelper.stringContactToDate(birthday), binding.inputNotes.getText().toString(), "");
            if(id < 0)
            {
                db.close();
                Snackbar.make(view, "Can not create contact", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return;
            }

            if(imageSet)
            {
                if(binding.imageSelect.getDrawable() == null)
                {
                    imageSet = false;
                }
                else
                {
                    Contact contact = db.getContact(id);
                    db.close();

                    if(contact.getId() >= 0)
                    {
                        if(contact.saveImage(getContext(), getActivity(), ((BitmapDrawable) binding.imageSelect.getDrawable()).getBitmap()) == false)
                        {
                            Snackbar.make(view, "Can not save the image contact", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            return;
                        }
                    }
                }
            }

            NavHostFragment.findNavController(CreateNewContactFragment.this).navigate(R.id.action_createNewContactFragment_to_FirstFragment);
        });

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
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