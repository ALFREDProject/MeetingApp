package eu.alfred.meetingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button meetingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        meetingButton = (Button) findViewById(R.id.meeting_button);

        meetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent meetingDetailsActivity = new Intent(v.getContext(), MeetingDetailsActivity.class);
                startActivity(meetingDetailsActivity);
            }
        });


    }
}
