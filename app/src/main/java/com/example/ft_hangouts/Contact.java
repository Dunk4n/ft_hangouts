package com.example.ft_hangouts;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Contact
{
    private long id = -1;
    private String phone_number = "";
    private String firstname = "";
    private String lastname = "";
    private String email = "";
    private String address = "";
    private Date birthday;
    private String notes = "";
    private String image = "";

    private static final int RW_EXTERNAL_STORAGE_REQUESTCODE = 1;
    private static final int PERMISSIONS_SEND_SMS_REQUESTCODE = 2;

    public Contact()
    {
    }

    public Contact(String phone_number)
    {
        this.phone_number = phone_number;
    }

    public Contact(String phone_number, String firstname, String lastname, String email, String address, Date birthday, String notes, String image)
    {
        this.phone_number = phone_number;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.address = address;
        this.birthday = birthday;
        this.notes = notes;
        this.image = image;
    }

    public Contact(Cursor contact)
    {
        if(contact.getCount() <= 0)
            return;

        int ret = contact.getColumnIndex("id");
        if(ret < 0)
            return;
        this.id = contact.getLong(ret);

        ret = contact.getColumnIndex("phone_number");
        if(ret < 0)
            return;
        this.phone_number = contact.getString(ret);

        ret = contact.getColumnIndex("firstname");
        if(ret < 0)
            return;
        this.firstname = contact.getString(ret);

        ret = contact.getColumnIndex("lastname");
        if(ret < 0)
            return;
        this.lastname = contact.getString(ret);

        ret = contact.getColumnIndex("email");
        if(ret < 0)
            return;
        this.email = contact.getString(ret);

        ret = contact.getColumnIndex("address");
        if(ret < 0)
            return;
        this.address = contact.getString(ret);

        ret = contact.getColumnIndex("birthday");
        if(ret < 0)
            return;
        this.birthday = DBHelper.stringToDate(contact.getString(ret));

        ret = contact.getColumnIndex("notes");
        if(ret < 0)
            return;
        this.notes = contact.getString(ret);

        ret = contact.getColumnIndex("image");
        if(ret < 0)
            return;
        this.image = contact.getString(ret);
    }

    public long getId()
    {
        return (this.id);
    }

    public void setId(long value)
    {
        this.id = value;
    }

    public String getPhoneNumber()
    {
        return (this.phone_number);
    }

    public void setPhoneNumber(String value)
    {
        this.phone_number = value;
    }

    public String getFirstname()
    {
        return (this.firstname);
    }

    public void setFirstname(String value)
    {
        this.firstname = value;
    }

    public String getLastname()
    {
        return (this.lastname);
    }

    public void setLastname(String value)
    {
        this.lastname = value;
    }

    public String getEmail()
    {
        return (this.email);
    }

    public void setEmail(String value)
    {
        this.email = value;
    }

    public String getAddress()
    {
        return (this.address);
    }

    public void setAddress(String value)
    {
        this.address = value;
    }

    public Date getBirthday()
    {
        return (this.birthday);
    }

    public void setBirthday(Date value)
    {
        this.birthday = value;
    }

    public String getNotes()
    {
        return (this.notes);
    }

    public void setNotes(String value)
    {
        this.notes = value;
    }

    public String getImage()
    {
        return (this.image);
    }

    public void setImage(String value)
    {
        this.image = value;
    }

    public ArrayList<Message> getMessages(Context context)
    {
        DBHelper db = new DBHelper(context);
        ArrayList<Message> messages = db.getAllMessagesFromPhoneNumber(this.phone_number);
        db.close();
        return (messages);
    }

    public boolean saveImage(Context context, Activity activity, Bitmap bitmapImage)
    {
        if(this.image.isEmpty() == false)
        {
            if(deletePreviousImage(context, activity) == false)
            {
                if(deletePreviousImage(context, activity) == false)
                    return (false);
            }
        }

        String image_name = "contact_image_" + this.id + ".png";

        if(saveImageToStorage(context, activity, bitmapImage, image_name) == false)
        {
            if(saveImageToStorage(context, activity, bitmapImage, image_name) == false)
                return (false);
        }

        this.image = image_name;
        DBHelper db = new DBHelper(context);
        if(db.updateContact(this) == false)
        {
            db.close();
            return (false);
        }
        db.close();

        return (true);
    }

    private boolean saveImageToStorage(Context context, Activity activity, Bitmap bitmapImage, String imageName)
    {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RW_EXTERNAL_STORAGE_REQUESTCODE);
            return (false);
        }

        Bitmap resizedBitmapImage = Bitmap.createScaledBitmap(bitmapImage, 120, 120,false);

        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("contactImage", Context.MODE_PRIVATE);

        File mypath = new File(directory, imageName);

        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(mypath);
            resizedBitmapImage.compress(Bitmap.CompressFormat.PNG, 10, fos);
        }
        catch (Exception e)
        {
            try
            {
                fos.close();
            }
            catch (IOException close_e)
            {
                return (false);
            }
            return (false);
        }

        try
        {
            fos.close();
        }
        catch (IOException close_e)
        {
            return (false);
        }

        return (true);
    }

    public boolean deletePreviousImage(Context context, Activity activity)
    {
        if(this.image.isEmpty() == true)
            return (false);

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RW_EXTERNAL_STORAGE_REQUESTCODE);
            return (false);
        }

        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("contactImage", Context.MODE_PRIVATE);

        File file = new File(directory, this.image);

        this.image = "";
        return (file.delete());
    }

    public Bitmap getImageBitmap(Context context, Activity activity)
    {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RW_EXTERNAL_STORAGE_REQUESTCODE);
            return (null);
        }

        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("contactImage", Context.MODE_PRIVATE);

        File file = new File(directory, this.image);
        if(file == null)
            return (null);

        return (BitmapFactory.decodeFile(file.getAbsolutePath()));
    }

    public void sendMessage(Activity activity, Context context, String message)
    {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.SEND_SMS))
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_SEND_SMS_REQUESTCODE);
            else
                Toast.makeText(context, R.string.no_sms_permission, Toast.LENGTH_LONG).show();
        }
        else
        {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(this.phone_number, null, message, null, null);

            DBHelper db = new DBHelper(context);
            db.insertMessage(Message.MESSAGE_OUT, this.phone_number, message, Calendar.getInstance().getTime());
            db.close();
        }
    }
}