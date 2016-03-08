package eu.alfred.meetingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListContactsActivity extends AppCompatActivity {

    private ListView contactsListView;
    // 56dd6bb2e4b074fe33fd8d03
    private List<String> contactNames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contacts);

        contactsListView = (ListView) findViewById(R.id.contactsListView);
        Serializable extra = getIntent().getSerializableExtra("Contacts");
        if (extra != null) {
            List<Contact> contacts = (ArrayList<Contact>) extra;
            for (Contact contact : contacts) {
                contactNames.add(contact.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, contactNames);
            contactsListView.setAdapter(adapter);
        }

    }

    /**
    private void loadContacts() {



        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, requestURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject contact = response.getJSONObject(i);
                        contacts.add(new Contact(contact.getString("alfredUserName"), contact.getString("phone"), contact.getString("email")));
                        contactNames.add(contact.getString("alfredUserName"));
                        Log.d("Contact added", contact.getString("alfredUserName"));
                    }
                    Log.d("Contacts Length", String.valueOf(contacts.size()));
                    //JSONObject contact = response.getJSONObject(0);
                    //testTextView.setText(contact.getString("alfredUserName"));
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, contactNames);
        contactsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
   **/



}
