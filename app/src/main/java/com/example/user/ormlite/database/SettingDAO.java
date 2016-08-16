package com.example.user.ormlite.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class SettingDAO extends BaseDaoImpl<Setting, Integer> {

    protected SettingDAO(ConnectionSource connectionSource,
                          Class<Setting> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Setting> getAllSettings() throws SQLException {
        //return this.queryForAll();
        List<Setting> listToReturn = new ArrayList<Setting>();
        listToReturn = this.queryForAll();
        //listToReturn = (List<Pick>) this.queryBuilder().orderBy("Id",true);
        return listToReturn;
    }

    public int getValByName(String name) throws SQLException{
        int res =0;
        try {
            List<Setting> list = this.queryBuilder().where().eq("Name", name).query();
            for (Setting item:list){
                res=item.getVal();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public boolean setValByName(String name, int val) throws SQLException{
        try {
            UpdateBuilder<Setting, Integer> updateBuilder = this.updateBuilder();
            updateBuilder.where().eq("Name", name);
            updateBuilder.updateColumnValue("Val", val);
            updateBuilder.update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int count(){
        return this.count();
    }

}
