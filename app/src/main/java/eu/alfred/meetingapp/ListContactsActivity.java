package eu.alfred.meetingapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListContactsActivity extends AppCompatActivity {

    private ListView contactsListView;
    private List<Contact> contacts = new ArrayList<Contact>();
    private List<String> contactNames = new ArrayList<String>();
    private List<Contact> invitedContacts = new ArrayList<Contact>();
    private String source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contacts);

        contactsListView = (ListView) findViewById(R.id.contactsListView);
        source = getIntent().getStringExtra("Source");
        Serializable extra = getIntent().getSerializableExtra("Contacts");
        if (extra != null) {
            contacts = (ArrayList<Contact>) extra;
            for (Contact contact : contacts) {
                contactNames.add(contact.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, contactNames);
            contactsListView.setAdapter(adapter);
            contactsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            contactsListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                    if(checked) { invitedContacts.add(contacts.get(position)); }
                    else { invitedContacts.remove(contacts.get(position)); }

                    mode.setTitle(invitedContacts.size() + " contacts selected");

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.contacts, menu);

                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch(item.getItemId()) {
                        case R.id.add_to_invited_list:
                            if (source.equals("main")) {
                                Intent meetingIntent = new Intent(getApplicationContext(), MeetingDetailsActivity.class);
                                meetingIntent.putExtra("InvitedContacts", (Serializable) invitedContacts);
                                startActivity(meetingIntent);
                            }
                            else {
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("InvitedContacts", (Serializable) invitedContacts);
                                setResult(RESULT_OK, returnIntent);
                                finish();
                            }


                            //Intent meetingIntent = new Intent(getApplicationContext(), MeetingDetailsActivity.class);
                            //meetingIntent.putExtra("InvitedContacts", (Serializable) invitedContacts);
                            //Log.d("invited contacts", invitedContacts.toString());
                            //startActivity(meetingIntent);
                    }

                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }

    }

}
