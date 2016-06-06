package eu.alfred.meetingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.alfred.api.PersonalAssistant;
import eu.alfred.api.personalization.client.ContactDto;
import eu.alfred.api.personalization.client.ContactMapper;
import eu.alfred.api.personalization.model.Contact;
import eu.alfred.api.personalization.webservice.PersonalizationManager;
import eu.alfred.api.proxies.interfaces.ICadeCommand;
import eu.alfred.meetingapp.adapter.RecyclerAdapter;
import eu.alfred.meetingapp.helper.PersonalAssistantProvider;
import eu.alfred.meetingapp.helper.PersonalizationArrayResponse;
import eu.alfred.ui.AppActivity;
import eu.alfred.ui.CircleButton;

public class MainActivity extends AppActivity implements ICadeCommand {

    private List<Contact> contacts = new ArrayList<Contact>();
    private String userId;
    private MyDBHandler dbHandler;
    private SharedPreferences preferences;

    private final static String TAG = "MA:MainActivity";
    private final static String CREATE_MEETING = "CreateMeetingAction";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().setTitle(R.string.upcoming); // TODO: use resource

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String loggedUserId = preferences.getString("id", "");
        if(loggedUserId.isEmpty()){
            //userId = "56e6c782e1079f764b596c87";
            //userId = "56e6f095e4b0fadc1367b66b";
            userId = "571f928be4b0d25de0692ed6";
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("id", userId);
            editor.commit();
        }
        else { userId = loggedUserId; }

        dbHandler = new MyDBHandler(this, null, null, 1);

        loadContacts();
        loadMeetings();

        circleButton = (CircleButton) findViewById(R.id.voiceControlBtn);
        circleButton.setOnTouchListener(new MicrophoneTouchListener());

        // trigger init
        PersonalAssistantProvider.getPersonalAssistant(this);

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

        PersonalAssistant PA = PersonalAssistantProvider.getPersonalAssistant(this);
        PersonalizationManager PM = new PersonalizationManager(PA.getMessenger());
        PM.retrieveAllUserContacts(userId, new PersonalizationArrayResponse() {
            @Override
            public void OnSuccess(JSONArray a) {
                Log.i(TAG, "retrieveAllUserContacts succeeded");

                Type type = new TypeToken<ArrayList<ContactDto>>() {}.getType();
                List<ContactDto> dto = new Gson().fromJson(a.toString(), type);

                for (ContactDto cd : dto) {
                    Contact contact = ContactMapper.toModel(cd);
                    contacts.add(contact);
                }
            }

            @Override
            public void OnError(Exception e) {
                Log.e(TAG, "retrieveUserProfiles failed", e);
            }
        });
    }

    private void loadMeetings() {
        RecyclerView meetingsRecyclerView = (RecyclerView) findViewById(R.id.meetingsRecyclerView);
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
                String subject = map.get("selected_subject");
                String location = map.get("selected_location");
                String year = map.get("selected_year");
                String month = map.get("selected_month");
                String day = map.get("selected_day");
                alfredMeetingIntent.putExtra("Subject", subject);
                alfredMeetingIntent.putExtra("Location", location);
                alfredMeetingIntent.putExtra("Year", year);
                alfredMeetingIntent.putExtra("Month", month);
                alfredMeetingIntent.putExtra("Day", day);
                startActivityForResult(alfredMeetingIntent, 2);
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
