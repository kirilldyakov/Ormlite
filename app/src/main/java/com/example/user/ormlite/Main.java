package com.example.user.ormlite;



import android.app.Activity;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.example.user.ormlite.Database.HelperFactory;
import com.example.user.ormlite.Database.Pick;
import com.example.user.ormlite.Database.Setting;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;
import java.util.List;

@EActivity(R.layout.activity_main)
public class Main extends Activity {

    @ViewById(R.id.textMain)
    TextView txtHW;
//
    @AfterViews
    protected void init() {
        txtHW.setText("newText");


        try {
            HelperFactory.getHelper().getPickDAO().create(new Pick(353));
            HelperFactory.getHelper().getPickDAO().create(new Pick(459));
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {


            HelperFactory.getHelper().getSettingDAO().create(new Setting("FILTER", 300));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            HelperFactory.getHelper().getSettingDAO().create(new Setting("IMPULS", 3200));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            HelperFactory.getHelper().getSettingDAO().create(new Setting("TICK_COUNT", 3200));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Pick> picks = null;
        try {
            picks = HelperFactory.getHelper().getPickDAO().getAllPicks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Pick pick:picks) {
            Log.d("LOG_TAG", pick.toString());
        }

        List<Setting> settings = null;

        try {
            settings = HelperFactory.getHelper().getSettingDAO().getAllSettings();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Setting setting:settings) {
            Log.d("LOG_TAG", setting.toString());
        }

    }
//
//    @Click(R.id.btnSettings)
//    void btnSettingsClick() {
//        Settings_.intent(this).start();
//    }
//
    @Click(R.id.btnGraph)
    void btnGraphClick() {
        Graph_.intent(this).start();
    }
//
//    @Click(R.id.btnNoice)
//    void btnNoiceSignalClick() {
//        Noice_.intent(this).start();
//    }
//
//    @Click(R.id.btnLamp)
//    void btnLampClick() {
//        Lamp_.intent(this).start();



    @Override
    public void onBackPressed() {
        Toast toast = Toast.makeText(this,"Не рычи", Toast.LENGTH_SHORT);
        toast.show();
    }
}