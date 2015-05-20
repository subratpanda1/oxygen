package com.subrat.Oxygen.objects;

import android.graphics.Canvas;
import android.graphics.PointF;
import com.subrat.Oxygen.utilities.Configuration;

import java.util.ArrayList;

/**
 * Created by subrat.panda on 07/05/15.
 */
public class ObjectBuilder {
    static int objectIdCounter = 0;

    public static Object buildLine(PointF start, PointF end) {
        Object line = new Line(start, end);
        Object.getObjectList().add(line);
        line.objectId = ++objectIdCounter;
        return line;
    }

    public static Object buildObject(ArrayList<PointF> points) {
        if (Line.detectLine(points)) {
            Line line = new Line(points.get(0), points.get(points.size() - 1));
            Object.getObjectList().add(line);
            return line;
        } else {
            float avgx = 0, avgy = 0;
            for (PointF point : points) {
                avgx += point.x;
                avgy += point.y;
            }

            avgx /= points.size();
            avgy /= points.size();

            PointF center = new PointF(avgx, avgy);
            float radius = Configuration.getCircleRadius();

            Circle circle = new Circle(center, radius);

            // Do not create if overlapping with other circles
            for (Object object : Object.getObjectList()) {
                if (circle.checkOverlap(object)) {
                    return null;
                }
            }

            Object.getObjectList().add(circle);
            circle.objectId = ++objectIdCounter;
            return circle;
        }
    }

    public static void createOrUpdateBoundaryLines(float width, float height) {
        float canvasMargin = Configuration.getCanvasMargin();
        PointF topLeft = new PointF(canvasMargin, canvasMargin);
        PointF topRight = new PointF(width - canvasMargin, canvasMargin);
        PointF bottomLeft = new PointF(canvasMargin, height - canvasMargin);
        PointF bottomRight = new PointF(width - canvasMargin, height - canvasMargin);

        PointF topLeftMid = new PointF(500, 800);
        PointF bottomRightMid = new PointF(700, 800);

        if (Object.getObjectList().isEmpty()) {
            buildLine(topLeft, topRight);
            buildLine(bottomLeft, bottomRight);
            buildLine(topLeft, bottomLeft);
            buildLine(topRight, bottomRight);
            // buildLine(topLeftMid, bottomRightMid);
        } else {
            ((Line) Object.getObjectList().get(0)).setEndPoints(topLeft, topRight);
            ((Line) Object.getObjectList().get(1)).setEndPoints(bottomLeft, bottomRight);
            ((Line) Object.getObjectList().get(2)).setEndPoints(topLeft, bottomLeft);
            ((Line) Object.getObjectList().get(3)).setEndPoints(topRight, bottomRight);
            // ((Line) Object.getObjectList().get(4)).setEndPoints(topLeftMid, bottomRightMid);
        }
    }
}
