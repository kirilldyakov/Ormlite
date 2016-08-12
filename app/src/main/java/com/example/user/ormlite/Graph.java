package com.example.user.ormlite;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;

import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.ormlite.Database.HelperFactory;
import com.example.user.ormlite.Database.Pick;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
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

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


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
    int GraphState = 1201;


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
        data = null;
        GraphState = GRAPH_POWER;
        txtGraphLabel.setText(getText(R.string.graphLabelPower));
        if (calcPower())
            drawGraph(TYPE_LINE, Color.BLUE);
        else
            Toast.makeText(this, "Нет данных", Toast.LENGTH_SHORT).show();
    }

    @OptionsItem(R.id.mi_graph_Energy)
    void showEnergyGraph() {
        data = null;
        GraphState = GRAPH_ENERGY;

        txtGraphLabel.setText(getText(R.string.graphLabelEnergy));

        if (calcEnergy())
            drawGraph(TYPE_LINE, Color.RED);
        else
            Toast.makeText(this, getText(R.string.noneData), Toast.LENGTH_SHORT).show();

    }

    @OptionsItem(R.id.mi_graph_DurtyData)
    void showDurtyDataGraph() {
        data = null;
        GraphState = GRAPH_DURTY;
        txtGraphLabel.setText(getText(R.string.graphLabelDurty));
        if (calcDurtyData()) {
//            draw(R.color.DurtyLine);
            drawGraph(TYPE_BAR, Color.GREEN);
            drawFilterLine(Color.CYAN);
        } else
            Toast.makeText(this, getText(R.string.noneData), Toast.LENGTH_SHORT).show();
    }


    //Расчет мощности потребления по имеющимся данным
    public boolean calcEnergy() {

        if (!calcPower()) return false;

        for(int i =1; i<data.length;i++){
            double x_0 = data[i].getX();
            double y_0 = data[i].getY();
            double y_m1 = data[i-1].getY();
            data[i]=new DataPoint(x_0, y_0+y_m1);
        }

        return true;
    }

    //Расчет количества потребленной ЭЭ за время измерений
    public boolean calcPower() {
        long minDT = HelperFactory.getHelper().getPickDAO().getMinDT();


        //double prev_y = 0;
        //double prev_x = 0;

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
        if (picks.size()==0) return false;

        ArrayList<Pick> tmpPicks = new ArrayList<>() ;

        int j =0;
        for(int i =0; i< picks.size();i++){
            Pick p = new Pick(picks.get(i).getDateTimeLong(),picks.get(i).getAmplitude());
            tmpPicks.add(p);
            int lastPickIdx = tmpPicks.size()-1;
            if (lastPickIdx>0){
                Pick pick_m1 = tmpPicks.get(lastPickIdx);
                Pick pick_m2 = tmpPicks.get(lastPickIdx-1);

                if (pick_m1.getDateTimeLong()-pick_m2.getDateTimeLong()<2000){
                    if (pick_m1.getAmplitude()<pick_m2.getAmplitude())
                        tmpPicks.remove(lastPickIdx);
                    else
                        tmpPicks.remove(lastPickIdx-1);
                }
            }
        }


        long last_dtl=0;
        this.data = new DataPoint[10/*tmpPicks.size()*/];
        for(int i=0; i<10/*tmpPicks.size()*/;i++){
            //double x = (tmpPicks.get(i).getDateTimeLong()-minDT)/(1000.0);
            //double x = 1.0*tmpPicks.get(i).getDateTimeLong()/1000;
            //long dt = tmpPicks.get(i).getDateTimeLong();
            //double x = 1.0*i;//1.0*dt/(1000.0*60*60*24)+10957;
            Date x = new Date((long)tmpPicks.get(i).getDateTimeLong());
            //Date x = new Date(16,8,1+i);
            //Date x =DateFormat.getInstance().format(tmpPicks.get(i).getDateTimeLong());

            double y = 0;
            if (i>0){
                double interval = 1.0*(tmpPicks.get(i).getDateTimeLong() - tmpPicks.get(i-1).getDateTimeLong())/1000;
                y= 3600.0/(tickCount*interval);
            }

            data[i] = new DataPoint((Date)x,y);
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

        if (picks.size()==0) return false;

        int cnt = picks.size();
        this.data = new DataPoint[cnt];

        int i=0;
        for(Pick pick: picks){
            long dtl = pick.getDateTimeLong();
            double x = 1.0*(dtl - minDT) / (1000); // секунды от начала
            double y = pick.getAmplitude();
            this.data[i++] = new DataPoint(x, y);
        }
        return true;
    }

    //Отрисовка данных на графике
    @TargetApi(Build.VERSION_CODES.N)
    boolean drawGraph(int GraphType, int color) {
        try {
            graphView.getViewport().setScrollable(true);
            graphView.getViewport().setScalable(true);
            graphView.removeAllSeries();

            graphView.getGridLabelRenderer().setLabelFormatter(
                    new DateAsXAxisLabelFormatter(this,new SimpleDateFormat("ddMMyyhhmmss")));// hh:mm:ss

           // graphView.getGridLabelRenderer().setLabelFormatter(
           //         new DateAsXAxisLabelFormatter(getApplicationContext(),DateFormat.getTimeInstance()));
            graphView.getGridLabelRenderer().setHorizontalAxisTitle("Date");
            graphView.getGridLabelRenderer().setNumHorizontalLabels(3);


            graphView.getGridLabelRenderer().setHorizontalLabelsVisible(true);
            graphView.getGridLabelRenderer().setVerticalLabelsVisible(true);

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

            //graphView.getViewport().setYAxisBoundsManual(true);
            //graphView.getViewport().setMinY(0.0);

//            int maxX = Math.min(20, data.length);
//            graphView.getViewport().setMaxX(Math.ceil(data[maxX-1].getX()));
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    boolean drawFilterLine(int color) {
        int filter = 0;
        try {
            filter = HelperFactory.getHelper().getSettingDAO().getValByName("FILTER");
        } catch (SQLException e) {
            e.printStackTrace();
        }
       return drawGraph(filter, color);
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
                Uri.parse("android-app://com.example.user.ormlite/http/host/path")
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
                Uri.parse("android-app://com.example.user.ormlite/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
