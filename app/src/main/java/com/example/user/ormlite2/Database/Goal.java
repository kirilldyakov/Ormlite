package com.example.user.ormlite2.Database;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "goals")
public class Goal{

    public final static String GOAL_NAME_FIELD_NAME = "name";

    @DatabaseField(generatedId = true)
    private int Id;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = GOAL_NAME_FIELD_NAME)
    private String name;

    @DatabaseField(dataType = DataType.DATE)
    private Date lastEditDate;

    @DatabaseField()
    private String notes;

    public Goal(){
        //scheduleList = new ArrayList<Shedule>();
        //priorities = new ArrayList<PrioritySchedule>();
    }
}