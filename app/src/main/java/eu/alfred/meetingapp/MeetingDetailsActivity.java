package eu.alfred.meetingapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.telephony.SmsManager;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MeetingDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText subjectEditText;
    private EditText datePickerEditText;
    private EditText timePickerEditText;
    private EditText locationEditText;
    private Button addContactsButton;
    private Button inviteContactsButton;
    private ListView invitedContactsListView;
    private List<Contact> contacts = new ArrayList<Contact>();
    private List<Contact> contactsToinvite = new ArrayList<Contact>();
    private List<String> contactsToInviteStr = new ArrayList<String>();
    private int mYear, mMonth, mDay, mHour, mMinute;
    MyDBHandler dbHandler;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_details);

        Serializable extra = getIntent().getSerializableExtra("Contacts");
        if (extra != null) { contacts = (ArrayList<Contact>) extra; }

        Serializable invitedContacts = getIntent().getSerializableExtra("InvitedContacts");
        if (invitedContacts != null) { this.contactsToinvite = (ArrayList<Contact>) invitedContacts; }

        subjectEditText = (EditText) findViewById(R.id.subjectEditText);
        datePickerEditText = (EditText) findViewById(R.id.dateEditText);
        timePickerEditText = (EditText) findViewById(R.id.timeEditText);
        locationEditText = (EditText) findViewById(R.id.locationEditText);
        addContactsButton = (Button) findViewById(R.id.addContactsButton);
        inviteContactsButton = (Button) findViewById(R.id.inviteContactsButton);
        invitedContactsListView = (ListView) findViewById(R.id.invitedContactsListView);

        dbHandler = new MyDBHandler(this, null, null, 1);

        datePickerEditText.setOnClickListener(this);
        timePickerEditText.setOnClickListener(this);
        addContactsButton.setOnClickListener(this);
        inviteContactsButton.setOnClickListener(this);

        //Contact testContact1 = new Contact("Deniz Coskun", "00491771708328", "Deniz.Coskun@tiekinetix.com");
        //Contact testContact2 = new Contact("Peter Merz", "00491727759581", "Peter.Merz@tiekinetix.com");
        //Contact testContact3 = new Contact("Robert Lill", "004915209119016", "Robert.Lill@tiekinetix.com");
        //Contact testContact4 = new Contact("Arian Kuschki", "004915222619029", "Arian.Kuschki@tiekinetix.com");

        //contactsToinvite.add(testContact1);
        //contactsToinvite.add(testContact2);
        //contactsToinvite.add(testContact3);
        //contactsToinvite.add(testContact4);

        if (!contactsToinvite.isEmpty()) {
            for (Contact contact : contactsToinvite) { contactsToInviteStr.add(contact.getName()); }
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, contactsToInviteStr);
        invitedContactsListView.setAdapter(adapter);

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
             listContactsIntent.putExtra("Contacts", (Serializable) contacts);
             listContactsIntent.putExtra("Source", "meeting");
             startActivityForResult(listContactsIntent, 1);

         }

         if (v == inviteContactsButton) {

             // TODO check if the intived contacts list is empty. Alert if empty.

             Intent returnIntent = new Intent();
             Meeting meeting = new Meeting(subjectEditText.getText().toString(), editTextToDate(datePickerEditText, timePickerEditText), locationEditText.getText().toString(), contactsToinvite);
             sendInvitations(meeting.getSubject(), meeting.getDate(), meeting.getLocation(), meeting.getInvitedContacts());
             dbHandler.addMeeting(meeting);
             setResult(RESULT_OK, returnIntent);
             finish();
         }

     }

    private Date editTextToDate(EditText dateEditText, EditText timeEditText) {

        String dateTimeString = dateEditText.getText().toString() + " " + timeEditText.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date dateObject = null;
        try {
            dateObject = sdf.parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateObject;

    }

    private void sendInvitations(String subject, Date date, String location, List<Contact> contactsToinvite) {
        SmsManager sms = SmsManager.getDefault();

        String message = "Hi! Let's meet. Date: " + date.toString() + " Location: " + location + ". Sent via ALFRED";

        for (Contact contact : contactsToinvite) {
            sms.sendTextMessage(contact.getPhone(), null, message, null, null);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Serializable extra = data.getSerializableExtra("InvitedContacts");
        if (extra != null){
            contactsToinvite = (ArrayList<Contact>) extra;

            for (Contact contact : contactsToinvite) { contactsToInviteStr.add(contact.getName()); }
        }

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, contactsToInviteStr);
        //invitedContactsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }



}
