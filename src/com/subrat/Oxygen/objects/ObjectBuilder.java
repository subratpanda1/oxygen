package com.subrat.Oxygen.objects;

import android.graphics.PointF;

import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;

import java.util.ArrayList;

/**
 * Created by subrat.panda on 07/05/15.
 */
public class ObjectBuilder {
    private static int objectIdCounter = 0;
    public static int getNextObjectId() { return objectIdCounter++; }

    public static Object buildObject(ArrayList<PointF> points) {
    	MathUtils.transformToMeterBasedPoints(points);
        if (Line.detectLine(points)) {
            return Line.getLine(points);
        } 
        
        else if (Circle.detectCircle(points)) {
            return Circle.getCircle(points);
        }

        return null;
    }

    public static void createOrUpdateBoundaryLines() {
    	if (OxygenActivity.getContext() == null) return;
        float canvasMargin = Configuration.CANVAS_MARGIN;
        PointF topLeft = new PointF(canvasMargin, canvasMargin);
        PointF topRight = new PointF(OxygenActivity.getWorldWidth() - canvasMargin, canvasMargin);
        PointF bottomLeft = new PointF(canvasMargin, OxygenActivity.getWorldHeight() - canvasMargin);
        PointF bottomRight = new PointF(OxygenActivity.getWorldWidth() - canvasMargin, OxygenActivity.getWorldHeight() - canvasMargin);

        if (Object.getObjectList().isEmpty()) {
            Line.getLine(topLeft, topRight);
            Line.getLine(bottomLeft, bottomRight);
            Line.getLine(topLeft, bottomLeft);
            Line.getLine(topRight, bottomRight);
        } else {
            ((Line) Object.getObjectList().get(0)).editLine(topLeft, topRight);
            ((Line) Object.getObjectList().get(1)).editLine(bottomLeft, bottomRight);
            ((Line) Object.getObjectList().get(2)).editLine(topLeft, bottomLeft);
            ((Line) Object.getObjectList().get(3)).editLine(topRight, bottomRight);
        }
    }
}
