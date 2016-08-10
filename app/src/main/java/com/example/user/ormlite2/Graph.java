package com.example.user.ormlite2;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.ormlite2.Database.HelperFactory;
import com.example.user.ormlite2.Database.Pick;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by user on 02.08.16.
 */
@EActivity(R.layout.activity_graph)
@OptionsMenu(R.menu.menu_graph)

public class Graph extends Activity {
    private GraphView graphView;
    private LinearLayout graphLayout;

    private DataPoint[] data = null;

    private final int TYPE_BAR = 1101;
    private final int TYPE_LINE = 1102;

    private final int GRAPH_POWER = 1201;
    private final int GRAPH_ENERGY = 1202;
    private final int GRAPH_DURTY = 1203;

    @InstanceState
    int GraphState = 1203;


    @ViewById(R.id.txtGraphLabel)
    TextView txtGraphLabel;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @AfterViews
    void init() {

        graphView = new GraphView(this);
        graphLayout = (LinearLayout) findViewById(R.id.GraphLayout);
        graphLayout.addView(graphView);

        switch (GraphState) {
            case GRAPH_ENERGY:
                showEnergyGraph();
                break;
            case GRAPH_POWER:
                showPowerGraph();
                break;
            case GRAPH_DURTY:
                showDurtyDataGraph();
                break;
            default:
                showPowerGraph();
        }

    }

    @OptionsItem(R.id.mi_graph_Power)
    void showPowerGraph() {
        GraphState = GRAPH_POWER;
        txtGraphLabel.setText(getText(R.string.graphLabelPower));
        if (calcPower())
            draw(TYPE_LINE, Color.BLUE);
        else
            Toast.makeText(this, "Нет данных", Toast.LENGTH_SHORT).show();
    }

    @OptionsItem(R.id.mi_graph_Energy)
    void showEnergyGraph() {
        GraphState = GRAPH_ENERGY;

        txtGraphLabel.setText(getText(R.string.graphLabelEnergy));

        if (calcEnergy())
            draw(TYPE_LINE, Color.RED);
        else
            Toast.makeText(this, getText(R.string.noneData), Toast.LENGTH_SHORT).show();

    }

    @OptionsItem(R.id.mi_graph_DurtyData)
    void showDurtyDataGraph() {
        GraphState = GRAPH_DURTY;

        txtGraphLabel.setText(getText(R.string.graphLabelDurty));

        if (calcDurtyData()) {
//            draw(R.color.DurtyLine);
            draw(TYPE_BAR, Color.GREEN);
            int filter = 0;
            try {
                filter = HelperFactory.getHelper().getSettingDAO().getValByName("FILTER");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            drawFilterLine(filter, Color.CYAN);
        } else
            Toast.makeText(this, getText(R.string.noneData), Toast.LENGTH_SHORT).show();
    }


    //Расчет мощности потребления по имеющимся данным
    public boolean calcPower() {
//       long minDT = db.getMinDT();

//        int tickCount = db.getTickCount();
//        double prev_x = 0;
//        double prev_y = 0;
//
//        Cursor cursor = db.getReadableDatabase().rawQuery("Select " + DBH.KEY_DTL + ", " + DBH.KEY_AMPL
//                + " from " + DBH.TABLE_NAME
//                + " where " + DBH.KEY_AMPL + " >5000 "
//                + " order by " + DBH.KEY_DTL + " asc;", null);
//        if (!cursor.moveToFirst())
//            return false;
//
//        List<DTlAmpl> curDTlAmpl = new ArrayList<DTlAmpl>();
//
//        long last_dtl = 0;
//        int last_ampl = 0;
//
//        do {
//            long dtl = cursor.getLong(cursor.getColumnIndex(DBH.KEY_DTL));
//            int ampl = cursor.getInt(cursor.getColumnIndex(DBH.KEY_AMPL));
//
///*            if ((dtl-last_dtl)<1000){
//                if (last_ampl<ampl){
//                    curDTlAmpl.remove(curDTlAmpl.size()-1);
//                }
//                else
//                    continue;
//            }*/
//
//
//            if ((dtl - last_dtl) < 1000) continue;
//
//            last_dtl = dtl;
//            last_ampl = ampl;
//            curDTlAmpl.add(new DTlAmpl(dtl, ampl));
//        } while (cursor.moveToNext());
//        cursor.close();
//
//        this.data = new DataPoint[curDTlAmpl.size() - 1];
//        int j = 0;
//        for (int i = 0; i < curDTlAmpl.size(); i++) {
//            double dtl = curDTlAmpl.get(i).getDTl();
//            double x = (dtl - minDT) / 1000; // секунды от начала
//            double y = 0;
//            if (x != 0) {
//                y = 3600 / (tickCount * (x - prev_x));
//                if (y > 100) y = prev_y;
//                this.data[j] = new DataPoint(x, y);
//                j++;
//            }
//            prev_x = x;
//            prev_y = y;
//        }
        return true;
    }

    //Расчет количества потребленной ЭЭ за время измерений
    public boolean calcEnergy() {
        long minDT = HelperFactory.getHelper().getPickDAO().getMinDT();


        double prev_y = 0;
        double prev_x = 0;

        int filter = 0;
        try {
            filter = HelperFactory.getHelper().getSettingDAO().getValByName("FILTER");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int tickCount = 0;
        try {
            tickCount = HelperFactory.getHelper().getSettingDAO().getValByName("TICK_COUNT");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Pick> picks = null;
        try {
            picks =HelperFactory.getHelper().getPickDAO().getAllPicksWhereAmplitudeGE(filter);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int j =0;
        while (j<picks.size()){
            long dtl0 = picks.get(j).getDateTimeLong();
            long dtl1 = picks.get(j+1).getDateTimeLong();

            int ampl0 = picks.get(j).getAmplitude();
            int ampl1 = picks.get(j).getAmplitude();

            if( dtl1-dtl0 <2000){
                if (ampl0<ampl1) picks.remove(j); else (ampl0>ampl1) picks.remove(j+1);}
            else
            j++;
        }

        long last_dtl=0;
        this.data = new DataPoint[picks.size()];
        for(int i=0; i<picks.size();i++){
            double x = (picks.get(i).getDateTimeLong()-minDT)/1000;
            double y = 0;
            if (i>0){
                y= (picks.get(i).getDateTimeLong() - picks.get(i-1).getDateTimeLong())/1000;
            }

            data[i] = new DataPoint(x,y);

        }


        return true;
    }


    public double periodInSeconds(long begDT, long endDT){
        return Math.max(0.0,(endDT-begDT)/1000);
    }

    public double power(int tickCount, double period){
        return 3600.0 / (tickCount * period);
    }




    //Подготова поступивших данных (пары [время: уровень сигнала])
    public boolean calcDurtyData() {
        long minDT = HelperFactory.getHelper().getPickDAO().getMinDT();

        List<Pick> picks = null;

        try {
            picks =HelperFactory.getHelper().getPickDAO().getAllPicks();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int cnt = picks.size();
        this.data = new DataPoint[cnt];

        int i=0;
        for(Pick pick: picks){
            long dtl = pick.getDateTimeLong();
            double x = (dtl - minDT) / (1000*60); // секунды от начала
            double y = pick.getAmplitude();
            this.data[i++] = new DataPoint(x, y);
        }
        return true;
    }

    //Отрисовка данных на графике
    boolean draw(int GraphType, int color) {
        try {
            graphView.getViewport().setScrollable(true);
            graphView.getViewport().setScalable(true);
            graphView.removeAllSeries();


            if (GraphType == TYPE_BAR) {
                BarGraphSeries<DataPoint> series = new BarGraphSeries<>(this.data);
                series.setColor(color);
                graphView.addSeries(series);
            }

            if (GraphType == TYPE_LINE) {
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(this.data);
                series.setDrawDataPoints(true);
                series.setDataPointsRadius(2);
                series.setColor(color);
                graphView.addSeries(series);
            }

            graphView.getViewport().setYAxisBoundsManual(true);
            graphView.getViewport().setMinY(0.0);

            int maxX = Math.min(10, data.length);
            graphView.getViewport().setMaxX(Math.ceil(data[maxX-1].getX()));
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    boolean drawFilterLine(int value, int color) {
        try {
            DataPoint[] filterData = new DataPoint[2];
            DataPoint BeginPoint = new DataPoint(0, value);
            DataPoint lastData = this.data[this.data.length - 1];
            DataPoint EndPoint = new DataPoint(Math.ceil(lastData.getX()), value);
            filterData[0] = BeginPoint;
            filterData[1] = EndPoint;

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(filterData);
            series.setDrawDataPoints(true);
            series.setDataPointsRadius(2);
            series.setThickness(2);
            series.setColor(color);
            graphView.addSeries(series);

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Graph Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.user.ormlite2/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Graph Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.user.ormlite2/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
