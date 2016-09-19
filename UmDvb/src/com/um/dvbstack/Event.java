package com.um.dvbstack;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Time;


public class Event implements Parcelable{
    private int progId;
	private Time start;
	private Time end;
	private String name;
	
	public Event()
	{

	}
	public Event(int id, Time s, Time e, String n)
	{
        progId = id;
		start = s;
		end	  = e;
		name  = n;
	}
    public int getProgId() {
        return progId;
    }
    public void setProgId(int progId) {
        this.progId = progId;
    }
	public Time getStartTime()
	{
		return start;
		
	}
	
	public Time getEndTime()
	{
		return end;
	}
	
	public String getName()
	{
		return name;
	}

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeValue(progId);
        parcel.writeValue(start);
        parcel.writeValue(end);
        parcel.writeString(name);
    }
    public static final Creator<Event> CREATOR =
            new Creator<Event>() {
                @Override
                public Event createFromParcel(Parcel source) {
                    int progId = source.readInt();
                    Time start = (Time) source.readValue(Time.class.getClassLoader());
                    Time end = (Time) source.readValue(Time.class.getClassLoader());
                    String name = source.readString();
                    return new Event(progId, start, end, name);
                }
                @Override
                public Event[] newArray(int size) {
                    return new Event[size];
                }
            };
}
