package eu.alfred.meetingapp;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper  {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "meetings.db";
    public static final String TABLE_MEETINGS = "meetings";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SUBJECT = "subject";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_CONTACTS = "invitedContacts";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_MEETINGS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SUBJECT + " TEXT, " +
                COLUMN_DATE + " BIGINT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_CONTACTS + " TEXT " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEETINGS);
        onCreate(db);
    }

    // Add a meeting in the database
    public void addMeeting(Meeting meeting) {

        Gson gson = new GsonBuilder().create();
        //Type listOfContactObject = new TypeToken<List<Contact>>(){}.getType();
        //Type type = new TypeToken<List<Contact>>() {}.getType();

        String contacts = gson.toJson(meeting.getInvitedContacts());

        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBJECT, meeting.getSubject());
        values.put(COLUMN_DATE, meeting.getDate().getTime());
        values.put(COLUMN_LOCATION, meeting.getLocation());
        values.put(COLUMN_CONTACTS, contacts);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_MEETINGS, null, values);
        db.close();

    }


    public List<Meeting> getDBMeetings() {

        List<Meeting> meetings = new ArrayList<Meeting>();
        Type type = new TypeToken<List<Contact>>() {
        }.getType();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MEETINGS;

        // Create a cursor and move it to the first row in the results
        Cursor c = db.rawQuery(query, null);
        //c.moveToFirst();

        if (c.moveToFirst()) {
            do {
                String subject = c.getString(c.getColumnIndex(COLUMN_SUBJECT));
                Date date = new Date(c.getLong(c.getColumnIndex(COLUMN_DATE)) * 1000);
                String location = c.getString(c.getColumnIndex(COLUMN_LOCATION));
                String contacts = c.getString(c.getColumnIndex(COLUMN_CONTACTS));

                Log.d("Contact: ", subject + " " + date.toString() + " " + location + " " + contacts);

                Gson gson = new Gson();
                List<Contact> invitedContacts = gson.fromJson(contacts, type);
                Meeting meeting = new Meeting(subject, date, location, invitedContacts);
                Log.d("Meeting created:", meeting.toString());
                meetings.add(meeting);

            } while (c.moveToNext());
        }

        db.close();
        return meetings;

    }

}
