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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import eu.alfred.api.PersonalAssistant;
import eu.alfred.api.personalization.client.ContactDto;
import eu.alfred.api.personalization.client.ContactMapper;
import eu.alfred.api.personalization.model.Contact;
import eu.alfred.api.personalization.webservice.PersonalizationManager;
import eu.alfred.meetingapp.helper.PersonalAssistantProvider;
import eu.alfred.meetingapp.helper.PersonalizationArrayResponse;

public class ListContactsActivity extends FragmentActivity {

    private ListView contactsListView;
    private List<Contact> contacts = new ArrayList<Contact>();
    private List<Contact> invitedContacts = new ArrayList<Contact>();
    private List<String> contactNames = new ArrayList<String>();
    private String userId, source;

	private final static String TAG = "MA:ListContactsAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contacts);

        source = getIntent().getStringExtra("Source");
        Log.d(TAG, "Source Activity: " + source);

	    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = preferences.getString("id", "");
        contactsListView = (ListView) findViewById(R.id.contactsListView);

        getContacts();
    }


    private void getContacts() {

        PersonalAssistant PA = PersonalAssistantProvider.getPersonalAssistant(this);
        PersonalizationManager PM = new PersonalizationManager(PA.getMessenger());

        PM.retrieveAllUserContacts(userId, new PersonalizationArrayResponse() {
            @Override
            public void OnSuccess(JSONArray array) {
                Log.i(TAG, "retrieveAllUserContacts succeeded");

	            Type type = new TypeToken<ArrayList<ContactDto>>() {}.getType();
	            List<ContactDto> dto = new Gson().fromJson(array.toString(), type);

	            for (ContactDto cd : dto) {
		            Contact contact = ContactMapper.toModel(cd);
		            Log.d(TAG, "Retrieved " + contact);

		            contacts.add(contact);
		            contactNames.add(contact.getFirstName() + " x " + contact.getAlfredUserName());
	            }

	            displayContacts();
            }
        });

    }

    private void displayContacts() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, contactNames);
        contactsListView.setAdapter(adapter);
        if (source.contentEquals("meeting")) {
            Log.d(TAG, "choice mode");
            contactsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            contactsListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                    if (checked) {
                        invitedContacts.add(contacts.get(position));
                    } else {
                        invitedContacts.remove(contacts.get(position));
                    }

                    mode.setTitle(invitedContacts.size() + " " + getString(R.string.contacts_selected));
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
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("InvitedContacts", (Serializable) invitedContacts);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                }
            });
        }
    }
}
