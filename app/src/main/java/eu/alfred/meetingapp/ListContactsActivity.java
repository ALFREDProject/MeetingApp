package eu.alfred.meetingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
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

public class ListContactsActivity extends FragmentActivity {

    private ListView contactsListView;
    private List<Contact> contacts = new ArrayList<Contact>();
    private List<Contact> invitedContacts = new ArrayList<Contact>();
    private List<String> contactNames = new ArrayList<String>();
    private String requestURL, userId, source;
    private SharedPreferences preferences;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contacts);

        source = getIntent().getStringExtra("Source");
        Log.d("Source Activity", source);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = preferences.getString("id", "");
        requestQueue = Volley.newRequestQueue(this);
        contactsListView = (ListView) findViewById(R.id.contactsListView);

        getContacts();
    }


    private void getContacts() {

        requestURL = "http://alfred.eu:8080/personalization-manager/services/databaseServices/users/" + userId + "/contacts/all";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, requestURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject contact = response.getJSONObject(i);
                        contacts.add(new Contact(contact.getString("firstName") + " " + contact.getString("lastName"), contact.getString("phone"), contact.getString("email")));
                        contactNames.add(contact.getString("firstName"));
                    }
                    displayContacts();
                } catch (JSONException e) { e.printStackTrace(); }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { Log.e("VOLLEY", error.getMessage()); }
        });

        requestQueue.add(request);
    }

    private void displayContacts() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, contactNames);
        contactsListView.setAdapter(adapter);
        if(source.contentEquals("meeting")) {
            Log.d("choice mode", "works!");
            contactsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            contactsListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                    if (checked) {
                        invitedContacts.add(contacts.get(position));
                    } else {
                        invitedContacts.remove(contacts.get(position));
                    }

                    mode.setTitle(invitedContacts.size() + " contacts selected");
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.contacts, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("InvitedContacts", (Serializable) invitedContacts);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) { }
            });
        }

    }


}
