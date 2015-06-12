package com.subrat.Oxygen.backendRoutines;

import java.util.ArrayList;

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
import com.google.fpl.liquidfun.ParticleDef;
import com.google.fpl.liquidfun.ParticleFlag;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.ParticleSystemDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.World;
import com.google.fpl.liquidfun.Body;

public class PhysicsEngine {
	private World world;
	private ParticleSystem particleSystem;

	private SparseArray<Body> objectList = new SparseArray<Body>();
	
	public PhysicsEngine() {
		initializeWorld();
		createParticleSystem();
	}
	
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
		world.step(Configuration.REFRESH_INTERVAL,
				   Configuration.VELOCITY_ITERATIONS,
				   Configuration.POSITION_ITERATIONS,
				   Configuration.PARTICLE_ITERATIONS);
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
		fixtureDef.setDensity(2F);
		fixtureDef.setRestitution(Configuration.RESTITUTION);
		fixtureDef.setFriction(1F);
		
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
		lineShape.setAsBox(length / 2, Configuration.LINE_THICKNESS/2);
		
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
		float rad = circleBody.getAngle();
		int deg = (int)( (rad * -180F) / MathUtils.getPI() );
		circle.setRotation(deg);
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
	
	public void createParticleSystem() {
		ParticleSystemDef psDef = new ParticleSystemDef();
        psDef.setRadius(Configuration.PARTICLE_RADIUS);
        psDef.setDampingStrength(Configuration.PARTICLE_DAMPING);
        psDef.setRepulsiveStrength(Configuration.PARTICLE_REPULSIVE_STRENGTH);
        psDef.setDensity(Configuration.PARTICLE_DENSITY);
        // psDef.setStrictContactCheck(true);
        
        particleSystem = world.createParticleSystem(psDef);
        particleSystem.setMaxParticleCount(Configuration.MAX_PARTICLE_COUNT);
        psDef.delete();
        
        // addGroundWater();
	}
	
	public void addGroundWater() {
        for (int x = 1; x < 20; ++x) {
        	for (int y = 1; y < 20; ++y) {
        		ParticleDef particleDef = new ParticleDef();
        		particleDef.setFlags(ParticleFlag.waterParticle);
        		particleDef.setPosition(2 * Configuration.CANVAS_MARGIN + 2 * Configuration.LINE_THICKNESS + ((float)x)/7,
        				                2 * Configuration.CANVAS_MARGIN + 2 * Configuration.LINE_THICKNESS + ((float)y)/7);
        		particleSystem.createParticle(particleDef);
        		particleDef.delete();
        	}
        }
	}
	
	public void addWater() {
		float shift = MathUtils.getRandom(OxygenActivity.getWorldWidth() / 5, (OxygenActivity.getWorldWidth() * 3) / 5);
		for (int x = 1; x < 5; ++x) {
			for (int y = 1; y < 5; ++y) {
        		ParticleDef particleDef = new ParticleDef();
        		particleDef.setFlags(ParticleFlag.waterParticle);
        		float borderDistance = 2 * Configuration.CANVAS_MARGIN + 2 * Configuration.LINE_THICKNESS;
        		particleDef.setPosition(borderDistance + shift + ((float)x)/7,
        				                OxygenActivity.getWorldHeight() - borderDistance - ((float)y)/7);
        		particleSystem.createParticle(particleDef);
        		particleDef.delete();	
			}
		}
	}
	
	public void updateParticles(ArrayList<Circle> particleList) {
		if (particleSystem == null) return;
		for (int i = 0; i < particleSystem.getParticleCount(); ++i) {
			PointF center = new PointF(particleSystem.getParticlePositionX(i), OxygenActivity.getWorldHeight() - particleSystem.getParticlePositionY(i));
			if (particleList.size() <= i) {
				Circle circle = new Circle(center, Configuration.PARTICLE_RADIUS, true);
				particleList.add(circle);
			} else {
				Circle circle = particleList.get(i);
				circle.setCenter(center);
			}
		}
	}
}
