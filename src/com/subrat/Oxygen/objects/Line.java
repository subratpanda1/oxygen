package com.subrat.Oxygen.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;

import java.util.ArrayList;

/**
 * Created by subrat.panda on 07/05/15.
 */
public class Line extends Object {
    private PointF start;
    public PointF getStart() { return start; }

    private PointF end;
    public PointF getEnd() { return end; }

    public void setEndPoints(PointF start, PointF end) { this.start = start; this.end = end; }

    static Paint linePainter = null;

    public Line(PointF start, PointF end) {
        setEndPoints(start, end);
    }

    protected Paint getLinePainter() {
        if (linePainter == null) {
            linePainter = new Paint();
            linePainter.setColor(Color.YELLOW);
            linePainter.setAntiAlias(true);
            linePainter.setStyle(Paint.Style.STROKE);
            linePainter.setStrokeWidth(Configuration.getLineThickness());
        }

        return linePainter;
    }

    public boolean draw(Canvas canvas) {
        canvas.drawLine(start.x, start.y, end.x, end.y, getLinePainter());
        return true;
    }

    public static boolean detectLine(ArrayList<PointF> points) {
        if (points.size() < Configuration.getLineMinPixels()) return false;

        ArrayList<Float> slopeList = new ArrayList<Float>();
        PointF start = points.get(0);
        PointF end = points.get(points.size() - 1);

        if (MathUtils.getDistance(start, end) < Configuration.getLineMinLength()) return false;

        for (PointF point : points) {
            if (point == start) continue;
            slopeList.add(MathUtils.getSinTheta(start, point));
        }

        float standardDeviation = MathUtils.getStandardDeviation(slopeList);
        if (standardDeviation > Configuration.getLineDeviationThreshold()) return false;

        return true;
    }

    public static Line getLine(ArrayList<PointF> points) {
        return new Line(points.get(0), points.get(points.size() - 1));
    }

    public void updatePosition() {

    }

    public boolean checkCollision(Object object) throws Exception {
        if (this.objectId == object.objectId) return false;
        if (object instanceof Circle) {
            return object.checkCollision(this);
        }

        return false;
    }

    public void updateCollision(Object object) throws Exception {
        if (this.objectId == object.objectId) return;
        if (object instanceof Circle) {
            object.updateCollision(this);
        }
    }
}
