package com.langvest.skylire.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.langvest.skylire.LockActivity;
import com.langvest.skylire.MainActivity;
import com.langvest.skylire.R;

public class AppMode {

	private static AppMode appMode;
	private final DatabaseReference FBD_mode;
	private boolean isLockActivity;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private OpenScreenAction openLockScreen;
	private OpenScreenAction openMainScreen;

	private AppMode() {
		FBD_mode = FirebaseDatabase.getInstance().getReference().child("mode");

		ValueEventListener eventListener = new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				Integer value = dataSnapshot.getValue(Integer.class);
				int modeCode = 1;
				if (value != null) modeCode = value;
				editor.putInt("mode", modeCode);
				editor.apply();
				if (isLockActivity) {
					if (modeCode == 0) openMainScreen.execute();
				} else {
					if (modeCode != 0) openLockScreen.execute();
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {}
		};

		FBD_mode.addValueEventListener(eventListener);
	}

	private void setOpenLockScreen(OpenScreenAction action) {
		openLockScreen = action;
	}

	private void setOpenMainScreen(OpenScreenAction action) {
		openMainScreen = action;
	}

	private void setIsLockActivity(boolean isLockActivity) {
		this.isLockActivity = isLockActivity;
	}

	private void setPreferences(Activity activity) {
		this.preferences = activity.getSharedPreferences("security", 0);
	}

	private void updateEditor() {
		this.editor = preferences.edit();
	}

	public static synchronized AppMode getInstance(Activity activity, boolean isLockActivity) {
		if(appMode == null) appMode = new AppMode();
		appMode.setOpenLockScreen(() -> {
			activity.startActivity(new Intent(activity, LockActivity.class));
			activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			activity.finish();
		});
		appMode.setOpenMainScreen(() -> {
			activity.startActivity(new Intent(activity, MainActivity.class));
			activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			activity.finish();
		});
		appMode.setIsLockActivity(isLockActivity);
		appMode.setPreferences(activity);
		appMode.updateEditor();
		return appMode;
	}

	public void startListenForModeCode() {
		if(!isLockActivity && preferences.getInt("mode", 0) != 0) openLockScreen.execute();
	}

	public void onceListenForModeCode(OnNormalModeCode normalModeCode) {
		FBD_mode.get().addOnCompleteListener(task -> {
			int modeCode = preferences.getInt("mode", 0);
			if(task.isSuccessful()) {
				Integer value = task.getResult().getValue(Integer.class);
				if(value != null) modeCode = value;
			}
			editor.putInt("mode", modeCode);
			editor.apply();
			if(!isLockActivity) {
				if(modeCode != 0) openLockScreen.execute();
				else normalModeCode.execute();
			}
		});
	}

	@FunctionalInterface
	public interface OnNormalModeCode {
		void execute();
	}

	@FunctionalInterface
	public interface OpenScreenAction {
		void execute();
	}
}