package com.example.user.ormlite.measures;


        import java.io.IOException;
        import java.sql.SQLException;
        import java.util.ArrayList;

        import android.media.MediaRecorder;
        import android.util.Log;




public class NoiceMeter {


    //События
    public interface NoiceMeterListener {
        void onImpuls(int amplitude);
    }

    private ArrayList<NoiceMeterListener> listeners = new ArrayList<NoiceMeterListener>();

    public void addListener(NoiceMeterListener listener) {
        listeners.add(listener);
    }

    public void removeListener(NoiceMeterListener listener) {
        listeners.remove(listener);
    }

    private void fireListeners(int count) {
        for (NoiceMeterListener listener : listeners) {
            listener.onImpuls(count);
        }
    }

    private MediaRecorder mRecorder = null;
    private final String LOG_TAG = "myLogs";
    private boolean isRunning=false;


    public void initMediaRecoder()  {

        if (mRecorder == null) {
            try {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile("/dev/null/");
                mRecorder.prepare();
            }
            catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
            mRecorder.start();
            isRunning=true;
        }
    }

    public void stop_rec() {
        isRunning=false;
        fireListeners(0);
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public double getTheAmplitude(){
        if(mRecorder != null)
            return (mRecorder.getMaxAmplitude());
        else
            return 1;
    }


    public void runRecording(){
        Thread thread = new Thread() {
            public void run() {
                while (isRunning){   //true) {
                    try {

                        //if (!isRunning) this.interrupt();
                        double amp = getTheAmplitude();
                        Log.d(LOG_TAG, "noise THR- "+amp);
                        //if(amp>2000)
                        fireListeners((int)amp);
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Log.e(LOG_TAG, "local Thread error", e);
                    }
                }
            }
        };
        thread.start();
    }


}