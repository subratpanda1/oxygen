package com.subrat.Oxygen.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;

/**
 * Created by subrat.panda on 07/05/15.
 */
public class Circle extends Object {
    private PointF center;
    public PointF getCenter() { return center; }
    public void setCenter(PointF point) { center = point; }

    private float radius;
    public float getRadius() { return radius; }

    public float getMass() {
        return (float) (3.14F * Math.pow(radius, 2) * Configuration.getCircleDensity());
    }

    private PointF velocity;
    public PointF getVelocity() { return velocity; }
    public void setVelocity(PointF point) { velocity = point; }

    private Paint fillPainter = null;
    private Paint strokePainter = null;

    public Circle(PointF center, float radius) {
        this.center = center;
        this.radius = radius;

        initVelocity();
    }

    public void initVelocity() {
        velocity = new PointF();
        // velocity.x = MathUtils.getRandom(-Configuration.getMaxVelocity(), Configuration.getMaxVelocity());
        // velocity.y = (float) (Math.sqrt(Math.pow(Configuration.getMaxVelocity(), 2) - Math.pow(velocity.x, 2)) * MathUtils.getRandomSign());
        velocity.x = 0;
        velocity.y = 0;
    }

    private boolean isStill() {
        if (velocity.x == 0 && velocity.y == 0) return true;
        return false;
    }

    protected Paint getFillPainter() {
        if (fillPainter == null) {
            fillPainter = new Paint();
            fillPainter.setColor(Color.parseColor(MathUtils.getRandomColor()));
            fillPainter.setAntiAlias(true);
            fillPainter.setStyle(Paint.Style.FILL);
        }
        return fillPainter;
    }

    protected Paint getStrokePainter() {
        if (strokePainter == null) {
            strokePainter = new Paint();
            strokePainter.setColor(Color.parseColor(MathUtils.getRandomColor()));
            strokePainter.setAntiAlias(true);
            strokePainter.setStyle(Paint.Style.STROKE);
            strokePainter.setStrokeWidth(Configuration.getCircleBorder());
        }
        return strokePainter;
    }

    public boolean draw(Canvas canvas) {
        float canvasMargin = Configuration.getCanvasMargin();
        if (center.x < canvasMargin + radius) center.x = canvasMargin + radius;
        if (center.y < canvasMargin + radius) center.y = canvasMargin + radius;
        if (center.x > canvas.getWidth() - canvasMargin - radius) center.x = canvas.getWidth() - canvasMargin - radius;
        if (center.y > canvas.getHeight() - canvasMargin - radius) center.y = canvas.getHeight() - canvasMargin - radius;

        canvas.drawCircle(center.x, center.y, radius, getFillPainter());
        canvas.drawCircle(center.x, center.y, radius, getStrokePainter());
        return true;
    }

    public void updatePosition() {

        PointF acceleration = new PointF(0, 0);
        MathUtils.addToPoint(acceleration, getGravity());
        MathUtils.addToPoint(velocity, MathUtils.scalePoint(acceleration, Configuration.getRefreshInterval()));

        // PointF currentPos = MathUtils.clonePoint(getCenter());
        MathUtils.addToPoint(center, MathUtils.scalePoint(velocity, Configuration.getRefreshInterval()));

        /*
        // Check if it collides with anyone in the new position. If so, don't move to new position.
        try {
            for (Object object : Object.getObjectList()) {
                // System.out.println("Checking collision of " + getObjectId() + " with " + object.getObjectId());
                // System.out.println("Center: x: " + center.x + ", y: " + center.y);
                // System.out.println("Velocity: x: " + velocity.x + ", y: " + velocity.y);
                if (checkCollision(object)) {
                    // System.out.println("Found true");
                    center = currentPos;
                    break;
                } else {
                    System.out.println("Found false");
                }
            }
        } catch (Exception e) {

        }
        */

        // Stop the object if it is very slow
        if (Math.abs(velocity.x) < Configuration.getMinVelocity() && Math.abs(velocity.y) < Configuration.getMinVelocity()) {
            velocity.x = 0;
            velocity.y = 0;
            return;
        }
    }

    public boolean checkOverlap(Object object) {
        if (this.objectId == object.objectId) return false;
        if (object instanceof Circle) {
            Circle circle = (Circle) object;
            float threshold = MathUtils.getDistance(circle, this) - (circle.radius + this.radius);
            if (threshold < Configuration.getCollisionThreshold()) return true;
        } else if (object instanceof Line) {
            Line line = (Line) object;
            float threshold = MathUtils.getDistance(this, line) - this.radius;
            if (threshold < Configuration.getCollisionThreshold()) {
                return true;
            }
        }

        return false;
    }

    public boolean checkCollision(Object object) throws Exception {
        if (this.objectId == object.objectId) return false;
        if (object instanceof Circle) {
            Circle circle = (Circle) object;
            if (this.isStill() && circle.isStill()) return false;
            float threshold = MathUtils.getDistance(circle, this) - (circle.radius + this.radius);
            if (threshold < Configuration.getCollisionThreshold()) {
                // Check if circles get closer in next frame
                PointF newThisCenter = MathUtils.addPoint(this.center, this.velocity);
                PointF newObjectCenter = MathUtils.addPoint(circle.center, circle.velocity);
                float newThreshold = MathUtils.getDistance(newObjectCenter, newThisCenter) - (circle.radius + this.radius);
                if (newThreshold < threshold) {
                    return true;
                }
            }
        } else if (object instanceof Line) {
            if (this.isStill()) return false;
            Line line = (Line) object;
            float threshold = MathUtils.getDistance(this, line) - this.radius;
            if (threshold < Configuration.getCollisionThreshold()) {
                // Check if circles get closer in next frame
                PointF newThisCenter = MathUtils.addPoint(this.center, this.velocity);
                float newThreshold = MathUtils.getDistance(newThisCenter, line) - this.radius;
                if (newThreshold < threshold) {
                    return true;
                }
            }
        } else {
            throw (new Exception("Collision between " + this.getClass().toString() + " and " + object.getClass().toString() + " could not be handled."));
        }

        return false;
    }

    public void updateCollision(Object object) throws Exception {
        if (this.objectId == object.objectId) return;
        System.out.println("Before Velocity: x: " + velocity.x + ", y: " + velocity.y);
        if (object instanceof Circle) {
            Circle circle = (Circle) object;
            updateCircleToCircleCollisionVelocity(circle);
        } else if (object instanceof Line) {
            Line line = (Line) object;
            updateCircleToLineCollisionVelocity(line);
        } else {
            throw (new Exception("Collision between " + this.getClass().toString() + " and " + object.getClass().toString() + " could not be handled."));
        }
    }

    private static PointF computeOneDimensionalCollisionVelocities(PointF mass, PointF velocity) {
        float v1 = velocity.x;
        float v2 = velocity.y;

        float m1 = mass.x;
        float m2 = mass.y;

        float e = Configuration.getRestitution();

        float V1 = ( (m1 * v1 + m2 * v2) - m2 * e * (v1 - v2) ) / (m1 + m2);
        float V2 = ( (m1 * v1 + m2 * v2) + m1 * e * (v1 - v2) ) / (m1 + m2);

        return new PointF(V1, V2);
    }

    public void updateCircleToCircleCollisionVelocity(Circle b) {
        if (this.getCenter().x == b.getCenter().x) {
            // Circles are vertically aligned
            PointF finalVelocity = computeOneDimensionalCollisionVelocities(new PointF(this.getMass(), b.getMass()),
                                                                            new PointF(this.getVelocity().y, b.getVelocity().y));
            this.getVelocity().y = finalVelocity.x;
            b.getVelocity().y = finalVelocity.y;
        } else if (this.getCenter().y == b.getCenter().y) {
            // Circles are horizontally aligned
            PointF finalVelocity = computeOneDimensionalCollisionVelocities(new PointF(this.getMass(), b.getMass()),
                                                                            new PointF(this.getVelocity().x, b.getVelocity().x));
            this.getVelocity().x = finalVelocity.x;
            b.getVelocity().x = finalVelocity.y;
        } else {
            try {
                float slope = (-1) / MathUtils.getSlope(this.getCenter(), b.getCenter());
                float sinTheta = (float) (slope / Math.sqrt(1 + Math.pow(slope, 2)));
                float cosTheta = (float) (1 / Math.sqrt(1 + Math.pow(slope, 2)));

                // Calculate projected velocities with the collision tangent as x axis
                float projectedVelocityXOfA = this.getVelocity().x * cosTheta + this.getVelocity().y * sinTheta;
                float projectedVelocityYOfA = this.getVelocity().y * cosTheta - this.getVelocity().x * sinTheta;

                float projectedVelocityXOfB = b.getVelocity().x * cosTheta + b.getVelocity().y * sinTheta;
                float projectedVelocityYOfB = b.getVelocity().y * cosTheta - b.getVelocity().x * sinTheta;

                // Compute post-collision velocities
                PointF finalVelocity = computeOneDimensionalCollisionVelocities(new PointF(this.getMass(), b.getMass()),
                        new PointF(projectedVelocityYOfA, projectedVelocityYOfB));
                projectedVelocityYOfA = finalVelocity.x;
                projectedVelocityYOfB = finalVelocity.y;

                // Calculate back projected velocities to normal axis
                this.getVelocity().x = projectedVelocityXOfA * cosTheta - projectedVelocityYOfA * sinTheta;
                this.getVelocity().y = projectedVelocityYOfA * cosTheta + projectedVelocityXOfA * sinTheta;

                b.getVelocity().x = projectedVelocityXOfB * cosTheta - projectedVelocityYOfB * sinTheta;
                b.getVelocity().y = projectedVelocityYOfB * cosTheta + projectedVelocityXOfB * sinTheta;
            } catch (Exception e) {
                // Infinite slope
            }
        }

        // resolveOverlap(b);
    }

    public void resolveOverlap(Circle b) {
        if (!checkOverlap(b)) return;

        float distance = MathUtils.getDistance(this, b);
        float overlap = distance - (this.radius + b.radius);
        float shift = overlap / 2;

        float sinTheta = (b.getCenter().y - this.getCenter().y) / distance;
        float cosTheta = (b.getCenter().x - this.getCenter().x) / distance;

        this.getCenter().x -= shift * cosTheta;
        this.getCenter().y -= shift * sinTheta;

        b.getCenter().x += shift * cosTheta;
        b.getCenter().y += shift * sinTheta;
    }

    public void updateCircleToLineCollisionVelocity(Line line) {
        if (line.getStart().x == line.getEnd().x) {
            // Vertical Line
            this.getVelocity().x = -1F * Configuration.getRestitution() * this.getVelocity().x;
        } else if (line.getStart().y == line.getEnd().y) {
            // Horizontal Line
            this.getVelocity().y = -1F * Configuration.getRestitution() * this.getVelocity().y;
        } else {
            try {
                float slope = MathUtils.getSlope(line.getEnd(), line.getStart());
                float sinTheta = (float) (slope / Math.sqrt(1 + Math.pow(slope, 2)));
                float cosTheta = (float) (1 / Math.sqrt(1 + Math.pow(slope, 2)));

                // Calculate projected velocities with the collision tangent as x axis
                float projectedVelocityX = this.getVelocity().x * cosTheta + this.getVelocity().y * sinTheta;
                float projectedVelocityY = this.getVelocity().y * cosTheta - this.getVelocity().x * sinTheta;

                // Compute post-collision velocities
                projectedVelocityY = -1F * Configuration.getRestitution() * projectedVelocityY;

                // Calculate back projected velocities to normal axis
                this.getVelocity().x = projectedVelocityX * cosTheta - projectedVelocityY * sinTheta;
                this.getVelocity().y = projectedVelocityY * cosTheta + projectedVelocityX * sinTheta;
            } catch (Exception e) {
                // Infinite slope
            }
        }
    }
}
