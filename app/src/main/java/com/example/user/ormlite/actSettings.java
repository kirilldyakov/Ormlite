package com.example.user.ormlite;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.example.user.ormlite.database.HelperFactory;
import com.example.user.ormlite.database.Pick;
import com.example.user.ormlite.database.Setting;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import java.sql.SQLException;
import java.util.List;

@EActivity(R.layout.activity_settings)
public class actSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @AfterViews
    protected void init() {
        //txtMain.setText("newText");


// получаем экземпляр элемента ListView
        ListView listView = (ListView)findViewById(R.id.listView);

// определяем массив типа String
        //final String[] catNames = new String[] {
        //        "Рыжик", "Барсик", "Мурзик", "Мурка", "Васька",
        //        "Томасина", "Кристина", "Пушок", "Дымка", "Кузя",
        //        "Китти", "Масяня", "Симба"
        //};
        List<Setting> settings =null;
        try {
            settings = HelperFactory.getHelper().getSettingDAO().getAllSettings();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (settings==null) return;
        int i=0;
        String[] catNames = new String[settings.size()];
        for (Setting setting: settings){
            catNames[i++] = setting.getName();
        }
// используем адаптер данных
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
        //        android.R.layout.simple_list_item_2, catNames);



        //listView.setAdapter(adapter);


    }
}

//http://developer.alexanderklimov.ru/android/theory/simpleadapter.php