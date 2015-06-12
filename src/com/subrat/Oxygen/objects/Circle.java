package com.subrat.Oxygen.objects;

import android.graphics.*;

import com.subrat.Oxygen.R;
import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;

import java.util.ArrayList;

/**
 * Created by subrat.panda on 07/05/15.
 */
public class Circle extends Object {
    private PointF center; // In mtr
    public PointF getCenter() { return center; }
    public void setCenter(PointF point) { center = point; }

    private float radius; // In mtr
    public float getRadius() { return radius; }
    
    private int rotation; // In degree
    public int getRotation() { return rotation; }
    public void setRotation(int deg) { rotation = deg; }

    public float getMass() {
        return (float) (3.14F * Math.pow(radius, 2) * Configuration.CIRCLE_DENSITY);
    }

    private PointF velocity; // In mtr per sec
    public PointF getVelocity() { return velocity; }
    public void setVelocity(PointF point) { velocity = point; }

    private static PointF gravity = new PointF(0, 0); // In mtr per sec per sec
    public static PointF getGravity() { return gravity; }
    public static void setGravity(PointF pointf) { gravity = pointf; }

    private Paint fillPainter = null;
    private Paint strokePainter = null;
    Bitmap pic;
    
    private boolean isParticle = false;

    public Circle(PointF center, float radius) {
        this.center = center;
        this.radius = radius;
        this.rotation = 0;
        this.isParticle = false;

        initVelocity();
        initBitmap();
    }
    
    public Circle(PointF center, float radius, boolean isParticle) {
        this.center = center;
        this.radius = radius;
        this.isParticle = isParticle;
    	
        if (isParticle != true) {
        	initVelocity();
        	initBitmap();
    	}
    }
    
    private void initBitmap() {
        pic = BitmapFactory.decodeResource(OxygenActivity.getContext().getResources(), R.drawable.tennis_ball);
        pic = Bitmap.createScaledBitmap(pic, 2 * MathUtils.getPixelFromMeter(this.radius), 2 * MathUtils.getPixelFromMeter(this.radius),  true);
    }

    public void initVelocity() {
        velocity = new PointF();
        velocity.x = 0;
        velocity.y = 0;
    }

    public void initRandomVelocity() {
        velocity = new PointF();
        velocity.x = MathUtils.getRandom(-Configuration.MAX_VELOCITY, Configuration.MAX_VELOCITY);
        velocity.y = (float) (Math.sqrt(Math.pow(Configuration.MAX_VELOCITY, 2) - Math.pow(velocity.x, 2)) * MathUtils.getRandomSign());
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
            strokePainter.setStrokeWidth(Configuration.CIRCLE_BORDER);
        }
        return strokePainter;
    }
    
    public boolean draw(Canvas canvas) {
    	if (isParticle) {
    	} else {
    		int bitmapCornerX = MathUtils.getPixelFromMeter(this.getCenter().x - this.getRadius());
    		int bitmapCornerY = MathUtils.getPixelFromMeter(this.getCenter().y - this.getRadius());
    		
    		Matrix matrix = new Matrix();
    		// System.out.println("Drawing at angle: " + rotation);
    		matrix.setRotate(rotation, pic.getWidth()/2, pic.getHeight()/2);
    		matrix.postTranslate(bitmapCornerX, bitmapCornerY);
    		
    		// canvas.drawBitmap(pic, bitmapCornerX, bitmapCornerY, null);
    		canvas.drawBitmap(pic, matrix, null);
    	}
        return true;
    }

    public static boolean detectCircle(ArrayList<PointF> points) {
        if (points.size() < Configuration.CIRCLE_MIN_PIXELS) return false;

        // Check if standard deviation of all points from center is low
        float avgx = 0, avgy = 0;
        for (PointF point : points) {
            avgx += point.x;
            avgy += point.y;
        }

        avgx /= points.size();
        avgy /= points.size();

        PointF center = new PointF(avgx, avgy);

        ArrayList<Float> radiusList = new ArrayList<Float>();
        for (PointF point: points) {
            radiusList.add(MathUtils.getDistance(center, point));
        }

        float meanRadius = MathUtils.getMean(radiusList);
        if (meanRadius < Configuration.CIRCLE_MIN_RADIUS) return false;
        float standardDeviation = MathUtils.getStandardDeviation(radiusList, meanRadius);
        if (standardDeviation > Configuration.CIRCLE_DEVIATION_THRESHOLD) return false;

        // Do not create if overlapping with other circles
        Circle circle = constructCircle(points);
        for (Object object : Object.getObjectList()) {
            if (circle.checkOverlap(object)) {
                return false;
            }
        }

        return true;
    }
    
    private static Circle constructCircle(ArrayList<PointF> points) {
    	float avgx = 0, avgy = 0;
        for (PointF point : points) {
            avgx += point.x;
            avgy += point.y;
        }

        avgx /= points.size();
        avgy /= points.size();

        PointF center = new PointF(avgx, avgy);
        // float radius = Configuration.getCircleRadius();

        ArrayList<Float> radiusList = new ArrayList<Float>();
        for (PointF point: points) {
            radiusList.add(MathUtils.getDistance(center, point));
        }

        float radius = MathUtils.getMean(radiusList);
        
        Circle circle = new Circle(center, radius);
        return circle;
    }

    public static Circle getCircle(ArrayList<PointF> points) {
        Circle circle = constructCircle(points);
        circle.setObjectId(ObjectBuilder.getNextObjectId());
        Object.getObjectList().add(circle);
        
        if (Configuration.USE_LIQUIDFUN_PHYSICS) {
        	OxygenActivity.getPhysicsEngine().createCircle(circle);
        }
        
        return circle;
    }
    
    public void updatePosition() {
        // Don't change velocity if acceleration is very low
        // if (MathUtils.getAbsolute(this.getGravity()) > Configuration.getMinGravity()) {
             PointF velocityChange = MathUtils.scalePoint(getGravity(), Configuration.REFRESH_INTERVAL);
             MathUtils.addToPoint(velocity, velocityChange);
        // }
             
        // Don't change position if velocity is very low
        // if (MathUtils.getAbsolute(this.getVelocity()) > Configuration.getMinVelocity()) {
             PointF positionChange = MathUtils.scalePoint(getVelocity(), Configuration.REFRESH_INTERVAL);
             MathUtils.addToPoint(center, positionChange);
        // }
    }

    public boolean checkOverlap(Object object) {
        if (this.getObjectId() == object.getObjectId()) return false;
        if (object instanceof Circle) {
            Circle circle = (Circle) object;
            float threshold = MathUtils.getDistance(circle.getCenter(), this.getCenter()) - (circle.radius + this.radius);
            if (threshold < Configuration.COLLISION_THRESHOLD) return true;
        } else if (object instanceof Line) {
            Line line = (Line) object;
            float threshold = MathUtils.getDistance(this, line) - this.radius;
            if (threshold < Configuration.COLLISION_THRESHOLD) {
                return true;
            }
        }

        return false;
    }

    public boolean checkCollision(Object object) throws Exception {
        if (this.getObjectId() == object.getObjectId()) return false;
        if (object instanceof Circle) {
            Circle circle = (Circle) object;
            if (this.isStill() && circle.isStill()) return false;
            float threshold = MathUtils.getDistance(circle.getCenter(), this.getCenter()) - (circle.radius + this.radius);
            if (threshold < Configuration.COLLISION_THRESHOLD) {
                // Check if circles get closer in next frame
                PointF thisPositionChange = MathUtils.scalePoint(this.getVelocity(), Configuration.REFRESH_INTERVAL);
                PointF objectPositionChange = MathUtils.scalePoint(circle.getVelocity(), Configuration.REFRESH_INTERVAL);
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
            PointF positionChange = MathUtils.scalePoint(this.getVelocity(), Configuration.REFRESH_INTERVAL);
            PointF newThisCenter = MathUtils.addPoint(this.center, positionChange);

            // Detect if circle is within bounding rectangle of the line
            PointF transformedCenter = MathUtils.transformPointToAxis(this.getCenter(), line);
            float lineLength = MathUtils.getDistance(line.getStart(), line.getEnd());
            if (transformedCenter.x > 0 && transformedCenter.x < lineLength) {
                float threshold = Math.abs(transformedCenter.y) - this.radius;
                if (threshold < Configuration.COLLISION_THRESHOLD) {
                    // Check if circles get closer in next frame
                    float newThreshold = MathUtils.getDistance(newThisCenter, line) - this.radius;
                    if (newThreshold < threshold) {
                        return true;
                    }
                }
            } else {
                // Detect if circle is going to hit the corner
                float threshold = MathUtils.getDistance(this.getCenter(), line.getStart()) - this.radius;
                if (threshold < Configuration.COLLISION_THRESHOLD) {
                    // Check if circles get closer in next frame
                    float newThreshold = MathUtils.getDistance(newThisCenter, line.getStart()) - this.radius;
                    if (newThreshold < threshold) {
                        return true;
                    }
                }

                threshold = MathUtils.getDistance(this.getCenter(), line.getEnd()) - this.radius;
                if (threshold < Configuration.COLLISION_THRESHOLD) {
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
        if (this.getObjectId() == object.getObjectId()) return;
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

        float e = Configuration.RESTITUTION;

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

            // Also update circle position so that circle does not sink through the line
            // Update should be proportional to their speed
            float threshold = MathUtils.getDistance(this.getCenter(), b.getCenter()) - (this.radius + b.radius);
            if (threshold < 0) {
                float thisSpeed = MathUtils.getAbsolute(this.getVelocity());
                float bSpeed = MathUtils.getAbsolute(b.getVelocity());
                Line line = Line.getTemporaryLine(this.getCenter(), b.getCenter());
                PointF thisTransformedCenter = MathUtils.transformPointToAxis(this.getCenter(), line);
                PointF bTransformedCenter = MathUtils.transformPointToAxis(b.getCenter(), line);
                float thisShift = (thisSpeed / (thisSpeed + bSpeed)) * threshold; // threshold is -ve, so shift is -ve
                float bShift = (bSpeed / (thisSpeed + bSpeed)) * threshold * -1; // threshold is -ve, so shift is +ve

                thisTransformedCenter.x += thisShift;
                bTransformedCenter.x += bShift;
                PointF thisBackTransformedCenter = MathUtils.transformPointFromAxis(thisTransformedCenter, line);
                PointF bBackTransformedCenter = MathUtils.transformPointFromAxis(bTransformedCenter, line);
                this.setCenter(thisBackTransformedCenter);
                b.setCenter(bBackTransformedCenter);
            }
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
            projectedVelocityY = -1F * Configuration.RESTITUTION * projectedVelocityY;

            // Calculate back projected velocities to normal axis
            this.getVelocity().x = projectedVelocityX * cosTheta - projectedVelocityY * sinTheta;
            this.getVelocity().y = projectedVelocityY * cosTheta + projectedVelocityX * sinTheta;

            // Also update circle position so that circle does not sink through the line
            if (Math.abs(transformedCenter.y) < this.getRadius()) {
                transformedCenter.y = transformedCenter.y > 0 ? this.getRadius() : -this.getRadius();
                PointF backTransformedCenter = MathUtils.transformPointFromAxis(transformedCenter, line);
                this.setCenter(backTransformedCenter);
            }
        } else {
            // In this case, circle is colliding with the end point of line. Treat it as a collision with line
            // orthogonal to line joining center of circle and end point of line.

            // If circle is going to hit the starting corner
            float threshold = MathUtils.getDistance(this.getCenter(), line.getStart()) - this.radius;
            if (threshold < Configuration.COLLISION_THRESHOLD) {
                float sinTheta = MathUtils.getSinTheta(this.getCenter(), line.getStart());
                float cosTheta = MathUtils.getCosTheta(this.getCenter(), line.getStart());

                // Calculate projected velocities with the line joining circle center and endpoint as x axis
                float projectedVelocityX = this.getVelocity().x * cosTheta + this.getVelocity().y * sinTheta;
                float projectedVelocityY = this.getVelocity().y * cosTheta - this.getVelocity().x * sinTheta;

                // Compute post-collision velocities (Note that velocity along projected X axis will be inverted)
                projectedVelocityX = -1F * Configuration.RESTITUTION * projectedVelocityX;

                // Calculate back projected velocities to normal axis
                this.getVelocity().x = projectedVelocityX * cosTheta - projectedVelocityY * sinTheta;
                this.getVelocity().y = projectedVelocityY * cosTheta + projectedVelocityX * sinTheta;

                // Also update circle position so that circle does not sink through the line
                if (threshold < 0) {
                    Line line1 = Line.getTemporaryLine(line.getStart(), this.getCenter());
                    PointF transformedCenter1 = MathUtils.transformPointToAxis(this.getCenter(), line1);
                    transformedCenter1.x = this.getRadius();  // transformedCenter.y should be approximately 0;
                    PointF backTransformedCenter = MathUtils.transformPointFromAxis(transformedCenter1, line1);
                    this.setCenter(backTransformedCenter);
                }
            }

            // If circle is going to hit the ending corner
            threshold = MathUtils.getDistance(this.getCenter(), line.getEnd()) - this.radius;
            if (threshold < Configuration.COLLISION_THRESHOLD) {
                float sinTheta = MathUtils.getSinTheta(this.getCenter(), line.getEnd());
                float cosTheta = MathUtils.getCosTheta(this.getCenter(), line.getEnd());

                // Calculate projected velocities with the line joining circle center and endpoint as x axis
                float projectedVelocityX = this.getVelocity().x * cosTheta + this.getVelocity().y * sinTheta;
                float projectedVelocityY = this.getVelocity().y * cosTheta - this.getVelocity().x * sinTheta;

                // Compute post-collision velocities (Note that velocity along projected X axis will be inverted)
                projectedVelocityX = -1F * Configuration.RESTITUTION * projectedVelocityX;

                // Calculate back projected velocities to normal axis
                this.getVelocity().x = projectedVelocityX * cosTheta - projectedVelocityY * sinTheta;
                this.getVelocity().y = projectedVelocityY * cosTheta + projectedVelocityX * sinTheta;

                // Also update circle position so that circle does not sink through the line
                if (threshold < 0) {
                    Line line1 = Line.getTemporaryLine(line.getEnd(), this.getCenter());
                    PointF transformedCenter1 = MathUtils.transformPointToAxis(this.getCenter(), line1);
                    transformedCenter1.x = this.getRadius();  // transformedCenter.y should be approximately 0;
                    PointF backTransformedCenter = MathUtils.transformPointFromAxis(transformedCenter1, line1);
                    this.setCenter(backTransformedCenter);
                }
            }
        }
    }
}
