package eu.alfred.meetingapp;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import java.util.List;

/**
 * Created by deniz.coskun on 08.03.16.
 */
public class Meeting implements Serializable {

    private String subject;
    private Date date;
    //private Time time;
    private String location;
    private List<Contact> invitedContacts;

    public Meeting(String subject, Date date, String location, List<Contact> invitedContacts) {
        this.subject = subject;
        this.date = date;
        //this.time = time;
        this.location = location;
        this.invitedContacts = invitedContacts;
    }

    public String getSubject() { return subject; }

    public void setSubject(String subject) { this.subject = subject; }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    // public Time getTime() { return time; }

    // public void setTime(Time time) { this.time = time; }

    public String getLocation() { return location; }

    public void setLocation(String location) { this.location = location; }

    public List<Contact> getInvitedContacts() { return invitedContacts; }

    public void setInvitedContacts(List<Contact> invitedContacts) { this.invitedContacts = invitedContacts; }

}
