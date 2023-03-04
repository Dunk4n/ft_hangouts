package com.example.ft_hangouts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper
{
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss ZZZ";
    private static final String DATE_FORMAT_CONTACT = "dd/MM/yyyy";

    public static final String DATABASE_NAME = "Ft_hangouts_DBName.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_PHONE_NUMBER = "phone_number";
    public static final String CONTACTS_COLUMN_FIRSTNAME = "firstname";
    public static final String CONTACTS_COLUMN_LASTNAME = "lastname";
    public static final String CONTACTS_COLUMN_EMAIL = "email";
    public static final String CONTACTS_COLUMN_ADDRESS = "address";
    public static final String CONTACTS_COLUMN_BIRTHDAY = "birthday";
    public static final String CONTACTS_COLUMN_NOTES = "notes";
    public static final String CONTACTS_COLUMN_IMAGE = "image";

    public static final String MESSAGES_TABLE_NAME = "messages";
    public static final String MESSAGES_COLUMN_ID = "id";
    public static final String MESSAGES_COLUMN_IO = "io";
    public static final String MESSAGES_COLUMN_PHONE_NUMBER = "phone_number";
    public static final String MESSAGES_COLUMN_TEXT = "text";
    public static final String MESSAGES_COLUMN_DATE = "date";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + CONTACTS_TABLE_NAME + " (" +
                CONTACTS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CONTACTS_COLUMN_PHONE_NUMBER + " TEXT UNIQUE NOT NULL, " +
                CONTACTS_COLUMN_FIRSTNAME + " TEXT NOT NULL, " +
                CONTACTS_COLUMN_LASTNAME + " TEXT NOT NULL, " +
                CONTACTS_COLUMN_EMAIL + " TEXT NOT NULL, " +
                CONTACTS_COLUMN_ADDRESS + " TEXT NOT NULL, " +
                CONTACTS_COLUMN_BIRTHDAY + " TEXT NOT NULL, " +
                CONTACTS_COLUMN_NOTES + " TEXT NOT NULL, " +
                CONTACTS_COLUMN_IMAGE + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + MESSAGES_TABLE_NAME + " (" +
                MESSAGES_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MESSAGES_COLUMN_IO + " SHORT, " +
                MESSAGES_COLUMN_PHONE_NUMBER + " TEXT NOT NULL, " +
                MESSAGES_COLUMN_TEXT + " TEXT NOT NULL, " +
                MESSAGES_COLUMN_DATE + " DATE NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE_NAME);
        onCreate(db);
    }

    public long insertContact(String phone_number, String firstname, String lastname, String email, String address, Date birthday, String notes, String image)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CONTACTS_COLUMN_PHONE_NUMBER, phone_number);
        contentValues.put(CONTACTS_COLUMN_FIRSTNAME, firstname);
        contentValues.put(CONTACTS_COLUMN_LASTNAME, lastname);
        contentValues.put(CONTACTS_COLUMN_EMAIL, email);
        contentValues.put(CONTACTS_COLUMN_ADDRESS, address);
        contentValues.put(CONTACTS_COLUMN_BIRTHDAY, dateToString(birthday));
        contentValues.put(CONTACTS_COLUMN_NOTES, notes);
        contentValues.put(CONTACTS_COLUMN_IMAGE, image);

        return (db.insert(CONTACTS_TABLE_NAME, null, contentValues));
    }

    public long insertMessage(boolean io, String phone_number, String text, Date date)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MESSAGES_COLUMN_IO, (short) (io ? 1 : 0));
        contentValues.put(MESSAGES_COLUMN_PHONE_NUMBER, phone_number);
        contentValues.put(MESSAGES_COLUMN_TEXT, text);
        contentValues.put(MESSAGES_COLUMN_DATE, dateToString(date));

        return (db.insert(MESSAGES_TABLE_NAME, null, contentValues));
    }

    public Contact getContact(long id)
    {
        if(id < 0)
            return (new Contact());
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + CONTACTS_TABLE_NAME + " WHERE id=?", new String[] {String.valueOf(id)});
        if(res.moveToFirst() == false)
            return (new Contact());
        return (new Contact(res));
    }

    public Message getMessage(long id)
    {
        if(id < 0)
            return (new Message());
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + MESSAGES_TABLE_NAME + " WHERE id=?", new String[] {String.valueOf(id)});
        if(res.moveToFirst() == false)
            return (new Message());
        return (new Message(res));
    }

    public long getNumberOfContacts()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return (DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME));
    }

    public long getNumberOfMessages()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return (DatabaseUtils.queryNumEntries(db, MESSAGES_TABLE_NAME));
    }

    public boolean updateContact(long id, String phone_number, String firstname, String lastname, String email, String address, Date birthday, String notes, String image)
    {
        if(id < 0)
            return (false);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CONTACTS_COLUMN_PHONE_NUMBER, phone_number);
        contentValues.put(CONTACTS_COLUMN_FIRSTNAME, firstname);
        contentValues.put(CONTACTS_COLUMN_LASTNAME, lastname);
        contentValues.put(CONTACTS_COLUMN_EMAIL, email);
        contentValues.put(CONTACTS_COLUMN_ADDRESS, address);
        contentValues.put(CONTACTS_COLUMN_BIRTHDAY, dateToString(birthday));
        contentValues.put(CONTACTS_COLUMN_NOTES, notes);
        contentValues.put(CONTACTS_COLUMN_IMAGE, image);

        if(db.update(CONTACTS_TABLE_NAME, contentValues, "id = ?", new String[] { String.valueOf(id) } ) == 0)
            return (false);
        return (true);
    }

    public boolean updateMessage(long id, boolean io, String phone_number, String text, Date date)
    {
        if(id < 0)
            return (false);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MESSAGES_COLUMN_IO, (short) (io ? 1 : 0));
        contentValues.put(MESSAGES_COLUMN_PHONE_NUMBER, phone_number);
        contentValues.put(MESSAGES_COLUMN_TEXT, text);
        contentValues.put(MESSAGES_COLUMN_DATE, dateToString(date));

        if(db.update(MESSAGES_TABLE_NAME, contentValues, "id = ?", new String[] { String.valueOf(id) } ) == 0)
            return (false);
        return (true);
    }

    public boolean updateContact(Contact contact)
    {
        if(contact.getId() < 0)
            return (false);
        return (updateContact(contact.getId(), contact.getPhoneNumber(), contact.getFirstname(), contact.getLastname(), contact.getEmail(), contact.getAddress(), contact.getBirthday(), contact.getNotes(), contact.getImage()));
    }

    public int deleteContact(long id)
    {
        if(id < 0)
            return (0);
        SQLiteDatabase db = this.getWritableDatabase();
        int ret = db.delete(CONTACTS_TABLE_NAME, "id = ?", new String[] { String.valueOf(id) });
        db.close();
        return (ret);
    }

    public int deleteMessage(long id)
    {
        if(id < 0)
            return (0);
        SQLiteDatabase db = this.getWritableDatabase();
        int ret = db.delete(MESSAGES_TABLE_NAME, "id = ?", new String[] { String.valueOf(id) });
        db.close();
        return (ret);
    }

    public int deleteMessagesWithPhoneNumber(String phone_number)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int ret = db.delete(MESSAGES_TABLE_NAME, MESSAGES_COLUMN_PHONE_NUMBER + " = ?", new String[] { phone_number });
        db.close();
        return (ret);
    }

    public ArrayList<Contact> getAllContacts()
    {
        ArrayList<Contact> array_list = new ArrayList<Contact>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + CONTACTS_TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false)
        {
            array_list.add(new Contact(res));
            res.moveToNext();
        }
        res.close();
        return (array_list);
    }

    public ArrayList<Message> getAllMessages()
    {
        ArrayList<Message> array_list = new ArrayList<Message>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + MESSAGES_TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false)
        {
            array_list.add(new Message(res));
            res.moveToNext();
        }
        res.close();
        return (array_list);
    }

    public ArrayList<Message> getAllMessagesFromPhoneNumber(String phone_number)
    {
        ArrayList<Message> array_list = new ArrayList<Message>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + MESSAGES_TABLE_NAME + " WHERE " + MESSAGES_COLUMN_PHONE_NUMBER + "=?", new String[] { phone_number });
        res.moveToFirst();

        while(res.isAfterLast() == false)
        {
            array_list.add(new Message(res));
            res.moveToNext();
        }
        res.close();
        return (array_list);
    }

    public static Date stringToDate(String date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        try
        {
            return (dateFormat.parse(date));
        }
        catch(ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String dateToString(Date date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        return (dateFormat.format(date));
    }

    public static String dateToStringContact(Date date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_CONTACT);

        return (dateFormat.format(date));
    }

    public static Date stringContactToDate(String date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_CONTACT);

        try
        {
            return (dateFormat.parse(date));
        }
        catch(ParseException e)
        {
            throw new RuntimeException(e);
        }
    }
}