package com.subrat.Oxygen.utilities;

/**
 * Created by subrat.panda on 08/05/15.
 */
public class Configuration {
    public final static float MAX_VELOCITY = 0.5F; // In mtr per sec
    public final static float MIN_VELOCITY = 0.1F; // In mtr per sec

    public final static float REFRESH_INTERVAL = 0.03F; // In sec
    public final static float COLLISION_THRESHOLD = 0.001F; // In mtr
    public final static float CIRCLE_BORDER = 0.05F; // In mtr
    public final static float LINE_THICKNESS = 0.03F; // In mtr
    public final static float CANVAS_MARGIN = 0.05F; // In mtr

    public final static float CIRCLE_DENSITY = 1F; // In Kg per mtr per mtr
    public final static float RESTITUTION = 0.8F;
    public final static float LINE_DEVIATION_THRESHOLD = 40F;
    public final static float CIRCLE_DEVIATION_THRESHOLD = 0.4F;
    public final static int   LINE_MIN_PIXELS = 10;
    public final static int   CIRCLE_MIN_PIXELS = 10;
    public final static float LINE_MIN_LENGTH = 0.1F; // In mtr
    public final static float CIRCLE_MIN_RADIUS = 0.1F; // In dp

    public final static float DEFAULT_WORLD_HEIGHT = 5F; // In mtr
    
    public final static boolean USE_LIQUIDFUN_PHYSICS = true;
    
    public static final int VELOCITY_ITERATIONS = 3;
    public static final int POSITION_ITERATIONS = 3;
    public static final int PARTICLE_ITERATIONS = 2;
    
    public static final int MAX_PARTICLE_COUNT = 15000;
    public static final float PARTICLE_RADIUS = 0.1F;
    public static final float PARTICLE_DAMPING = 0.5F;
    public static final float PARTICLE_DENSITY = 3F;
    public static final float PARTICLE_REPULSIVE_STRENGTH = 0.5F;
}
