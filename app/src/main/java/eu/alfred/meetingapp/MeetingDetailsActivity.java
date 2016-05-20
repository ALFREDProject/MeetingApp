package eu.alfred.meetingapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TimePicker;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MeetingDetailsActivity extends FragmentActivity implements View.OnClickListener {

    private EditText subjectEditText, datePickerEditText, timePickerEditText, locationEditText;
    private Button addContactsButton, inviteContactsButton;
    private Switch calendarSwitch;
    private ListView invitedContactsListView;
    private List<Contact> contacts = new ArrayList<Contact>();
    private List<Contact> contactsToinvite = new ArrayList<Contact>();
    private List<String> contactsToInviteStr = new ArrayList<String>();
    private int mYear, mMonth, mDay, mHour, mMinute;
    private long meetingDate;

    MyDBHandler dbHandler;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_details);

        subjectEditText = (EditText) findViewById(R.id.subjectEditText);
        datePickerEditText = (EditText) findViewById(R.id.dateEditText);
        timePickerEditText = (EditText) findViewById(R.id.timeEditText);
        locationEditText = (EditText) findViewById(R.id.locationEditText);
        addContactsButton = (Button) findViewById(R.id.addContactsButton);
        inviteContactsButton = (Button) findViewById(R.id.inviteContactsButton);
        invitedContactsListView = (ListView) findViewById(R.id.invitedContactsListView);
        calendarSwitch = (Switch) findViewById(R.id.calendarSwitch);

        dbHandler = new MyDBHandler(this, null, null, 1);

        datePickerEditText.setOnClickListener(this);
        timePickerEditText.setOnClickListener(this);
        addContactsButton.setOnClickListener(this);
        inviteContactsButton.setOnClickListener(this);

        Serializable extra = getIntent().getSerializableExtra("Contacts");
        if (extra != null) { contacts = (ArrayList<Contact>) extra; }

        Bundle extras = getIntent().getExtras();
        if (extras == null) { return; }
        if (extras != null) {
            subjectEditText.setText(extras.getString("Subject"));
            locationEditText.setText(extras.getString("Location"));
            String strDate = extras.getString("Day") + " " + extras.getString("Month") + " " + extras.getString("Year");
            DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
            try {
                Date date = (Date) dateFormat.parse(strDate);
                DateFormat df = new SimpleDateFormat("dd.MM.yyy");
                String alfredDate = df.format(date);
                Log.d("Alfred date", date.toString());
                datePickerEditText.setText(alfredDate);
            } catch (ParseException e) { e.printStackTrace(); }
        }

        Serializable invitedContacts = getIntent().getSerializableExtra("InvitedContacts");
        if (invitedContacts != null) { this.contactsToinvite = (ArrayList<Contact>) invitedContacts; }

        if (!contactsToinvite.isEmpty()) {
            for (Contact contact : contactsToinvite) { contactsToInviteStr.add(contact.getName()); }
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, contactsToInviteStr);
        invitedContactsListView.setAdapter(adapter);

        Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        timePickerEditText.setText(String.format("%02d:%02d", mHour, mMinute));

    }


     @Override
     public void onClick(View v) {
         if (v == datePickerEditText) {

             final Calendar c = Calendar.getInstance();
             mYear = c.get(Calendar.YEAR);
             mMonth = c.get(Calendar.MONTH);
             mDay = c.get(Calendar.DAY_OF_MONTH);

             DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                     new DatePickerDialog.OnDateSetListener() {

                         @Override
                         public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                             datePickerEditText.setText(dayOfMonth + "." + (monthOfYear +1) + "." + year);

                         }
                     }, mYear, mMonth, mDay);
             datePickerDialog.show();
         }

         if (v == timePickerEditText) {

             final Calendar c = Calendar.getInstance();
             mHour = c.get(Calendar.HOUR_OF_DAY);
             mMinute = c.get(Calendar.MINUTE);

             TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                     new TimePickerDialog.OnTimeSetListener() {
                         @Override
                         public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                             timePickerEditText.setText(String.format("%02d:%02d", hourOfDay, minute));
                         }
                     }, mHour, mMinute, true);
             timePickerDialog.show();
         }

         if (v == addContactsButton) {

             Intent listContactsIntent = new Intent(this, ListContactsActivity.class);
             //listContactsIntent.putExtra("Contacts", (Serializable) contacts);
             listContactsIntent.putExtra("Source", "meeting");
             startActivityForResult(listContactsIntent, 1);

         }

         if (v == inviteContactsButton) {

             // TODO check if the intived contacts list is empty. Alert if empty.

             long eventDate = getDate(datePickerEditText, timePickerEditText);
             String location = locationEditText.getText().toString();
             String subject = subjectEditText.getText().toString();

             if(calendarSwitch.isChecked()){
                 ContentResolver cr = this.getContentResolver();
                 ContentValues values = new ContentValues();

                 values.put(CalendarContract.Events.DTSTART, eventDate);
                 values.put(CalendarContract.Events.DTEND, eventDate + 3600*1000*2);

                 values.put(CalendarContract.Events.TITLE, subjectEditText.getText().toString());
                 values.put(CalendarContract.Events.DESCRIPTION, getString(R.string.calendar_message));

                 TimeZone timeZone = TimeZone.getDefault();
                 values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

                 values.put(CalendarContract.Events.CALENDAR_ID, 1);
                 values.put(CalendarContract.Events.HAS_ALARM, true);
                 values.put(CalendarContract.Events.EVENT_LOCATION, location);

                 //values.put(CalendarContract.Events.);

                 Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
             }

             Intent returnIntent = new Intent();
             Meeting meeting = new Meeting(subject, eventDate, location, contactsToinvite);
             //sendInvitations(meeting.getSubject(), meeting.getDate(), meeting.getLocation(), meeting.getInvitedContacts());
             dbHandler.addMeeting(meeting);
             setResult(RESULT_OK, returnIntent);
             finish();
         }

     }

    private long getDate(EditText dateEditText, EditText timeEditText) {

        String dateTimeString = dateEditText.getText().toString() + " " + timeEditText.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            Date date = sdf.parse(dateTimeString);
            meetingDate = date.getTime();
        } catch (ParseException e) { e.printStackTrace(); }

        return meetingDate;
    }

    private void sendInvitations(String subject, Date date, String location, List<Contact> contactsToinvite) {
        SmsManager sms = SmsManager.getDefault();
        String message = "Hi! Let's meet. Date: " + date.toString() + " Location: " + location + ". Sent via ALFRED";

        for (Contact contact : contactsToinvite) { sms.sendTextMessage(contact.getPhone(), null, message, null, null); }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == RESULT_OK) {
            Serializable extra = data.getSerializableExtra("InvitedContacts");
            if (extra != null){
                contactsToinvite = (ArrayList<Contact>) extra;
                for (Contact contact : contactsToinvite) { contactsToInviteStr.add(contact.getName()); }
            }

            adapter.notifyDataSetChanged();
        }

    }



}
