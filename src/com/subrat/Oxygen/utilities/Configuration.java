package com.subrat.Oxygen.utilities;

/**
 * Created by subrat.panda on 08/05/15.
 */
public class Configuration {
    private final static float MAX_VELOCITY = 0.5F; // In mtr per sec
    private final static float MIN_VELOCITY = 0.1F; // In mtr per sec

    private final static float REFRESH_INTERVAL = 0.02F; // In sec
    private final static float COLLISION_THRESHOLD = 0.001F; // In mtr
    private final static float CIRCLE_BORDER = 0.05F; // In mtr
    private final static float LINE_THICKNESS = 0.03F; // In mtr
    private final static float CANVAS_MARGIN = 0.05F; // In mtr

    private final static float CIRCLE_DENSITY = 1F; // In Kg per mtr per mtr
    private final static float RESTITUTION = 0.8F;
    private final static float LINE_DEVIATION_THRESHOLD = 40F;
    private final static float CIRCLE_DEVIATION_THRESHOLD = 0.1F;
    private final static int   LINE_MIN_PIXELS = 10;
    private final static int   CIRCLE_MIN_PIXELS = 10;
    private final static float LINE_MIN_LENGTH = 0.1F; // In mtr
    private final static float CIRCLE_MIN_RADIUS = 0.1F; // In dp

    private final static float DEFAULT_WORLD_HEIGHT = 5F; // In mtr
    
    private final static boolean USE_LIQUIDFUN_PHYSICS = true;

    public static float getMinVelocity() { return MIN_VELOCITY; }
    public static float getMaxVelocity() { return MAX_VELOCITY; }

    public static float getRefreshInterval() { return REFRESH_INTERVAL; }

    public static float getCollisionThreshold() { return COLLISION_THRESHOLD; }

    public static float getCircleBorder() { return CIRCLE_BORDER; }
    public static float getLineThickness() { return LINE_THICKNESS; }
    public static float getCircleDensity() { return CIRCLE_DENSITY; }
    public static float getCanvasMargin() { return CANVAS_MARGIN; }

    public static float getRestitution() { return RESTITUTION; }

    public static float getLineDeviationThreshold() { return LINE_DEVIATION_THRESHOLD; }
    public static float getCircleDeviationThreshold() { return CIRCLE_DEVIATION_THRESHOLD; }
    public static int getLineMinPixels() { return LINE_MIN_PIXELS; }
    public static int getCircleMinPixels() { return CIRCLE_MIN_PIXELS; }
    public static float getLineMinLength() { return LINE_MIN_LENGTH; }
    public static float getCircleMinRadius() { return CIRCLE_MIN_RADIUS; }
    public static float getDefaultWorldHeight() { return DEFAULT_WORLD_HEIGHT; }
    
    public static boolean useLiquidFunPhysics() { return USE_LIQUIDFUN_PHYSICS; }
}
