package com.example.ft_hangouts;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsMessage;
import android.util.Log;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class SmsReceiver extends BroadcastReceiver
{
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private RecyclerView recyclerView = null;

    public SmsReceiver()
    {
    }

    public SmsReceiver(RecyclerView recyclerView)
    {
        this.recyclerView = recyclerView;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(!intent.getAction().equals(SMS_RECEIVED))
            return;

        Bundle bundle = intent.getExtras();
        if (bundle == null)
            return;

        // get sms objects
        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus.length == 0)
            return;

        // large message might be broken into many
        SmsMessage[] messages = new SmsMessage[pdus.length];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pdus.length; i++)
        {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], SmsMessage.FORMAT_3GPP);
            if(messages[i] == null)
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], SmsMessage.FORMAT_3GPP2);
            sb.append(messages[i].getMessageBody());
        }
        String sender = messages[0].getOriginatingAddress();
        String message = sb.toString();

        Message new_message = receiveMessage(context, sender, message);
        if(this.recyclerView != null)
        {
            MessageAdapter adapter = (MessageAdapter) this.recyclerView.getAdapter();
            if(adapter == null)
                return;
            adapter.addMessage(new_message);
            adapter.notifyDataSetChanged();

            this.recyclerView.smoothScrollToPosition(adapter.getItemCount());
        }
    }

    public Message receiveMessage(Context context, String phone_number, String message)
    {
        DBHelper db = new DBHelper(context);
        ArrayList<Contact> contacts = db.getAllContacts();
        Message new_message = null;

        int cnt = 0;
        while(cnt < contacts.size())
        {
            if(Objects.equals(phone_number, contacts.get(cnt).getPhoneNumber()))
            {
                long id = db.insertMessage(Message.MESSAGE_IN, contacts.get(cnt).getPhoneNumber(), message, Calendar.getInstance().getTime());
                new_message = db.getMessage(id);
                db.close();
                return (new_message);
            }
            cnt++;
        }

        if(db.insertContact(phone_number, "", "", "", "", Calendar.getInstance().getTime(), "", "") >= 0)
        {
            long id = db.insertMessage(Message.MESSAGE_IN, phone_number, message, Calendar.getInstance().getTime());
            new_message = db.getMessage(id);
        }
        db.close();
        return (new_message);
    }
}