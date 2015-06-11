package com.subrat.Oxygen.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.subrat.Oxygen.activities.OxygenActivity;
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

    private void setEndPoints(PointF start, PointF end) {
    	this.start = start; this.end = end;
    }

    static Paint linePainter = null;

    private Line(PointF start, PointF end) {
        setEndPoints(start, end);
    }

    protected Paint getLinePainter() {
        if (linePainter == null) {
            linePainter = new Paint();
            linePainter.setColor(Color.YELLOW);
            linePainter.setAntiAlias(true);
            linePainter.setStyle(Paint.Style.STROKE);
            linePainter.setStrokeWidth(MathUtils.getPixelFromMeter(Configuration.LINE_THICKNESS));
        }

        return linePainter;
    }

    public boolean draw(Canvas canvas) {
    	PointF startPixel = MathUtils.getPixelBasedPointFromMeterBasedPoint(start);
    	PointF endPixel = MathUtils.getPixelBasedPointFromMeterBasedPoint(end);
        canvas.drawLine(startPixel.x, startPixel.y, endPixel.x, endPixel.y, getLinePainter());
        return true;
    }

    public static boolean detectLine(ArrayList<PointF> points) {
        if (points.size() < Configuration.LINE_MIN_PIXELS) return false;

        PointF start = points.get(0);
        PointF end = points.get(points.size() - 1);

        float lineLength = MathUtils.getDistance(start, end);
        if (lineLength < Configuration.LINE_MIN_LENGTH) return false;

        // Check if bounding box is thin enough to be approximated by a line
        Line line = Line.getTemporaryLine(start, end);
        ArrayList<Float> distanceList = new ArrayList<Float>();
        for (PointF point : points) {
        	distanceList.add(MathUtils.getDistance(point, line));
        }
        
        float meanDistance = MathUtils.getMean(distanceList);
        if ((lineLength / meanDistance) < Configuration.LINE_DEVIATION_THRESHOLD) return false;

        return true;
    }

    public static Line getLine(ArrayList<PointF> points) {
    	Line line = new Line(points.get(0), points.get(points.size() - 1));
        line.setObjectId(ObjectBuilder.getNextObjectId());
        Object.getObjectList().add(line);
        
        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
        	OxygenActivity.getPhysicsEngine().createLine(line);
        }
        
        return line;
    }
    
    public static Line getLine(PointF start, PointF end) {
    	Line line = new Line(start, end);
        line.setObjectId(ObjectBuilder.getNextObjectId());
        Object.getObjectList().add(line);
        
        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
        	OxygenActivity.getPhysicsEngine().createLine(line);
        }
        
        return line;
    }
    
    public static Line getTemporaryLine(PointF start, PointF end) {
    	return new Line(start, end);
    }
    
    public void editLine(PointF start, PointF end) {
    	setEndPoints(start, end);
        
    	if (Configuration.USE_LIQUIDFUN_PHYSICS) {
    		OxygenActivity.getPhysicsEngine().editLine(this);
    	}
    }
    
    public void updatePosition() {

    }

    public boolean checkCollision(Object object) throws Exception {
        if (this.getObjectId() == object.getObjectId()) return false;
        if (object instanceof Circle) {
            return object.checkCollision(this);
        }

        return false;
    }

    public void updateCollision(Object object) throws Exception {
        if (this.getObjectId() == object.getObjectId()) return;
        if (object instanceof Circle) {
            object.updateCollision(this);
        }
    }
}
