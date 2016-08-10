package com.example.user.ormlite.Database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PickDAO extends BaseDaoImpl<Pick, Integer> {

    protected PickDAO(ConnectionSource connectionSource,
                      Class<Pick> dataClass) throws SQLException{
        super(connectionSource, dataClass);
    }

    public List<Pick> getAllPicks() throws SQLException {
        //return this.queryForAll();
        List<Pick> listToReturn = new ArrayList<Pick>();
        listToReturn = this.queryForAll();
        //listToReturn = (List<Pick>) this.queryBuilder().orderBy("Id",true);
        return listToReturn;
    }

    public List<Pick> getAllPicks2() throws SQLException {
        List<Pick> listToReturn = new ArrayList<Pick>();
        String minDT = Long.toString(this.getMinDT());

        listToReturn = (List<Pick>) this.queryBuilder().selectColumns();
        return listToReturn;
    }


    public List<Pick> getAllPicksWhereAmplitudeGE(int filter) throws SQLException {
        List<Pick> listToReturn = new ArrayList<Pick>();
        QueryBuilder<Pick, Integer> qb = this.queryBuilder();
        qb.where().ge("Amplitude", filter);
        qb.orderBy("Id",true);
        listToReturn = qb.query();
        return listToReturn;
    }


    public long getMinDT(){
        long res=0;
        try {
            res = this.queryRawValue("Select min(DateTimeLong) from pick;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public int getCount(){
        try {
            return (int) this.countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}