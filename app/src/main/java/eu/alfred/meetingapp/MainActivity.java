package eu.alfred.meetingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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

public class MainActivity extends AppCompatActivity {

    private Button meetingButton;
    private List<Contact> contacts = new ArrayList<Contact>();
    private String requestURL = "http://alfred.eu:8080/personalization-manager/services/databaseServices/users/56dec456e4b074fe33fd8d05/contacts/all";
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        meetingButton = (Button) findViewById(R.id.meeting_button);
        requestQueue = Volley.newRequestQueue(this);

        meetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent meetingDetailsActivity = new Intent(v.getContext(), MeetingDetailsActivity.class);
                startActivity(meetingDetailsActivity);
            }
        });

        loadContacts();
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
                Intent meetingDetailsActivity = new Intent(this, MeetingDetailsActivity.class);
                startActivity(meetingDetailsActivity);
                return true;
            case R.id.display_contacts_item:
                Intent displayContactsIntent = new Intent(this, ListContactsActivity.class);
                displayContactsIntent.putExtra("Contacts", (Serializable) contacts);
                startActivityForResult(displayContactsIntent, 1);
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
                        contacts.add(new Contact(contact.getString("alfredUserName"), contact.getString("phone"), contact.getString("email")));
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


}
