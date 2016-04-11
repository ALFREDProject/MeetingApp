package eu.alfred.meetingapp;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import java.util.List;

public class Meeting implements Serializable {

    private int _id;
    private String subject;
    private long date;
    private String location;
    private List<Contact> invitedContacts;

    public Meeting(String subject, long date, String location, List<Contact> invitedContacts) {
        this.subject = subject;
        this.date = date;
        this.location = location;
        this.invitedContacts = invitedContacts;

    }

    public int get_id() { return _id; }

    public void set_id(int _id) { this._id = _id; }

    public String getSubject() { return subject; }

    public void setSubject(String subject) { this.subject = subject; }

    public long getDate() { return date; }

    public void setDate(long date) { this.date = date; }

    public String getLocation() { return location; }

    public void setLocation(String location) { this.location = location; }

    public List<Contact> getInvitedContacts() { return invitedContacts; }

    public void setInvitedContacts(List<Contact> invitedContacts) { this.invitedContacts = invitedContacts; }

}
