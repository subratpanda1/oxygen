package com.subrat.Oxygen.objects;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.utilities.Configuration;

/**
 * Created by subrat.panda on 07/05/15.
 */
public abstract class Object {
    private int objectId;
    public int getObjectId() { return objectId; }
    public void setObjectId(int id) { objectId = id; }

    protected ArrayList<Force> forceList = new ArrayList<Force>();

    private static List<Object> objectList = Collections.synchronizedList(new ArrayList<Object>());
    public static synchronized List<Object> getObjectList() { return objectList; }

    public abstract boolean draw(Canvas canvas);
    public abstract boolean checkCollision(Object obj) throws Exception;
    public abstract void updateCollision(Object obj) throws Exception;
    public abstract void updatePosition();

    public static void updateAllObjects() {
    	if (Configuration.USE_LIQUIDFUN_PHYSICS) {
    		OxygenActivity.getPhysicsEngine().stepWorld();
    		for (Object object : Object.getObjectList()) {
    			if (object instanceof Circle) {
    				OxygenActivity.getPhysicsEngine().updateCircle((Circle)object);
    			} else if (object instanceof Line) {
    				OxygenActivity.getPhysicsEngine().updateLine((Line)object);
    			}
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
