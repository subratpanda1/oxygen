package com.subrat.Oxygen.objects;

import android.graphics.Canvas;
import android.graphics.PointF;
import com.subrat.Oxygen.utilities.MathUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by subrat.panda on 07/05/15.
 */
public abstract class Object {
    protected int objectId;
    public int getObjectId() {
        return objectId;
    }

    protected ArrayList<Force> forceList = new ArrayList<Force>();

    private static PointF gravity = new PointF(0, 0);
    public static PointF getGravity() { return gravity; }
    public static void setGravity(PointF pointf) { gravity = pointf; }

    private static List<Object> objectList = Collections.synchronizedList(new ArrayList<Object>());
    public static synchronized List<Object> getObjectList() {
        return objectList;
    }

    public abstract boolean draw(Canvas canvas);
    public abstract boolean checkCollision(Object obj) throws Exception;
    public abstract void updateCollision(Object obj) throws Exception;
    public abstract void updatePosition();

    public static void updateAllObjects() {
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

    public static void resetVelocities() {
        for (Object object : Object.getObjectList()) {
            if (object instanceof Circle) {
                Circle circle = (Circle) object;
                circle.initVelocity();
            }
        }
    }
}
