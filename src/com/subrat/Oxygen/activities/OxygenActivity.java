package com.subrat.Oxygen.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.subrat.Oxygen.R;
import com.subrat.Oxygen.backendRoutines.ShakeDetector;
import com.subrat.Oxygen.customviews.OxygenView;

import com.subrat.Oxygen.objects.Circle;
import com.subrat.Oxygen.objects.Object;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;

/**
 * Created by subrat.panda on 07/05/15.
 */
public class OxygenActivity extends Activity {
    private int alertSecondsCounter;
    Runnable runnable;
    OxygenView oxygenView;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    // Repeat Task
    private Handler mHandler;
    Runnable redrawObjects;

    private static boolean haltSimulation = false;
    public static void setHaltSimulation(boolean val) { haltSimulation = val; }
    public static boolean getHaltSimulation() { return haltSimulation; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oxygen);
        oxygenView = (OxygenView) findViewById(R.id.view);

        initializeShake();
        initializeRepeatingTask();

        startRepeatingTask();
    }

    private void initializeShake() {
        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                Object.resetVelocities();
            }
        });
    }

    void initializeRepeatingTask() {
        mHandler = new Handler();
        redrawObjects = new Runnable() {
            @Override
            public void run() {
                if (!haltSimulation) {
                    updateAllObjectsInThread();

                }
                mHandler.postDelayed(redrawObjects, Configuration.getRefreshInterval());
            }
        };
    }

    public void startRepeatingTask() {
        if (redrawObjects != null) {
            redrawObjects.run();
        }
    }

    public void stopRepeatingTask() {
        if (redrawObjects != null) {
            mHandler.removeCallbacks(redrawObjects);
        }
    }

    public void updateAllObjectsInThread() {
        final Handler threadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                oxygenView.invalidate();
                updateSensorReading();
            }
        };

        Thread background = new Thread(new Runnable() {
            public void run() {
                Object.updateAllObjects();
                threadHandler.sendMessage(threadHandler.obtainMessage());
            }
        });

        background.start();
    }

    private void updateSensorReading() {
        float[] accelValues = mShakeDetector.accelValues; // Got in mtr per sec per sec
        float convertedAccelValuesX = MathUtils.getPixelFromDP(accelValues[0] * Configuration.getGravityScale());
        float convertedAccelValuesY = MathUtils.getPixelFromDP(accelValues[1] * Configuration.getGravityScale());

        Circle.setGravity(new PointF(-convertedAccelValuesX, convertedAccelValuesY));
        /*
        float[] magnetValues = mShakeDetector.magnetValues;
        TextView textView = (TextView) findViewById(R.id.sensorValue);
        String accelText = "Accel: " + accelValues[0] + ", " + accelValues[1] + ", " + accelValues[2];
        String magnetText = "Magnet: " + magnetValues[0] + ", " + magnetValues[1] + ", " + magnetValues[2];
        textView.setText(accelText + " " + magnetText);
        */
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Object.getObjectList().clear();
        stopRepeatingTask();
        finish();
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        stopRepeatingTask();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onRestart() {
        super.onRestart();
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
                    startRepeatingTask();
                } else {
                    alertDialog.setMessage("Resuming simulation in " + alertSecondsCounter-- + " seconds");
                    handler.postDelayed(runnable, 1000);
                }
            }
        };

        handler.postDelayed(runnable, 1000);
    }
}

