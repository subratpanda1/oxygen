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

    private static boolean haltSimulation = false;
    public static void setHaltSimulation(boolean val) { haltSimulation = val; }
    public static boolean getHaltSimulation() { return haltSimulation; }

    UpdateObjectsInAThread updateObjectsInAThread;
    Handler threadHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oxygen);
        oxygenView = (OxygenView) findViewById(R.id.view);

        threadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                oxygenView.invalidate();
            }
        };

        updateObjectsInAThread = new UpdateObjectsInAThread(this, threadHandler);
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
        updateObjectsInAThread.stopThread();
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
                    updateObjectsInAThread.startThread();
                } else {
                    alertDialog.setMessage("Resuming simulation in " + alertSecondsCounter-- + " seconds");
                    handler.postDelayed(runnable, 1000);
                }
            }
        };

        handler.postDelayed(runnable, 1000);
    }
}

