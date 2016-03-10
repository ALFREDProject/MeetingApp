package eu.alfred.meetingapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import eu.alfred.meetingapp.Meeting;
import eu.alfred.meetingapp.R;

/**
 * Created by deniz.coskun on 10.03.16.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MeetingsViewHolder> {

    private List<Meeting> meetings;
    private LayoutInflater mInflater;

    public RecyclerAdapter(Context context, List<Meeting> meetings) {
        this.meetings = meetings;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public MeetingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.meeting_list_item, parent, false);
        MeetingsViewHolder holder = new MeetingsViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return meetings.size();
    }

    @Override
    public void onBindViewHolder(MeetingsViewHolder holder, int position) {
        
        Meeting meeting = meetings.get(position);
        holder.setData(meeting, position);

    }

    class MeetingsViewHolder extends RecyclerView.ViewHolder {

        TextView meetingSubjectTextView, meetingLocationTextView, meetingDateTextView;
        Meeting meeting;

        public MeetingsViewHolder(View itemView) {
            super(itemView);
            meetingSubjectTextView = (TextView) itemView.findViewById(R.id.meetingSubjectTextView);
            meetingLocationTextView = (TextView) itemView.findViewById(R.id.meetingLocationTextView);
            meetingDateTextView = (TextView) itemView.findViewById(R.id.meetingDateTextView);
        }

        public void setData(Meeting meeting, int position) {
            this.meetingSubjectTextView.setText(meeting.getSubject());
            this.meetingLocationTextView.setText(meeting.getLocation());
            this.meetingDateTextView.setText(meeting.getDate().toString());
            this.meeting = meeting;
        }
    }

}
