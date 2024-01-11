package com.example.ft_hangouts;

import android.app.Activity;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ft_hangouts.databinding.FragmentMessageBinding;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment
{
    private FragmentMessageBinding binding;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "id";

    private long id = -1;
    private boolean imageIsLoad = false;
    private Bitmap image;
    private Contact contact;

    private ComponentName component = null;
    private SmsReceiver smsReceiver = null;
    private IntentFilter filter = null;

    public MessageFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @return A new instance of fragment MessageFragment.
     */
    public static MessageFragment newInstance(long id, String phone_number, String firstname, String lastname, String image)
    {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
            this.id = getArguments().getLong(ARG_PARAM1);
    }

    @Override
    public void onResume()
    {
        if(this.component != null && getContext().getPackageManager().getComponentEnabledSetting(component) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
            getContext().getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        if(this.smsReceiver != null && this.filter != null)
            getActivity().registerReceiver(this.smsReceiver, this.filter);

        refreshMessages();

        super.onResume();
    }

    @Override
    public void onPause()
    {
        if(this.component != null && getContext().getPackageManager().getComponentEnabledSetting(component) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
            getContext().getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        if(this.smsReceiver != null)
            getActivity().unregisterReceiver(this.smsReceiver);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentMessageBinding.inflate(inflater, container, false);

        if(this.id < 0)
        {
            getActivity().finish();
            return (binding.getRoot());
        }

        DBHelper db = new DBHelper(getContext());
        this.contact = db.getContact(this.id);

        if(this.contact.getFirstname().isEmpty() && this.contact.getLastname().isEmpty())
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(this.contact.getPhoneNumber());
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
            {
                this.imageIsLoad = true;
                this.image = bitmap;
                setImageToActionBar(getActivity(), bitmap);
            }
        }

        this.binding.btnSendMessage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String text = binding.inputMessageText.getText().toString().trim();
                if(text.isEmpty())
                {
                    Toast.makeText(getContext(), R.string.message_empty, Toast.LENGTH_LONG).show();
                    return;
                }

                contact.sendMessage(getActivity(), getContext(), text);
                binding.inputMessageText.setText("");

                refreshMessages();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(true);

        binding.recyclerView.setLayoutManager(layoutManager);

        refreshMessages();

        if(binding.recyclerView != null)
        {

            this.smsReceiver = new SmsReceiver(binding.recyclerView);
            this.filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            getActivity().registerReceiver(this.smsReceiver, filter);

            this.component = new ComponentName(getContext(), SmsReceiver.class);
            if(this.component != null && getContext().getPackageManager().getComponentEnabledSetting(component) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
                getContext().getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }

        return (binding.getRoot());
    }

    public void refreshMessages()
    {
        ArrayList<Message> messages = this.contact.getMessages(getContext());

        binding.recyclerView.setAdapter(new MessageAdapter(messages, this.contact.getPhoneNumber()));
    }

    private void setImageToActionBar(Activity activity, Bitmap bitmap)
    {
        android.support.v7.app.ActionBar actionBar = ((MainActivity) activity).getSupportActionBar();
        if(actionBar == null)
            return;

        actionBar.setIcon(new BitmapDrawable(getResources(), bitmap));
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if(this.imageIsLoad == true)
            setImageToActionBar(getActivity(), this.image);
    }

    @Override
    public void onStop()
    {
        super.onStop();

        android.support.v7.app.ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if(actionBar == null)
            return;

        actionBar.setIcon(new BitmapDrawable(getResources(), (Bitmap) null));
    }
}