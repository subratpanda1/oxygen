package com.subrat.Oxygen.utilities;

/**
 * Created by subrat.panda on 08/05/15.
 */
public class Configuration {
    private final static float MAX_VELOCITY = 0.4F; // In dp per msec
    private final static float MIN_VELOCITY = 0.1F; // In dp per msec

    private final static int   REFRESH_INTERVAL = 5; // In msec
    private final static float COLLISION_THRESHOLD = 3F; // In dp
    private final static float CIRCLE_BORDER = 3F; // In dp
    private final static float CIRCLE_RADIUS = 30F; // In dp
    private final static float LINE_THICKNESS = 3F; // In dp
    private final static float CANVAS_MARGIN = 10F; // In dp

    private final static float CIRCLE_DENSITY = 1F;
    private final static float RESTITUTION = 0.8F;
    private final static float LINE_DEVIATION_THRESHOLD = 0.1F;
    private final static float CIRCLE_DEVIATION_THRESHOLD = 60F;
    private final static int   LINE_MIN_PIXELS = 10;
    private final static int   CIRCLE_MIN_PIXELS = 10;

    private final static float GRAVITY_SCALE = 0.0004F; // Convert mtr per sec per sec to dp per msec per msec

    public static float getMinVelocity() { return MathUtils.getPixelFromDP(MIN_VELOCITY); }
    public static float getMaxVelocity() { return MathUtils.getPixelFromDP(MAX_VELOCITY); }

    public static int getRefreshInterval() { return REFRESH_INTERVAL; }

    public static float getCollisionThreshold() { return MathUtils.getPixelFromDP(COLLISION_THRESHOLD); }

    public static float getCircleBorder() { return MathUtils.getPixelFromDP(CIRCLE_BORDER); }
    public static float getCircleRadius() { return MathUtils.getPixelFromDP(CIRCLE_RADIUS); }
    public static float getLineThickness() { return MathUtils.getPixelFromDP(LINE_THICKNESS); }
    public static float getCircleDensity() { return CIRCLE_DENSITY; }
    public static float getCanvasMargin() { return MathUtils.getPixelFromDP(CANVAS_MARGIN); }

    public static float getRestitution() { return RESTITUTION; }
    public static float getGravityScale() { return MathUtils.getPixelFromDP(GRAVITY_SCALE); }

    public static int getLineMinPixels() { return LINE_MIN_PIXELS; }
    public static int getCircleMinPixels() { return CIRCLE_MIN_PIXELS; }
    public static float getLineDeviationThreshold() { return LINE_DEVIATION_THRESHOLD; }
    public static float getCircleDeviationThreshold() { return CIRCLE_DEVIATION_THRESHOLD; }
}
