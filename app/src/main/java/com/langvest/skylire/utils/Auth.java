package com.langvest.skylire.utils;

import android.app.Activity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.langvest.skylire.R;

public class Auth {

	private static Auth auth;
	private static FirebaseAuth firebaseAuth;

	private Auth() {
		firebaseAuth = FirebaseAuth.getInstance();
	}

	public static synchronized Auth getInstance() {
		if(auth == null) auth = new Auth();
		return auth;
	}

	public GoogleSignInClient getGoogleSignInClient(Activity activity) {
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(activity.getString(R.string.default_web_client_id_custom))
				.requestEmail()
				.build();
		return GoogleSignIn.getClient(activity, gso);
	}

	public FirebaseAuth getFirebaseAuth() {
		return firebaseAuth;
	}
}