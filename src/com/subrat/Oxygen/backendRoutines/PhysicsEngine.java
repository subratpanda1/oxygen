package com.subrat.Oxygen.backendRoutines;

import android.graphics.PointF;
import android.util.SparseArray;

import com.subrat.Oxygen.activities.OxygenActivity;
import com.subrat.Oxygen.objects.Circle;
import com.subrat.Oxygen.objects.Line;
import com.subrat.Oxygen.utilities.Configuration;
import com.subrat.Oxygen.utilities.MathUtils;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.World;
import com.google.fpl.liquidfun.Body;

public class PhysicsEngine {
	World world;
	private SparseArray<Body> objectList = new SparseArray<Body>();
	
	public PhysicsEngine() { initializeWorld();	}
	
	public void clearWorld() {
		if (world != null) {
			for(int i = 0; i < objectList.size(); i++) {
				int key = objectList.keyAt(i);
				Body body = objectList.get(key);
				world.destroyBody(body);
			}
			world.delete();
			world = null;
		}
		objectList.clear();
	}
	
	private void initializeWorld() {
		clearWorld();
		world = new World(0, 0);
	}
	
	public void stepWorld() {
		if (world == null) return;
		world.step(Configuration.getRefreshInterval(), 5, 5, 5);
	}
	
	public void setGravity(PointF gravity) {
		world.setGravity(gravity.x, gravity.y);
	}
	
	public void createCircle(Circle circle) {
		PointF centerInWorld = new PointF(circle.getCenter().x, OxygenActivity.getWorldHeight() - circle.getCenter().y);

		CircleShape circleShape = new CircleShape();
		circleShape.setPosition(0,0);
		circleShape.setRadius(circle.getRadius());
		
		BodyDef circleBodyDef = new BodyDef();
		circleBodyDef.setType(BodyType.dynamicBody);
		circleBodyDef.setPosition(centerInWorld.x, centerInWorld.y);
		circleBodyDef.setAllowSleep(false);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.setShape(circleShape);
		fixtureDef.setDensity(1F);
		fixtureDef.setRestitution(Configuration.getRestitution());
		fixtureDef.setFriction(0F);
		
		Body circleBody = world.createBody(circleBodyDef);
		circleBody.createFixture(fixtureDef);
		
		circleShape.delete();
		circleBodyDef.delete();
		fixtureDef.delete();
		
		objectList.put(circle.getObjectId(), circleBody);
	}
	
	public void createLine(Line line) {
		PointF startPointInWorld = new PointF(line.getStart().x, OxygenActivity.getWorldHeight() - line.getStart().y);
		PointF endPointInWorld = new PointF(line.getEnd().x, OxygenActivity.getWorldHeight() - line.getEnd().y);
		
		PolygonShape lineShape = new PolygonShape();
		float length = MathUtils.getDistance(line.getStart(), line.getEnd());
		lineShape.setAsBox(length / 2, Configuration.getLineThickness()/2);
		
		BodyDef lineBodyDef = new BodyDef();
		lineBodyDef.setType(BodyType.staticBody);
		lineBodyDef.setPosition((startPointInWorld.x + endPointInWorld.x) / 2, (startPointInWorld.y + endPointInWorld.y) / 2);
		lineBodyDef.setAngle(MathUtils.getRadian(startPointInWorld, endPointInWorld));

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.setShape(lineShape);
		fixtureDef.setDensity(0F);
		
		Body lineBody = world.createBody(lineBodyDef);
		// lineBody.createFixture(lineShape, 0F);
		lineBody.createFixture(fixtureDef);
		
		lineShape.delete();
		lineBodyDef.delete();
		fixtureDef.delete();
		
		objectList.put(line.getObjectId(), lineBody);
	}
	
	public void updateCircle(Circle circle) {
		Body circleBody = objectList.get(circle.getObjectId());
		
		PointF centerInCanvas = new PointF(circleBody.getPositionX(), OxygenActivity.getWorldHeight() - circleBody.getPositionY());
		circle.setCenter(centerInCanvas);
	}
	
	public void editCircle(Circle circle) {
		Body circleBody = objectList.get(circle.getObjectId());
		
		PointF centerInWorld = new PointF(circle.getCenter().x, OxygenActivity.getWorldHeight() - circle.getCenter().y);
		circleBody.setTransform(centerInWorld.x, centerInWorld.y, 0);
		
	}
	
	public void updateLine(Line line) {
		// Body lineBody = objectList.get(line.getObjectId());
		// Get edge points from line center and rotation
		// PointF lineStartInCanvas
		// PointF lineEndInCanvas
	}
	
	public void editLine(Line line) {
		PointF startPointInWorld = new PointF(line.getStart().x, OxygenActivity.getWorldHeight() - line.getStart().y);
		PointF endPointInWorld   = new PointF(line.getEnd().x,   OxygenActivity.getWorldHeight() - line.getEnd().y);
		
		Body lineBody = objectList.get(line.getObjectId());
		lineBody.setTransform((startPointInWorld.x + endPointInWorld.x) / 2,
				              (startPointInWorld.y + endPointInWorld.y) / 2,
				              MathUtils.getRadian(startPointInWorld, endPointInWorld));
	}

}
