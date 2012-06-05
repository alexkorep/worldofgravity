package org.levasoft.worldofgravity;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

public class LevelSelectActivity extends BaseGameActivity {

	private static final float CAMERA_HEIGHT = 480;
	private static final float CAMERA_WIDTH = 720;
	
	private static final int ICON_WIDTH = 100;
	private static final int ICON_HEIGHT = 100;
	private static final int ICON_COLS = 5;
	private static final int ICON_ROWS = 3;
	private static final int VERT_MARGIN = 30;
	private static final int HORZ_MARGIN = 50;
	private static final Integer MAX_LEVEL = ICON_COLS*ICON_ROWS;
	private static final float STAR_HEIGHT = 20;
	private static final float STAR_WIDTH = 20;
	private static final int MAX_STAR_COUNT = 3;

	private Scene m_scene;
	private final Camera m_camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
	private TextureRegion m_levelIconTextureRegion;
	private Font m_font;
	private Integer m_lastLevel;
	private TextureRegion m_starTextureRegion;
	private TextureRegion m_starBwTextureRegion;

	private Sprite[][] m_stars = new Sprite[MAX_LEVEL][MAX_STAR_COUNT];
	private Sprite[][] m_starsBw = new Sprite[MAX_LEVEL][MAX_STAR_COUNT];
	private TextureRegion m_levelIconGrayTextureRegion;
	private Sprite[] m_levelIconSprites = new Sprite[MAX_LEVEL];
	
	@Override
	public Engine onLoadEngine() {
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, 
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), m_camera);
		engineOptions.getTouchOptions().setRunOnUpdateThread(true);
		return new Engine(engineOptions);
	}

	@Override
	public void onLoadResources() {
		//m_bitmapTextureAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(128, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		int y = 0;
		//m_backgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.m_bitmapTextureAtlas, this, "back2.png", 0, y); // 720x480
		//y += 480;
		
		m_levelIconTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, this, "levelicon.png", 0, y); // 100x100
		y += 100;
		
		m_levelIconGrayTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, this, "levelicon_bw.png", 0, y); // 100x100
		y += 100;
		
		m_starTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, this, "star_small.png", 0, y); // 20x20
		y += 20;
		
		m_starBwTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, this, "star_small_bw.png", 0, y); // 20x20
		y += 20;
		
		mEngine.getTextureManager().loadTexture(bitmapTextureAtlas);

	}

	@Override
	public Scene onLoadScene() {
		m_scene = new Scene();

		// Set sky background
		//Sprite backSprite = new Sprite(0, 0, m_backgroundTextureRegion);
		//m_scene.setBackground(new SpriteBackground(backSprite));
		//m_scene.setBackground(new ColorBackground(0x05/256f, 0x18/256f, 0x34/256f));
		//m_scene.setBackground(new ColorBackground(0x0, 0x0, 0x0));

		loadFont();
		addIcons();
		updateStars();
		updateLevelAvailability();

		return m_scene;
	}

	private void loadFont() {
		BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		m_font = new Font(fontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 
				32, true, Color.WHITE);

		mEngine.getTextureManager().loadTexture(fontTexture);
		mEngine.getFontManager().loadFont(m_font);
		
	}

	private void addIcons() {
		final float stepX = (CAMERA_WIDTH - 2*HORZ_MARGIN)/ICON_COLS;
		final float stepY = (CAMERA_HEIGHT - 2*VERT_MARGIN)/ICON_ROWS;
		for (int row = 0; row < ICON_ROWS; ++row) {
			for (int col = 0; col < ICON_COLS; ++col) {
				final int nLevel = col + row*ICON_COLS;

				final float x = HORZ_MARGIN + stepX*col + (stepX - ICON_WIDTH)/2;
				final float y = VERT_MARGIN + stepY*row + (stepY - ICON_HEIGHT)/2;

				Sprite grayIconSprite = new Sprite(x, y, m_levelIconGrayTextureRegion);
				m_scene.attachChild(grayIconSprite);
				
				Sprite iconSprite = new Sprite (x, y, m_levelIconTextureRegion) {
					@Override
					public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
						if (!isVisible()) {
							return false;
						}
						
						if (pSceneTouchEvent.isActionDown()) {
							Integer level = (Integer)getUserData();
							launchLevel(level);
							return true;
						}
						return false;
					}
				};
				m_scene.attachChild(iconSprite);
				m_levelIconSprites[nLevel] = iconSprite;
				

				final int levelNo = nLevel + 1; // 1-based index 
				iconSprite.setUserData(new Integer(levelNo));
				
				Text text = new Text(x + 35, y + 20, m_font, Integer.toString(levelNo));
				m_scene.attachChild(text);
				
				
				int[] xCoords = {20, 40, 60};
				for (int i = 0; i < xCoords.length; ++i) {

					Sprite starSprite = new Sprite(x + xCoords[i], y + 70, 
							STAR_WIDTH, STAR_HEIGHT, m_starTextureRegion);
					m_scene.attachChild(starSprite);
					m_stars[nLevel][i] = starSprite;

					Sprite starbwSprite = new Sprite(x + xCoords[i], y + 70, 
							STAR_WIDTH, STAR_HEIGHT, m_starBwTextureRegion);
					m_scene.attachChild(starbwSprite);
					m_starsBw[nLevel][i] = starbwSprite;
					starbwSprite.setVisible(false);
				}
				
				m_scene.registerTouchArea(iconSprite);

			}
		}
		
	}

	protected void launchLevel(Integer level) {
		m_lastLevel = level;
		
		Bundle bun = new Bundle();
		bun.putInt("level", level);
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.putExtras(bun);
		intent.setClassName(this, WorldOfGravityActivity.class.getName());
		startActivityForResult(intent, 0);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		// new stars could be reached 
		updateStars();
		updateLevelAvailability();
		
		if (resultCode == WorldOfGravityActivity.ACTIVITY_RESULT_SELECT_LEVEL) {
			// do nothing, just need to select another level
			return;
		}
			
		if (m_lastLevel < MAX_LEVEL) {
			launchLevel(m_lastLevel + 1);
		} else {
			// TODO all levels finished, handle this situation somehow
		}
	}
	
	private void updateStars() {
		for (int level = 0; level < MAX_LEVEL; ++level) {
			final int stars = StarsAchivedManager.getAchivement(this, level + 1);
			for (int i = 0; i < MAX_STAR_COUNT; ++i) {
				m_stars[level][i].setVisible(i < stars);
				m_starsBw[level][i].setVisible(i >= stars);
			}
		}
	}

	private void updateLevelAvailability() {
		final int maxLevel = StarsAchivedManager.getMaxLevelPassed(this);
		for (int level = 0; level < MAX_LEVEL; ++level) {
			m_levelIconSprites[level].setVisible(level <= maxLevel);
		}
	}

	@Override
	public void onLoadComplete() {
	}
}