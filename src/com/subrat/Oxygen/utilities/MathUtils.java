package com.subrat.Oxygen.utilities;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.graphics.PointF;
import com.subrat.Oxygen.objects.Circle;
import com.subrat.Oxygen.objects.Line;

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

    public static float getSlope(PointF a, PointF b) {
        if (a.x == b.x) { return (float) 0xFFFFFFFF; } // Infinite slope case
        return (a.y - b.y) / (a.x - b.x);
    }

    public static float getSinTheta(PointF a, PointF b) {
        float distance = getDistance(a, b);
        return (b.y - a.y) / distance;
    }

    public static float getCosTheta(PointF a, PointF b) {
        float distance = getDistance(a, b);
        return (b.x - a.x) / distance;
    }

    public static void addToPoint(PointF a, PointF b) {
        a.x += b.x;
        a.y += b.y;
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

    public static float getPixelFromDP(float dp) {
        if (resources == null) return dp;
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        float px = dp * (displayMetrics.density);
        return px;
    }

    public static PointF clonePoint(PointF point) {
        return new PointF(point.x, point.y);
    }

    public static PointF transformPointToAxis(PointF point, Line line) {
        PointF start = line.getStart();
        PointF end = line.getEnd();
        float sinTheta = MathUtils.getSinTheta(start, end);
        float cosTheta = MathUtils.getCosTheta(start, end);

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
        float sinTheta = MathUtils.getSinTheta(start, end);
        float cosTheta = MathUtils.getCosTheta(start, end);

        // First do linear transform of axes from start to origin
        float tmpX = point.x + start.x;
        float tmpY = point.y + start.y;

        // Now rotate axis back to global axis
        float tmpXX = tmpX * cosTheta - tmpY * sinTheta;
        float tmpYY = tmpY * cosTheta + tmpX * sinTheta;

        return new PointF(tmpXX, tmpYY);
    }
}
