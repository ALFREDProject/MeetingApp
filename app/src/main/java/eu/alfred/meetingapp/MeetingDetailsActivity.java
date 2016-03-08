package eu.alfred.meetingapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MeetingDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    //private EditText subjectEditText;
    private EditText datePickerEditText;
    private EditText timePickerEditText;
    private Button addContactsButton;
    private Button inviteContactsButton;
    private List<Contact> contactsToinvite = new ArrayList<Contact>();
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_details);

        //subjectEditText = (EditText) findViewById(R.id.subjectEditText);
        datePickerEditText = (EditText) findViewById(R.id.dateEditText);
        timePickerEditText = (EditText) findViewById(R.id.timeEditText);
        addContactsButton = (Button) findViewById(R.id.addContactsButton);
        inviteContactsButton = (Button) findViewById(R.id.inviteContactsButton);

        datePickerEditText.setOnClickListener(this);
        timePickerEditText.setOnClickListener(this);
        addContactsButton.setOnClickListener(this);
        inviteContactsButton.setOnClickListener(this);

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

             Intent listContactsActivity = new Intent(this, ListContactsActivity.class);
             startActivity(listContactsActivity);

         }

         if (v == inviteContactsButton) {
            if (contactsToinvite.isEmpty()) {

            }

         }


     }



/**
     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     if (resultCode == Activity.RESULT_OK) {
     if (requestCode == CONTACT_PICKER_RESULT) {
     Bundle bundle = data.getExtras();
     String result = bundle.getString("result");
     ArrayList<String> contacts = bundle.getStringArrayList(result);
     Log.i(TAG, "lanunchMultiplePhonePicker bundle.toString()= " + contacts.toString());
     }

     switch (requestCode) {
     case CONTACT_PICKER_RESULT:
     Bundle bundle = data.getExtras();
     String result = bundle.getString("result");
     ArrayList<String> contacts = bundle.getStringArrayList("result");
     Log.i(TAG, "lanunchMultiplePhonePicker bundle.toString()= " + contactsPick.toString());
     handleContactRequest(requestCode, resultCode, data);
     break;
     default:
     Log.e("OnActivityResult", String.format("Request failed - resultCode: %d, requestCode: %d", resultCode, requestCode));
     }
     }
     }
**/
}
