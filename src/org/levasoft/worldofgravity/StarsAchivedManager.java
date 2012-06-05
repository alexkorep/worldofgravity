package org.levasoft.worldofgravity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class StarsAchivedManager {
	 private static final String APP_SHARED_PREFS = "org.levasoft.worldofgravity_preferences";
	private static final String KEY_STARS = "stars";
	private static final String KEY_MAX_LEVEL = "max_level";

	public static void saveAchivement(
			Context context, int level, int starsCollected) {
		
		final int existingStars = getAchivement(context, level);
		if (existingStars >= starsCollected) {
			return;
		}
		
		SharedPreferences prefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        Editor editor = prefs.edit();
        final String key = KEY_STARS + level;
        editor.putInt(key, starsCollected);
        editor.commit();
	}
	
	public static int getAchivement(Context context, int level) {
		SharedPreferences prefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
		final String key = KEY_STARS + level;
		return prefs.getInt(key, 0);
	}
	
	public static void setLevelPassed(Context context, int level) {
		
		final int oldMaxLevel = getMaxLevelPassed(context);
		if (oldMaxLevel >= level) {
			return;
		}
		
		SharedPreferences prefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        Editor editor = prefs.edit();
        final String key = KEY_MAX_LEVEL;
        editor.putInt(key, level);
        editor.commit();
	}
	
	/**
	 * Returns 1-base index of the last complete level. If no levels were complete, returns 0.
	 * @param context
	 * @return
	 */
	public static int getMaxLevelPassed(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
		final String key = KEY_MAX_LEVEL;
		return prefs.getInt(key, 0);
	}
}
