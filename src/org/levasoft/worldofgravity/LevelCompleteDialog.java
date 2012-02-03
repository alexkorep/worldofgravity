package org.levasoft.worldofgravity;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;

public class LevelCompleteDialog {
	
	private final WorldOfGravityActivity m_activity;
	private Sprite m_dialog;
	private Sprite m_menuSprite;
	private Sprite m_restartSprite;
	private Sprite m_nextButtonSprite;
	
	LevelCompleteDialog(WorldOfGravityActivity activity) {
		m_activity = activity;
	}
	
	void showDialog() {
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
					//m_activity.nextLevel();
					return true;
				}
				return false;
			}
		};
		m_activity.getScene().attachChild(m_nextButtonSprite);
		m_activity.getScene().registerTouchArea(m_nextButtonSprite);
	}

	protected void closeDialog() {
		m_activity.getScene().detachChild(m_dialog);
		m_activity.getScene().detachChild(m_menuSprite);
		m_activity.getScene().unregisterTouchArea(m_menuSprite);
		m_activity.getScene().detachChild(m_restartSprite);
		m_activity.getScene().unregisterTouchArea(m_restartSprite);
		m_activity.getScene().detachChild(m_nextButtonSprite);
		m_activity.getScene().unregisterTouchArea(m_nextButtonSprite);
		
		m_activity.m_ballBody.setActive(true);
	}

}
