package org.levasoft.worldofgravity;

import static org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class PhisObject {
	private Sprite m_sprite;
	private Body m_body;
	
	/**
	 * 
	 * @param textureRegion
	 * @param physicsWorld
	 * @param scene
	 * @param fixtureDef
	 * @param touchEventHandler in, can be null, in this case touch event will not be handled
	 */
	public PhisObject(TextureRegion textureRegion, PhysicsWorld physicsWorld, Scene scene, FixtureDef fixtureDef) {

		m_sprite = new Sprite(50, 50, textureRegion);
		m_body = PhysicsFactory.createBoxBody(physicsWorld, m_sprite, BodyType.StaticBody, fixtureDef);
		scene.attachChild(m_sprite);
		//physicsWorld.registerPhysicsConnector(new PhysicsConnector(m_sprite, m_body, true, true));
	}
	
	public Sprite getSprite() {
		return m_sprite;
	}

	public Body getBody() {
		return m_body;
	}

	public void move(int x, int y) {
		final Vector2 localPoint = Vector2Pool.obtain(x/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, y/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
		m_body.setTransform(localPoint, 0);
		Vector2Pool.recycle(localPoint);
		m_sprite.setPosition(x - m_sprite.getWidth()/2, y - m_sprite.getHeight()/2);
	}
}