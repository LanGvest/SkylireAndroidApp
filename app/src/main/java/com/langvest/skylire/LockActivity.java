package com.langvest.skylire;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.langvest.skylire.utils.AppMode;

public class LockActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.LockActivity);
		setContentView(R.layout.activity_lock);

		AppMode.getInstance(LockActivity.this, true).startListenForModeCode();
	}
}