package eu.alfred.meetingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import eu.alfred.meetingapp.adapter.RecyclerAdapter;

public class MainActivity extends AppCompatActivity {

    //private List<Meeting> meetings = new ArrayList<Meeting>();
    private List<Contact> contacts = new ArrayList<Contact>();
    private RecyclerView meetingsRecyclerView;
    private String requestURL = "http://alfred.eu:8080/personalization-manager/services/databaseServices/users/56e6ad24e4b0fadc1367b665/contacts/all";
    // 56df0386e4b054b0e40cd6fc
    RequestQueue requestQueue;
    MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);
        dbHandler = new MyDBHandler(this, null, null, 1);

        loadContacts();
        loadMeetings();
        //loadMeetings();
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
                displayContactsIntent.putExtra("Contacts", (Serializable) contacts);
                displayContactsIntent.putExtra("Source", "main");
                startActivity(displayContactsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void loadContacts() {

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
}
