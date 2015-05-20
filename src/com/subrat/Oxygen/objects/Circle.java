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

    private static PointF gravity = new PointF(0, 0); // In pixels per msec per msec
    public static PointF getGravity() { return gravity; }
    public static void setGravity(PointF pointf) { gravity = pointf; }

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
        // Don't change velocity if acceleration is very low
        PointF velocityChange = MathUtils.scalePoint(getGravity(), Configuration.getRefreshInterval());
        if (Math.abs(velocityChange.x) > Configuration.getMinVelocity() || Math.abs(velocityChange.y) > Configuration.getMinVelocity()) {
            MathUtils.addToPoint(velocity, velocityChange);
        }

        // Don't change position if velocity is very low
        PointF positionChange = MathUtils.scalePoint(getVelocity(), Configuration.getRefreshInterval());
        if (Math.abs(positionChange.x) > Configuration.getMinVelocity() || Math.abs(positionChange.y) > Configuration.getMinVelocity()) {
            MathUtils.addToPoint(center, positionChange);
        }
    }

    public boolean checkOverlap(Object object) {
        if (this.objectId == object.objectId) return false;
        if (object instanceof Circle) {
            Circle circle = (Circle) object;
            float threshold = MathUtils.getDistance(circle.getCenter(), this.getCenter()) - (circle.radius + this.radius);
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
            float threshold = MathUtils.getDistance(circle.getCenter(), this.getCenter()) - (circle.radius + this.radius);
            if (threshold < Configuration.getCollisionThreshold()) {
                // Check if circles get closer in next frame
                PointF thisPositionChange = MathUtils.scalePoint(this.getVelocity(), Configuration.getRefreshInterval());
                PointF objectPositionChange = MathUtils.scalePoint(circle.getVelocity(), Configuration.getRefreshInterval());
                PointF newThisCenter = MathUtils.addPoint(this.center, thisPositionChange);
                PointF newObjectCenter = MathUtils.addPoint(circle.center, objectPositionChange);
                float newThreshold = MathUtils.getDistance(newObjectCenter, newThisCenter) - (circle.radius + this.radius);
                if (newThreshold < threshold) {
                    return true;
                }
            }
        } else if (object instanceof Line) {
            if (this.isStill()) return false;
            Line line = (Line) object;
            PointF positionChange = MathUtils.scalePoint(this.getVelocity(), Configuration.getRefreshInterval());
            PointF newThisCenter = MathUtils.addPoint(this.center, positionChange);

            // Detect if circle is within bounding rectangle of the line
            PointF transformedCenter = MathUtils.transformPointToAxis(this.getCenter(), line);
            float lineLength = MathUtils.getDistance(line.getStart(), line.getEnd());
            if (transformedCenter.x > 0 && transformedCenter.x < lineLength) {
                float threshold = Math.abs(transformedCenter.y) - this.radius;
                if (threshold < Configuration.getCollisionThreshold()) {
                    // Check if circles get closer in next frame
                    float newThreshold = MathUtils.getDistance(newThisCenter, line) - this.radius;
                    if (newThreshold < threshold) {
                        return true;
                    }
                }
            } else {
                // Detect if circle is going to hit the corner
                float threshold = MathUtils.getDistance(this.getCenter(), line.getStart()) - this.radius;
                if (threshold < Configuration.getCollisionThreshold()) {
                    // Check if circles get closer in next frame
                    float newThreshold = MathUtils.getDistance(newThisCenter, line.getStart()) - this.radius;
                    if (newThreshold < threshold) {
                        return true;
                    }
                }

                threshold = MathUtils.getDistance(this.getCenter(), line.getEnd()) - this.radius;
                if (threshold < Configuration.getCollisionThreshold()) {
                    // Check if circles get closer in next frame
                    float newThreshold = MathUtils.getDistance(newThisCenter, line.getEnd()) - this.radius;
                    if (newThreshold < threshold) {
                        return true;
                    }
                }
            }
        } else {
            throw (new Exception("Collision between " + this.getClass().toString() + " and " + object.getClass().toString() + " could not be handled."));
        }

        return false;
    }

    public void updateCollision(Object object) throws Exception {
        if (this.objectId == object.objectId) return;
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
            float sinTheta = MathUtils.getSinTheta(this.getCenter(), b.getCenter());
            float cosTheta = MathUtils.getCosTheta(this.getCenter(), b.getCenter());

            // Calculate projected velocities along the axes that go through centers of circles
            float projectedVelocityXOfA = this.getVelocity().x * cosTheta + this.getVelocity().y * sinTheta;
            float projectedVelocityYOfA = this.getVelocity().y * cosTheta - this.getVelocity().x * sinTheta;

            float projectedVelocityXOfB = b.getVelocity().x * cosTheta + b.getVelocity().y * sinTheta;
            float projectedVelocityYOfB = b.getVelocity().y * cosTheta - b.getVelocity().x * sinTheta;

            // Compute post-collision velocities
            PointF finalVelocity = computeOneDimensionalCollisionVelocities(new PointF(this.getMass(), b.getMass()),
                    new PointF(projectedVelocityXOfA, projectedVelocityXOfB));
            projectedVelocityXOfA = finalVelocity.x;
            projectedVelocityXOfB = finalVelocity.y;

            // Calculate back projected velocities to normal axis
            this.getVelocity().x = projectedVelocityXOfA * cosTheta - projectedVelocityYOfA * sinTheta;
            this.getVelocity().y = projectedVelocityYOfA * cosTheta + projectedVelocityXOfA * sinTheta;

            b.getVelocity().x = projectedVelocityXOfB * cosTheta - projectedVelocityYOfB * sinTheta;
            b.getVelocity().y = projectedVelocityYOfB * cosTheta + projectedVelocityXOfB * sinTheta;
        }
    }

    public void updateCircleToLineCollisionVelocity(Line line) {
        // Detect if circle is within bounding rectangle of the line
        PointF transformedCenter = MathUtils.transformPointToAxis(this.getCenter(), line);
        float lineLength = MathUtils.getDistance(line.getStart(), line.getEnd());
        if (transformedCenter.x > 0 && transformedCenter.x < lineLength) {
            float sinTheta = MathUtils.getSinTheta(line.getStart(), line.getEnd());
            float cosTheta = MathUtils.getCosTheta(line.getStart(), line.getEnd());

            // Calculate projected velocities with the collision tangent as x axis
            float projectedVelocityX = this.getVelocity().x * cosTheta + this.getVelocity().y * sinTheta;
            float projectedVelocityY = this.getVelocity().y * cosTheta - this.getVelocity().x * sinTheta;

            // Compute post-collision velocities (Note that velocity along projected Y axis will be inverted)
            projectedVelocityY = -1F * Configuration.getRestitution() * projectedVelocityY;

            // Calculate back projected velocities to normal axis
            this.getVelocity().x = projectedVelocityX * cosTheta - projectedVelocityY * sinTheta;
            this.getVelocity().y = projectedVelocityY * cosTheta + projectedVelocityX * sinTheta;
        } else {
            // In this case, circle is colliding with the end point of line. Treat it as a collision with line
            // orthogonal to line joining center of circle and end point of line.

            // If circle is going to hit the starting corner
            float threshold = MathUtils.getDistance(this.getCenter(), line.getStart()) - this.radius;
            if (threshold < Configuration.getCollisionThreshold()) {
                float sinTheta = MathUtils.getSinTheta(this.getCenter(), line.getStart());
                float cosTheta = MathUtils.getCosTheta(this.getCenter(), line.getStart());

                // Calculate projected velocities with the line joining circle center and endpoint as x axis
                float projectedVelocityX = this.getVelocity().x * cosTheta + this.getVelocity().y * sinTheta;
                float projectedVelocityY = this.getVelocity().y * cosTheta - this.getVelocity().x * sinTheta;

                // Compute post-collision velocities (Note that velocity along projected X axis will be inverted)
                projectedVelocityX = -1F * Configuration.getRestitution() * projectedVelocityX;

                // Calculate back projected velocities to normal axis
                this.getVelocity().x = projectedVelocityX * cosTheta - projectedVelocityY * sinTheta;
                this.getVelocity().y = projectedVelocityY * cosTheta + projectedVelocityX * sinTheta;
            }

            // If circle is going to hit the ending corner
            threshold = MathUtils.getDistance(this.getCenter(), line.getEnd()) - this.radius;
            if (threshold < Configuration.getCollisionThreshold()) {
                float sinTheta = MathUtils.getSinTheta(this.getCenter(), line.getEnd());
                float cosTheta = MathUtils.getCosTheta(this.getCenter(), line.getEnd());

                // Calculate projected velocities with the line joining circle center and endpoint as x axis
                float projectedVelocityX = this.getVelocity().x * cosTheta + this.getVelocity().y * sinTheta;
                float projectedVelocityY = this.getVelocity().y * cosTheta - this.getVelocity().x * sinTheta;

                // Compute post-collision velocities (Note that velocity along projected X axis will be inverted)
                projectedVelocityX = -1F * Configuration.getRestitution() * projectedVelocityX;

                // Calculate back projected velocities to normal axis
                this.getVelocity().x = projectedVelocityX * cosTheta - projectedVelocityY * sinTheta;
                this.getVelocity().y = projectedVelocityY * cosTheta + projectedVelocityX * sinTheta;
            }
        }
    }
}
