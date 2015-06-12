package com.subrat.Oxygen.backendRoutines;

import android.content.Context;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.objects.*;
import com.subrat.Oxygen.objects.Object;
import com.subrat.Oxygen.utilities.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by subrat.panda on 08/05/15.
 */
public class UpdateObjectsInAThread {
    public enum ThreadInstruction {
        THREAD_CONTINUE(0), THREAD_STOP(1), NO_OP(2);

        private final int value;
        ThreadInstruction(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    AtomicInteger threadInstruction = new AtomicInteger(ThreadInstruction.NO_OP.getValue());

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    private Handler threadHandler;
    private Thread thread = null;

    // Repeat Task
    private Handler repeatHandler;
    Runnable repeatRunnable;
    boolean repeatTaskRunning;

    // private Context context;

    public UpdateObjectsInAThread(Context context, Handler threadHandler) {
        // this.context = context;
        this.threadHandler = threadHandler;
        repeatTaskRunning = false;
        initializeShake();
        initializeRepeatingTask();
    }

    private void startThreadEventLoop() {
        while (true) {
            if (threadInstruction.get() == ThreadInstruction.THREAD_STOP.getValue()) {
                stopRepeatingTask();
                break;
            } else if (threadInstruction.get() == ThreadInstruction.THREAD_CONTINUE.getValue()) {
                startRepeatingTask();
            }

            try {
                Thread.sleep(1);
            } catch(InterruptedException ex) {
                thread.interrupt();
            }
        }
    }

    public void startThread() {
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        threadInstruction.set(ThreadInstruction.THREAD_CONTINUE.getValue());

        if (thread == null) {
            thread = new Thread(new Runnable() {
                public void run() {
                    startThreadEventLoop();
                }
            });
        }
        thread.start();
    }

    public void stopThread() {
        threadInstruction.set(ThreadInstruction.THREAD_STOP.getValue());
        mSensorManager.unregisterListener(mShakeDetector);
        thread = null;
    }

    void initializeRepeatingTask() {
        repeatHandler = new Handler();
        repeatRunnable = new Runnable() {
            @Override
            public void run() {
            	if (OxygenActivity.getContext() == null) return;
                updateSensorReading();
                Object.updateAllObjects();
                threadHandler.sendMessage(threadHandler.obtainMessage());
                repeatHandler.postDelayed(repeatRunnable, (int)Configuration.REFRESH_INTERVAL * 1000/*in msec*/);
            }
        };
    }

    public void startRepeatingTask() {
        if (repeatTaskRunning) return;
        repeatRunnable.run();
        repeatTaskRunning = true;
    }

    public void stopRepeatingTask() {
        if (!repeatTaskRunning) return;
        repeatHandler.removeCallbacks(repeatRunnable);
        repeatTaskRunning = false;
    }

    private void updateSensorReading() {
        float[] accelValues = mShakeDetector.accelValues; // Got in mtr per sec per sec
        float convertedAccelValuesX = accelValues[0];
        float convertedAccelValuesY = accelValues[1];

        PointF gravity = new PointF(-convertedAccelValuesX, convertedAccelValuesY);
        Circle.setGravity(gravity);
        
        PointF gravityForEngine = new PointF(-convertedAccelValuesX, -convertedAccelValuesY);
        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
        	if (OxygenActivity.getPhysicsEngine() != null) {
        		OxygenActivity.getPhysicsEngine().setGravity(gravityForEngine);
        	}
        }
        // float[] magnetValues = mShakeDetector.magnetValues;
    }

    private void initializeShake() {
        mSensorManager = (SensorManager) OxygenActivity.getContext().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                Object.resetVelocities();
            }
        });
    }
}
