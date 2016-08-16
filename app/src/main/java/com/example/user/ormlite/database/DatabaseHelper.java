package com.example.user.ormlite.database;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    //имя файла базы данных который будет храниться в /data/data/APPNAME/DATABASE_NAME.db
    private static final String DATABASE_NAME ="Counter.db";

    //с каждым увеличением версии, при нахождении в устройстве БД с предыдущей версией будет выполнен метод onUpgrade();
    private static final int DATABASE_VERSION = 3;

    //ссылки на DAO соответсвующие сущностям, хранимым в БД
    private PickDAO pickDao = null;
    private SettingDAO settingDao = null;

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Выполняется, когда файл с БД не найден на устройстве
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource){
        try
        {
            TableUtils.createTable(connectionSource, Pick.class);
            TableUtils.createTable(connectionSource, Setting.class);
        }
        catch (SQLException e){
            Log.e(TAG, "error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);}
    }

    //Выполняется, когда БД имеет версию отличную от текущей
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer,
                          int newVer){
        try{
            //Так делают ленивые, гораздо предпочтительнее не удаляя БД аккуратно вносить изменения
            TableUtils.dropTable(connectionSource, Pick.class, true);
            TableUtils.dropTable(connectionSource, Setting.class, true);
            onCreate(db, connectionSource);
        }
        catch (SQLException e){
            Log.e(TAG,"error upgrading db "+DATABASE_NAME+"from ver "+oldVer);
            throw new RuntimeException(e);
        }
    }

    //синглтон для PickDAO
     public PickDAO getPickDAO() {
         if(pickDao == null){
             try {
                 pickDao = new PickDAO(getConnectionSource(), Pick.class);
             } catch (java.sql.SQLException e) {
                 e.printStackTrace();
             }
         }
         return pickDao;
      }

    //синглтон для PickDAO
    public SettingDAO getSettingDAO() {
        if(settingDao == null){
            try {
                settingDao = new SettingDAO(getConnectionSource(), Setting.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return settingDao;
    }


    //выполняется при закрытии приложения
    @Override
    public void close(){
        super.close();

        pickDao = null;
        settingDao=null;
    }
}