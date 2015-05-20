package com.subrat.Oxygen.utilities;

/**
 * Created by subrat.panda on 08/05/15.
 */
public class Configuration {
    private final static float MAX_VELOCITY = 0.4F;
    private final static float MIN_VELOCITY = 0.002F;

    private final static int REFRESH_INTERVAL = 10;
    private final static float COLLISION_THRESHOLD = 3F;
    private final static float OVERLAP_THRESHOLD = 10F;
    private final static float CIRCLE_BORDER = 3F;
    private final static float CIRCLE_RADIUS = 30F;
    private final static float LINE_THICKNESS = 3F;
    private final static float CIRCLE_DENSITY = 1F;
    private final static float RESTITUTION = 0.9F;
    private final static float CANVAS_MARGIN = 10F;

    private final static float GRAVITY_SCALE = 0.001F;
    private final static float REFRESH_RATE_SCALE = 1F;

    private static float getRefreshRateScale() {
        return REFRESH_RATE_SCALE * REFRESH_INTERVAL;
    }

    public static float getMaxVelocity() {
        return MathUtils.getPixelFromMM(MAX_VELOCITY * getRefreshRateScale());
    }

    public static float getMinVelocity() {
        return MathUtils.getPixelFromMM(MIN_VELOCITY * getRefreshRateScale());
    }

    public static int getRefreshInterval() {
        return REFRESH_INTERVAL;
    }

    public static float getCollisionThreshold() {
        return MathUtils.getPixelFromMM(COLLISION_THRESHOLD);
    }

    public static float getOverlapThreshold() {
        return MathUtils.getPixelFromMM(OVERLAP_THRESHOLD);
    }

    public static float getCircleBorder() {
        return MathUtils.getPixelFromMM(CIRCLE_BORDER);
    }

    public static float getCircleRadius() {
        return MathUtils.getPixelFromMM(CIRCLE_RADIUS);
    }

    public static float getLineThickness() {
        return MathUtils.getPixelFromMM(LINE_THICKNESS);
    }

    public static float getCircleDensity() {
        return CIRCLE_DENSITY;
    }

    public static float getRestitution() {
        return RESTITUTION;
    }

    public static float getCanvasMargin() {
        return MathUtils.getPixelFromMM(CANVAS_MARGIN);
    }

    public static float getGravityScale() {
        return MathUtils.getPixelFromMM(GRAVITY_SCALE);
    }
}
