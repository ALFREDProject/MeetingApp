package eu.alfred.meetingapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MeetingDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText subjectEditText;
    private EditText datePickerEditText;
    private EditText timePickerEditText;
    private TextView testTextView;
    private Button addContactsButton;
    private String requestURL = "http://alfred.eu:8080/personalization-manager/services/databaseServices/users/56dd6bb2e4b074fe33fd8d03/contacts/all";
    private int mYear, mMonth, mDay, mHour, mMinute;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_details);

        subjectEditText = (EditText) findViewById(R.id.subjectEditText);
        datePickerEditText = (EditText) findViewById(R.id.dateEditText);
        timePickerEditText = (EditText) findViewById(R.id.timeEditText);
        addContactsButton = (Button) findViewById(R.id.addContactsButton);
        testTextView = (TextView) findViewById(R.id.testTextView);
        requestQueue = Volley.newRequestQueue(this);

        datePickerEditText.setOnClickListener(this);
        timePickerEditText.setOnClickListener(this);
        addContactsButton.setOnClickListener(this);



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

             Log.d("addContactButton", "Button clicked!");

             JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, requestURL, null, new Response.Listener<JSONArray>() {
                 @Override
                 public void onResponse(JSONArray response) {
                     try {
                         JSONObject contact = response.getJSONObject(0);
                         testTextView.setText(contact.getString("alfredUserName"));
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }

                     //Log.d("jsonresponse", "in onresponse");
                 }
             }, new Response.ErrorListener() {
                 @Override
                 public void onErrorResponse(VolleyError error) {
                     Log.e("VOLLEY", error.getMessage());
                 }
             });

             requestQueue.add(request);

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
