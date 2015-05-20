package com.subrat.Oxygen.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.subrat.Oxygen.R;
import com.subrat.Oxygen.backendRoutines.UpdateObjectsInAThread;
import com.subrat.Oxygen.customviews.OxygenView;
import com.subrat.Oxygen.objects.Object;

/**
 * Created by subrat.panda on 07/05/15.
 */
public class OxygenActivity extends Activity {
    private int alertSecondsCounter = 0;
    Runnable runnable;
    OxygenView oxygenView;

    UpdateObjectsInAThread updateObjectsInAThread = null;
    Handler threadHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oxygen);
        oxygenView = (OxygenView) findViewById(R.id.view);
        oxygenView.oxygenActivity = this;

        threadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                oxygenView.invalidate();
            }
        };

        if (updateObjectsInAThread == null) {
            updateObjectsInAThread = new UpdateObjectsInAThread(this, threadHandler);
        }
        startSimulation();
    }

    public void stopSimulation() {
        updateObjectsInAThread.stopThread();
    }

    public void startSimulation() {
        updateObjectsInAThread.startThread();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Object.getObjectList().clear();
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopSimulation();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        oxygenView.invalidate();
        alertSecondsCounter = 6;
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage("Resuming simulation in " + alertSecondsCounter-- + " seconds");
        alertDialog.show();

        final Handler handler  = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (alertSecondsCounter == 0) {
                    alertDialog.cancel();
                    startSimulation();
                } else {
                    alertDialog.setMessage("Resuming simulation in " + alertSecondsCounter-- + " seconds");
                    handler.postDelayed(runnable, 1000);
                }
            }
        };

        handler.postDelayed(runnable, 1000);
    }
}

