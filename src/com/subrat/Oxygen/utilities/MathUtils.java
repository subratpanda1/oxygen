package com.subrat.Oxygen.utilities;

import android.content.res.Resources;
import android.graphics.PointF;

import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.objects.Circle;
import com.subrat.Oxygen.objects.Line;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by subrat.panda on 07/05/15.
 */
public class MathUtils {
    private static Random r = new Random();

    public static Resources resources = null;

    public static float getRandom(float begin, float end) {
        float number = r.nextFloat() * (end - begin) + begin;
        return number;
    }

    public static int getRandom(int begin, int end) {
        int number = r.nextInt(end - begin) + begin;
        return number;
    }

    public static int getRandomSign() {
        return (getRandom(-1.0F, 1.0F) > 0) ? 1 : -1;
    }

    public static float getDistance(PointF a, PointF b) {
        return (float) Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
    }

    public static float getDistance(PointF a, Line b) {
        float term1 = (b.getEnd().x - b.getStart().x);
        float term2 = (b.getStart().x - a.x);
        float term3 = (b.getEnd().y - b.getStart().y);
        float term4 = (b.getStart().y - a.y);

        float numerator = Math.abs(term1 * term4 - term2 * term3);
        float denominator = (float) Math.sqrt(Math.pow(term1, 2) + Math.pow(term3, 2));

        return numerator / denominator;
    }

    public static float getDistance(Circle a, Line b) {
        return getDistance(a.getCenter(), b);
    }
    
    public static float getRadian(PointF a, PointF b) {
    	return (float)Math.atan2(b.y - a.y, b.x - a.x);
    }

    public static float getSlope(PointF a, PointF b) {
        if (a.x == b.x) { return (float) 0xFFFFFFFF; } // Infinite slope case
        return (a.y - b.y) / (a.x - b.x);
    }

    public static float getSinTheta(PointF a, PointF b) {
        float distance = getDistance(a, b);
        if (distance == 0) return 0;
        return (b.y - a.y) / distance;
    }

    public static float getCosTheta(PointF a, PointF b) {
        float distance = getDistance(a, b);
        if (distance == 0) return 1;
        return (b.x - a.x) / distance;
    }

    public static void addToPoint(PointF a, PointF b) {
        a.x += b.x;
        a.y += b.y;
    }

    public static float getAbsolute(PointF point) {
        return (float)Math.sqrt(Math.pow(point.x, 2) + Math.pow(point.y, 2));
    }

    public static PointF scalePoint(PointF a, float scale) {
        return new PointF(a.x * scale, a.y * scale);
    }

    public static PointF addPoint(PointF a, PointF b) {
        return new PointF(a.x + b.x, a.y + b.y);
    }

    public static PointF diffPoint(PointF a, PointF b) {
        return new PointF(a.x - b.x, a.y - b.y);
    }

    public static String getRandomColor() {
        int average = 0;
        int intColor = 0;

        // Try to get brighter colors
        while (average < 80) {
            intColor = Math.abs(r.nextInt());
            average = (((intColor & 0xFF0000) >> 16) + ((intColor & 0x00FF00) >> 8) + ((intColor & 0x0000FF))) / 3;
        }

        String hexColor = String.format("#%06X", (0xFFFFFF & intColor));
        return hexColor;
    }



    public static PointF clonePoint(PointF point) {
        return new PointF(point.x, point.y);
    }

    public static PointF transformPointToAxis(PointF point, Line line) {
        PointF start = line.getStart();
        PointF end = line.getEnd();
        float sinTheta = getSinTheta(start, end);
        float cosTheta = getCosTheta(start, end);

        // First do linear transform of axes from origin to start
        float tmpX = point.x - start.x;
        float tmpY = point.y - start.y;

        // Now rotate axis to align x axis along the given line with origin being at start
        float tmpXX = tmpX * cosTheta + tmpY * sinTheta;
        float tmpYY = tmpY * cosTheta - tmpX * sinTheta;

        return new PointF(tmpXX, tmpYY);
    }

    public static PointF transformPointFromAxis(PointF point, Line line) {
        PointF start = line.getStart();
        PointF end = line.getEnd();
        float sinTheta = getSinTheta(start, end);
        float cosTheta = getCosTheta(start, end);

        // Now rotate axis back to global axis
        float tmpX = point.x * cosTheta - point.y * sinTheta;
        float tmpY = point.y * cosTheta + point.x * sinTheta;

        // First do linear transform of axes from start to origin
        float tmpXX = tmpX + start.x;
        float tmpYY = tmpY + start.y;

        return new PointF(tmpXX, tmpYY);
    }

    public static float getMean(ArrayList<Float> dataList) {
        if (dataList.isEmpty()) return 0;
        float sum = 0;
        for (float data : dataList) {
            sum += data;
        }

        return sum / dataList.size();
    }

    public static float getStandardDeviation(ArrayList<Float> dataList, float mean) {
        if (dataList.isEmpty()) return 0;
        float sum = 0;
        for (float data : dataList) {
            sum += Math.pow(data - mean, 2);
        }

        return (float)Math.sqrt(sum / dataList.size());
    }

    public static float getStandardDeviation(ArrayList<Float> dataList) {
        return getStandardDeviation(dataList, getMean(dataList));
    }
    
    public static float getPI() { return 3.141F; }

    public static String getPointString(PointF point) {
        return new String("(" + point.x + ", " + point.y + ")");
    }
    
	public static int getPixelFromMeter(float meter) {
    	int pixelsPerMeter = (int)(OxygenActivity.getCanvasHeight() / OxygenActivity.getWorldHeight());
    	int pixels = (int)(meter * pixelsPerMeter);
    	if (pixels < 1) pixels = 1;
		return pixels;
	}
	
	public static float getMeterFromPixel(int pixel) {
    	int pixelsPerMeter = (int)(OxygenActivity.getCanvasHeight() / OxygenActivity.getWorldHeight());
		return ((float)pixel / (float)pixelsPerMeter);
	}
	
	public static PointF getPixelBasedPointFromMeterBasedPoint(PointF point) {
		return new PointF(getPixelFromMeter(point.x), getPixelFromMeter(point.y));
	}

	public static PointF getMeterBasedPointFromPixelBasedPoint(PointF point) {
		return new PointF(getMeterFromPixel((int)point.x), getMeterFromPixel((int)point.y));
	}
	
	public static void transformToMeterBasedPoints(ArrayList<PointF> points) {
		for (PointF point : points) {
			point.x = getMeterFromPixel((int)point.x);
			point.y = getMeterFromPixel((int)point.y);
		}
	}
	
	public static void transformToPixelBasedPoints(ArrayList<PointF> points) {
		for (PointF point : points) {
			point.x = getPixelFromMeter((int)point.x);
			point.y = getPixelFromMeter((int)point.y);
		}
	}
}
