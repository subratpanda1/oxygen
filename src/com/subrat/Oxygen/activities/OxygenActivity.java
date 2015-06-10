package com.subrat.Oxygen.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.subrat.Oxygen.R;
import com.subrat.Oxygen.backendRoutines.PhysicsEngine;
import com.subrat.Oxygen.backendRoutines.UpdateObjectsInAThread;
import com.subrat.Oxygen.customviews.OxygenView;
import com.subrat.Oxygen.objects.Object;
import com.subrat.Oxygen.utilities.Configuration;

/**
 * Created by subrat.panda on 07/05/15.
 */
public class OxygenActivity extends Activity {
    private static Context context = null;
    private static int canvasWidth = 0;    // In pixels
    private static int canvasHeight = 0;   // In pixels
	private static float worldWidth = 0;   // In meter
	private static float worldHeight = 0F; // In meter
	
	private static PhysicsEngine physicsEngine;

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
        
        if (Configuration.useLiquidFunPhysics()) {
        	physicsEngine = new PhysicsEngine();
        }

        context = this;

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

    public static Context getContext() { return context; }
    
    public static PhysicsEngine getPhysicsEngine() { return physicsEngine; }

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
        context = null;
        
        if (Configuration.useLiquidFunPhysics()) {
        	physicsEngine.clearWorld();
        }

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
    
    public static int getCanvasWidth() { return canvasWidth; }
    public static int getCanvasHeight() { return canvasHeight; }
    public static float getWorldWidth() { return worldWidth; }
    public static float getWorldHeight() { return worldHeight; }
    
    public static void setCanvasDimensions(int width, int height) { 
    	canvasWidth = width;
    	canvasHeight = height;
    	worldHeight = Configuration.getDefaultWorldHeight();
    	int pixelsPerMeter = (int)(canvasHeight / worldHeight);
    	worldWidth = (float)canvasWidth / (float)pixelsPerMeter;
    }
}

