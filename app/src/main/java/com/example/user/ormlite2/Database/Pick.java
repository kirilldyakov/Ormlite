package com.example.user.ormlite2.Database;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;



@DatabaseTable(tableName = "pick")
public class Pick{

    @DatabaseField(generatedId = true)
    private int Id;


    @DatabaseField(canBeNull = false, dataType = DataType.LONG)
    private long DateTimeLong;

    @DatabaseField(canBeNull = false, dataType = DataType.INTEGER)
    private int Amplitude;

    public Pick(){
        //scheduleList = new ArrayList<Shedule>();
        //priorities = new ArrayList<PrioritySchedule>();
    }

    public Pick(Long dateTimeLong, int amplitude) {
        DateTimeLong = dateTimeLong;
        Amplitude = amplitude;
    }

    public Pick(int amplitude) {
        Amplitude = amplitude;
        DateTimeLong = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Pick{" +
                "Id=" + Id +
                ", DateTimeLong=" + DateTimeLong +
                ", Amplitude=" + Amplitude +
                '}';
    }

    public long getDateTimeLong() {
        return DateTimeLong;
    }

    public void setDateTimeLong(long dateTimeLong) {
        DateTimeLong = dateTimeLong;
    }

    public int getAmplitude() {
        return Amplitude;
    }
}