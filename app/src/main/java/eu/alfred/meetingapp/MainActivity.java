package eu.alfred.meetingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.alfred.api.proxies.interfaces.ICadeCommand;
import eu.alfred.meetingapp.adapter.RecyclerAdapter;
import eu.alfred.ui.AppActivity;
import eu.alfred.ui.CircleButton;

public class MainActivity extends AppActivity implements ICadeCommand {

    private List<Contact> contacts = new ArrayList<Contact>();
    private RecyclerView meetingsRecyclerView;
    private String requestURL, userId, loggedUserId;
    private RequestQueue requestQueue;
    private MyDBHandler dbHandler;
    private SharedPreferences preferences;

    final static String CREATE_MEETING = "CreateMeetingAction";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        loggedUserId = preferences.getString("id", "");
        if(loggedUserId.isEmpty()){
            userId = "56e6c782e1079f764b596c87";
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("id", userId);
            editor.commit();
        }
        else { userId = loggedUserId; }

        requestQueue = Volley.newRequestQueue(this);
        dbHandler = new MyDBHandler(this, null, null, 1);

        loadContacts();
        loadMeetings();

        circleButton = (CircleButton) findViewById(R.id.voiceControlBtn);
        circleButton.setOnTouchListener(new CircleTouchListener());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // return super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.organize_meeting_item:
                Intent meetingDetailsIntent = new Intent(this, MeetingDetailsActivity.class);
                meetingDetailsIntent.putExtra("Contacts", (Serializable) contacts);
                startActivityForResult(meetingDetailsIntent, 2);
                //Intent i = new Intent(this, MeetingDetailsActivity.class);
                //startActivity(i);
                return true;
            case R.id.display_contacts_item:
                Intent displayContactsIntent = new Intent(this, ListContactsActivity.class);
                //displayContactsIntent.putExtra("Contacts", (Serializable) contacts);
                displayContactsIntent.putExtra("Source", "main");
                startActivity(displayContactsIntent);
                return true;
            case R.id.logout_item:
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                Intent goToLoginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(goToLoginIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void loadContacts() {

        requestURL = "http://alfred.eu:8080/personalization-manager/services/databaseServices/users/" + userId + "/contacts/all";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, requestURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject contact = response.getJSONObject(i);
                        contacts.add(new Contact(contact.getString("firstName") + " " + contact.getString("lastName"), contact.getString("phone"), contact.getString("email")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.getMessage());
            }
        });

        requestQueue.add(request);
    }

    private void loadMeetings() {
        meetingsRecyclerView = (RecyclerView) findViewById(R.id.meetingsRecyclerView);
        //meetings = dbHandler.getDBMeetings();
        RecyclerAdapter adapter = new RecyclerAdapter(this, dbHandler.getDBMeetings());
        meetingsRecyclerView.setAdapter(adapter);

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(this);
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        meetingsRecyclerView.setLayoutManager(mLinearLayoutManagerVertical);

        meetingsRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /** Serializable extra = data.getSerializableExtra("Meeting");
        if (extra != null){
            Meeting newMeeting = (Meeting) extra;
            meetings.add(newMeeting);
        } **/

        //loadMeetings();
        loadMeetings();

    }

    @Override
    public void onNewIntent(Intent intent) { super.onNewIntent(intent); }

    @Override
    public void performAction(String s, Map<String, String> map) {
        Log.d("Perform Action string", s);
        Log.d("Perform Action string", map.toString());

        switch (s) {
            case CREATE_MEETING:
                Intent alfredMeetingIntent = new Intent(this, MeetingDetailsActivity.class);
                String subject = (String) map.get("selected_subject");
                String location = (String) map.get("selected_location");
                String year = (String) map.get("selected_year");
                String month = (String) map.get("selected_month");
                String day = (String) map.get("selected_day");
                alfredMeetingIntent.putExtra("Subject", subject);
                alfredMeetingIntent.putExtra("Location", location);
                alfredMeetingIntent.putExtra("Year", year);
                alfredMeetingIntent.putExtra("Month", month);
                alfredMeetingIntent.putExtra("Day", day);
                startActivity(alfredMeetingIntent);
                break;
            default:
                break;
        }

        cade.sendActionResult(true);
    }

    @Override
    public void performWhQuery(String s, Map<String, String> map) { }

    @Override
    public void performValidity(String s, Map<String, String> map) { }

    @Override
    public void performEntityRecognizer(String s, Map<String, String> map) { }

}
