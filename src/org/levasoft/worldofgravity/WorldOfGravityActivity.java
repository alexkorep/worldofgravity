package org.levasoft.worldofgravity;

import static org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.SmoothCamera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.scene.background.SpriteBackground;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

public class WorldOfGravityActivity extends BaseGameActivity implements IOnSceneTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================

	static final int CAMERA_WIDTH = 720;
	static final int CAMERA_HEIGHT = 480;
	
	static final int BOX_WIDTH = 40;
	static final int BOX_HEIGHT = 40;
	
	private static final FixtureDef BOX_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0.1f, 0.1f, 5f);
	private static final FixtureDef BALL_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0.1f, 0.1f, 5f);
	private static final FixtureDef WALL_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
	
	// Gravity ratios
	private static final float GRAVITY_INITIAL_FORCE_RATIO = 1f;
	private static final float GRAVITY_TOUCH_FORCE_RATIO = 5f;

	private static final String LEVEL_PATH = "/Android/data/org.levasoft.worldofgravity";
	
	// Wall color
	private static final float WALL_COLOR_BLUE = 0;
	private static final float WALL_COLOR_RED = 0;
	private static final float WALL_COLOR_GREEN = 0;
	
	
	
	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas m_bitmapTextureAtlas = null;

	private Scene m_scene = null;

	private PhysicsWorld m_physicsWorld;
	private final SmoothCamera m_camera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 1000, 1000, 10f);
	
	private TextureRegion m_backgroundTextureRegion = null;
	private TextureRegion m_boxTextureRegion = null;
	private TextureRegion m_magnetTextureRegion = null;
	private TextureRegion m_ballTextureRegion = null;
	private Sprite m_ballSprite = null;
	private Sprite m_ballLightSprite = null;
	private Body m_ballBody = null;
	private Vector<PhisObject> m_magnets = new Vector<PhisObject>();


	// 0: nothing
	// 1: box
	// 2: magnet
	// 3: start
	// 4: exit
	// 18x12
	private int[][] m_map = {
			{3, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1},
			{0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1},
			{0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1},
			{0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 2, 4, 0, 0, 1, 0, 1},
			{0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1},
			{1, 1, 2, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 1, 1, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1}
	};
	private float m_forceRatio = 1;
	private TextureRegion m_exitBoxTextureRegion;
	private PhisObject m_exitBox;
	private Vector2 m_gravityForce = null;
	private TextureRegion m_menuTextureRegion;
	private TextureRegion m_restartTextureRegion;
	private float m_initialBoxX = 0;
	private float m_initialBoxY = 0;
	private TextureRegion m_ballLightTextureRegion;
	private TextureRegion m_starTextureRegion;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public Engine onLoadEngine() {
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), m_camera);
		engineOptions.getTouchOptions().setRunOnUpdateThread(true);
		return new Engine(engineOptions);
	}

	@Override
	public void onLoadResources() {
		/* Textures. */
		m_bitmapTextureAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		/* TextureRegions. */
		
		m_backgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.m_bitmapTextureAtlas, this, "back.png", 0, 0); // 720x480
		m_boxTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.m_bitmapTextureAtlas, this, "box.png", 0, 480); // 40x40
		m_magnetTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.m_bitmapTextureAtlas, this, "magnet.png", 0, 520); // 40x40
		m_exitBoxTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.m_bitmapTextureAtlas, this, "exit.png", 0, 560); // 40x40
		m_ballTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.m_bitmapTextureAtlas, this, "ball.png", 0, 600); // 40x40 max
		m_menuTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.m_bitmapTextureAtlas, this, "button_menu.png", 0, 660); // 80x80
		m_restartTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.m_bitmapTextureAtlas, this, "button_restart.png", 0, 740); // 80x80
		m_ballLightTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.m_bitmapTextureAtlas, this, "ball_light.png", 0, 840); // 40x40 max
		m_starTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.m_bitmapTextureAtlas, this, "star.png", 0, 880); // 40x40
		
		this.mEngine.getTextureManager().loadTexture(this.m_bitmapTextureAtlas);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public Scene onLoadScene() {
		m_scene = new Scene();

		m_scene.setOnSceneTouchListener(this);
		
		// Set sky background
		Sprite backSprite = new Sprite(0, 0, m_backgroundTextureRegion);
		m_scene.setBackground(new SpriteBackground(backSprite));

		m_physicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
		// not sure what last 2 params mean
		//m_physicsWorld = new FixedStepPhysicsWorld(30, new Vector2(0, 0), false, 3, 2); 
		
		createRoom();

		createBall();

		loadLevel();
		
		createBoxes();
		
		//m_camera.setZoomFactor(2);
		
		m_scene.registerUpdateHandler(this.m_physicsWorld);
		
		IUpdateHandler updateHandler = new IUpdateHandler() {

			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				Vector2 force = new Vector2(0, 0);
				for (int i = 0; i < m_magnets.size(); ++i) {
					PhisObject magnet = m_magnets.get(i);
					final float distance = magnet.getBody().getWorldCenter().dst(m_ballBody.getWorldCenter());
					m_gravityForce = magnet.getBody().getWorldCenter().sub(m_ballBody.getWorldCenter()).nor().mul(
							GRAVITY_INITIAL_FORCE_RATIO*m_forceRatio/distance);
					force.add(m_gravityForce);
				}
				m_ballBody.applyForce(force, m_ballBody.getWorldCenter());
				
				updateBallLightSpritePosition();
			}
		};
		m_scene.registerUpdateHandler(updateHandler);

		m_physicsWorld.setContactListener(new ContactListener() {
			@Override
			public void beginContact(final Contact pContact) {
				if (pContact == null || 
						pContact.getFixtureA() == null || pContact.getFixtureA().getBody() == null ||
						pContact.getFixtureB() == null || pContact.getFixtureB().getBody() == null) {
					return;
				}
				
				if (pContact.getFixtureA().getBody() == m_exitBox.getBody() ||
						pContact.getFixtureB().getBody() == m_exitBox.getBody()) {
					//Toast.makeText(that, "Level complete!", Toast.LENGTH_LONG).show();
					// we are succeed, exit activity
					finish();
				} 
				
			}
			
			@Override
			public void endContact(final Contact pContact) {
			}


			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Sprite menuSprite = new Sprite(20, 20, m_menuTextureRegion) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					// go back to menu
					finish();
					return  true;
				}
				return false;
			}
		};
		m_scene.registerTouchArea(menuSprite);
		menuSprite.setAlpha(0.8f);
		m_scene.attachChild(menuSprite);

		Sprite restartSprite = new Sprite(620, 20, m_restartTextureRegion) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					// restart level
					m_ballBody.setTransform(m_initialBoxX/PIXEL_TO_METER_RATIO_DEFAULT, 
							m_initialBoxY/PIXEL_TO_METER_RATIO_DEFAULT, 0);
					m_ballBody.setLinearVelocity(0, 0);
					return  true;
				}
				return false;
			}
		};
		m_scene.registerTouchArea(restartSprite);
		restartSprite.setAlpha(0.8f);
		m_scene.attachChild(restartSprite);

		return m_scene;
	}


	private void loadLevel() {
		Bundle bun = getIntent().getExtras();
		int levelNo = bun.getInt("level", 0); 

		//m_map
		try {
			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				Toast.makeText(this, "Error: Cannot access external storage (SD card)", Toast.LENGTH_LONG).show();
				return;
			}
			
			File root = Environment.getExternalStorageDirectory();
		    if (!root.canRead()){
				Toast.makeText(this, "Error: Cannot read from external storage (SD card)", Toast.LENGTH_LONG).show();
				return;
		    }

		    
			File file = new File(root + LEVEL_PATH, levelNo + ".txt");
	        
	        FileReader fileReader = new FileReader(file);
	        BufferedReader in = new BufferedReader(fileReader);
	        String str;
	        int row = 0;
	        while ((str = in.readLine()) != null) {
	        	int[] aRow = m_map[row];
	        	for (int col = 0; col < str.length(); ++col) {
	        		if (col < aRow.length) {
		        		char c = str.charAt(col);
		        		m_map[row][col] = c - '0';
	        		}
	        	}
	        	++row;
	        	if (row >= m_map.length) {
	        		// we are done, don't care what else is in the file
	        		return;
	        	}
	        }
	        in.close();
		} catch (IOException e) {
			String error = "Could not read from file " + e.getMessage();
		    Toast.makeText(this, error, Toast.LENGTH_LONG).show();
		    return;
		}
		
	}

	private void createBoxes() {
		m_exitBox = new PhisObject(
				m_exitBoxTextureRegion, m_physicsWorld, m_scene, BOX_FIXTURE_DEF);

		for (int row = 0; row < m_map.length; ++row) {
			int[] aRow = m_map[row];
			for (int col = 0; col < aRow.length; ++col) {
				int cell = aRow[col];
				if (cell == 0) {
					continue;
				}
				final int x = col*BOX_WIDTH + BOX_WIDTH/2;
				final int y = row*BOX_HEIGHT + BOX_HEIGHT/2;
				if (cell == 1 || cell == 2) {
					PhisObject box = new PhisObject(
							cell == 1 ? m_boxTextureRegion : m_magnetTextureRegion, 
							m_physicsWorld, m_scene, BOX_FIXTURE_DEF);
					box.move(x, y);
					if (cell == 2) {
						m_magnets.add(box);
					}
				}
				
				if (cell == 3) {
					// start position
					m_initialBoxX = x;
					m_initialBoxY = y;
					m_ballBody.setTransform(x/PIXEL_TO_METER_RATIO_DEFAULT, y/PIXEL_TO_METER_RATIO_DEFAULT, 0);
				}
				
				if (cell == 4) {
					// exit position 
					m_exitBox.move(x, y);
				}
				
				if (cell == 5) {
					Sprite star = new Sprite(0, 0, m_starTextureRegion);
					m_scene.attachChild(star);
					star.setPosition(x - star.getWidth()/2, y - star.getBaseHeight()/2);
					// TODO handle star collision	
				}
			}
		}
	}

	private void createRoom() {
		final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2);
		final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
		final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
		final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT);

		PhysicsFactory.createBoxBody(this.m_physicsWorld, ground, BodyType.StaticBody, WALL_FIXTURE_DEF);
		PhysicsFactory.createBoxBody(this.m_physicsWorld, roof, BodyType.StaticBody, WALL_FIXTURE_DEF);
		PhysicsFactory.createBoxBody(this.m_physicsWorld, left, BodyType.StaticBody, WALL_FIXTURE_DEF);
		PhysicsFactory.createBoxBody(this.m_physicsWorld, right, BodyType.StaticBody, WALL_FIXTURE_DEF);

		m_scene.attachChild(ground);
		m_scene.attachChild(roof);
		m_scene.attachChild(left);
		m_scene.attachChild(right);
		
		ground.setColor(WALL_COLOR_RED, WALL_COLOR_GREEN, WALL_COLOR_BLUE);
		roof.setColor(WALL_COLOR_RED, WALL_COLOR_GREEN, WALL_COLOR_BLUE);
		left.setColor(WALL_COLOR_RED, WALL_COLOR_GREEN, WALL_COLOR_BLUE);
		right.setColor(WALL_COLOR_RED, WALL_COLOR_GREEN, WALL_COLOR_BLUE);
	}

	private void createBall() {
		
		m_ballSprite = new Sprite(10, 10, m_ballTextureRegion);
		m_ballBody = PhysicsFactory.createCircleBody(m_physicsWorld, m_ballSprite, BodyType.DynamicBody, BALL_FIXTURE_DEF);
		m_scene.attachChild(m_ballSprite);
		m_physicsWorld.registerPhysicsConnector(new PhysicsConnector(m_ballSprite, m_ballBody, true, true));
		
		m_ballLightSprite = new Sprite(0, 0, m_ballLightTextureRegion);
		m_scene.attachChild(m_ballLightSprite);
	}

	@Override
	public void onLoadComplete() {

	}

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		// Handle menu button
		//
		return super.onKeyDown(pKeyCode, pEvent);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.isActionDown()) {
			m_forceRatio = GRAVITY_TOUCH_FORCE_RATIO;
		} else if (pSceneTouchEvent.isActionUp()) {
			m_forceRatio = GRAVITY_INITIAL_FORCE_RATIO;
		} 
		return false;
	}

	private void updateBallLightSpritePosition() {
		m_ballLightSprite.setPosition(m_ballSprite);
	}


	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}