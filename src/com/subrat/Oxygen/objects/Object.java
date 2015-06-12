package com.subrat.Oxygen.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import java.util.ArrayList;

import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;

/**
 * Created by subrat.panda on 07/05/15.
 */
public abstract class Object {
    private int objectId;
    public int getObjectId() { return objectId; }
    public void setObjectId(int id) { objectId = id; }

    protected ArrayList<Force> forceList = new ArrayList<Force>();

    private static ArrayList<Object> objectList = new ArrayList<Object>();
    public static ArrayList<Object> getObjectList() { return objectList; }

    private static ArrayList<Circle> particleList = new ArrayList<Circle>();
    public static ArrayList<Circle> getParticleList() { return particleList; }
    
    private static Paint waterPainter = null;
    
    protected static Paint getWaterPainter() {
        if (waterPainter == null) {
            waterPainter = new Paint();
            waterPainter.setColor(Color.CYAN);
            waterPainter.setStyle(Style.STROKE);
            waterPainter.setStrokeWidth(MathUtils.getPixelFromMeter(Configuration.PARTICLE_RADIUS / 4));
        }
        return waterPainter;
    }
    
    public static void drawParticles(Canvas canvas) {
    	float[] points = new float[2 * particleList.size()];
    	int i = 0;
    	for (Circle circle : particleList) {
    		points[i++] = MathUtils.getPixelFromMeter(circle.getCenter().x);
    		points[i++] = MathUtils.getPixelFromMeter(circle.getCenter().y);
    	}
    	
    	canvas.drawPoints(points, getWaterPainter());
    	// canvas.drawVertices(Canvas.VertexMode.TRIANGLES, points.length, points, 0, null, 0, null, 0, null, 0, 0, getWaterPainter());
    }

    public abstract boolean draw(Canvas canvas);
    public abstract boolean checkCollision(Object obj) throws Exception;
    public abstract void updateCollision(Object obj) throws Exception;
    public abstract void updatePosition();

    public static void updateAllObjects() {
    	if (Configuration.USE_LIQUIDFUN_PHYSICS) {
			if (OxygenActivity.getPhysicsEngine() != null) {
				OxygenActivity.getPhysicsEngine().stepWorld();
				for (Object object : Object.getObjectList()) {
					if (object instanceof Circle) {
						OxygenActivity.getPhysicsEngine().updateCircle((Circle) object);
					} else if (object instanceof Line) {
						OxygenActivity.getPhysicsEngine().updateLine((Line) object);
					}
				}

				OxygenActivity.getPhysicsEngine().updateParticles(particleList);
			}
    	} else {
			try {
				for (Object object1 : Object.getObjectList()) {
					for (Object object2 : Object.getObjectList()) {
						if (object2.getObjectId() > object1.getObjectId()) {
							if (object1.checkCollision(object2)) {
								object1.updateCollision(object2);
							}
						}
					}
				}
			} catch (Exception e) {
			}

			for (Object object : Object.getObjectList()) {
				object.updatePosition();
			}
    	}
    }

    public static void resetVelocities() {
        for (Object object : Object.getObjectList()) {
            if (object instanceof Circle) {
                Circle circle = (Circle) object;
                circle.initRandomVelocity();
            }
        }
    }
}
