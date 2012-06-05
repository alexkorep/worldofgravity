package org.levasoft.worldofgravity;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class LevelCompleteDialog {
	
	private static final int STAR_MAX_COUNT = 3;
	private final WorldOfGravityActivity m_activity;
	private Sprite m_dialog;
	private Sprite m_menuSprite;
	private Sprite m_restartSprite;
	private Sprite m_nextButtonSprite;
	private Sprite[] starSprites = new Sprite[STAR_MAX_COUNT];
	private Sprite m_marketButtonSprite;
	
	LevelCompleteDialog(WorldOfGravityActivity activity) {
		m_activity = activity;
	}
	
	void showDialog(int starsCollected) {
		//Toast.makeText(m_activity, "LevelCompleteDialog displayed", Toast.LENGTH_SHORT).show();
		
		m_activity.m_ballBody.setActive(false);
		
		m_dialog = new Sprite(0, 0, m_activity.m_completeDialogTextureRegion) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				// Do nothing
				return true;
			}
		};
		m_activity.getScene().attachChild(m_dialog);
		
		m_menuSprite = new Sprite(200, 360, m_activity.m_menuTextureRegion) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					m_activity.finish();
					return true;
				}
				return false;
			}
		};
		m_activity.getScene().attachChild(m_menuSprite);
		m_activity.getScene().registerTouchArea(m_menuSprite);
	
		m_restartSprite = new Sprite(320, 360, m_activity.m_restartTextureRegion) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					closeDialog();
					m_activity.restartLevel();
					return true;
				}
				return false;
			}
		};
		m_activity.getScene().attachChild(m_restartSprite);
		m_activity.getScene().registerTouchArea(m_restartSprite);
		
		m_nextButtonSprite = new Sprite(440, 360, m_activity.m_nextButtonTextureRegion) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					closeDialog();
					m_activity.nextLevel();
					return true;
				}
				return false;
			}
		};
		m_activity.getScene().attachChild(m_nextButtonSprite);
		m_activity.getScene().registerTouchArea(m_nextButtonSprite);
		
		m_marketButtonSprite = new Sprite(520, 140, m_activity.m_marketButtonTextureRegion) {
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					// Open rate page
					Uri marketUri = Uri.parse("market://details?id=" + m_activity.getPackageName());
		            Intent intent = new Intent(Intent.ACTION_VIEW);
		            intent.setData(marketUri);
		            m_activity.startActivity(intent);
					return true;
				}
				return false;
			}
		};
		m_activity.getScene().attachChild(m_marketButtonSprite);
		m_activity.getScene().registerTouchArea(m_marketButtonSprite);
		
		for (int i = 0; i < STAR_MAX_COUNT; ++i) {
			starSprites[i] = new Sprite(220 + i*100, 220,
					i < starsCollected ? m_activity.m_starBigTextureRegion : m_activity.m_starBigBwTextureRegion);
			m_activity.getScene().attachChild(starSprites[i]);
		}
	}

	protected void closeDialog() {
		m_activity.getScene().detachChild(m_dialog);
		m_activity.getScene().detachChild(m_menuSprite);
		m_activity.getScene().unregisterTouchArea(m_menuSprite);
		m_activity.getScene().detachChild(m_restartSprite);
		m_activity.getScene().unregisterTouchArea(m_restartSprite);
		m_activity.getScene().detachChild(m_nextButtonSprite);
		m_activity.getScene().unregisterTouchArea(m_nextButtonSprite);
		m_activity.getScene().detachChild(m_marketButtonSprite);
		m_activity.getScene().unregisterTouchArea(m_marketButtonSprite);

		for (int i = 0; i < STAR_MAX_COUNT; ++i) {
			m_activity.getScene().detachChild(starSprites[i]);
		}
		
		m_activity.m_ballBody.setActive(true);
	}

}
