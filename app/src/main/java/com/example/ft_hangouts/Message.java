package com.example.ft_hangouts;

import android.database.Cursor;

import java.util.Date;

public class Message
{
    public static final boolean MESSAGE_IN = true;
    public static final boolean MESSAGE_OUT = false;

    private long id = -1;
    private boolean io = MESSAGE_IN;
    private String phone_number = "";
    private String text = "";
    private Date date;

    public Message()
    {
    }

    public Message(Cursor message)
    {
        if(message.getCount() <= 0)
            return;

        int ret = message.getColumnIndex("id");
        if(ret < 0)
            return;
        this.id = message.getLong(ret);

        ret = message.getColumnIndex("io");
        if(ret < 0)
            return;
        this.io = message.getShort(ret) > 0;

        ret = message.getColumnIndex("phone_number");
        if(ret < 0)
            return;
        this.phone_number = message.getString(ret);

        ret = message.getColumnIndex("text");
        if(ret < 0)
            return;
        this.text = message.getString(ret);

        ret = message.getColumnIndex("date");
        if(ret < 0)
            return;

        this.date = DBHelper.stringToDate(message.getString(ret));
    }

    Message(boolean io, String phone_number, String text)
    {
        this.io = io;
        this.phone_number = phone_number;
        this.text = text;
    }

    Message(boolean io, String phone_number, String text, Date date)
    {
        this.io = io;
        this.phone_number = phone_number;
        this.text = text;
        this.date = date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public void setDateFromString(String date)
    {
        this.date = DBHelper.stringToDate(date);
    }

    public long getId() { return (this.id); }
    public boolean getIo() { return (this.io); }
    public String getPhoneNumber() { return (this.phone_number); }
    public String getText() { return (this.text); }
    public Date getDate() { return (this.date); }
}
