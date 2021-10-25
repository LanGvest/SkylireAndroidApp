package com.langvest.skylire;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.langvest.skylire.utils.AppMode;
import com.langvest.skylire.utils.Auth;
import com.langvest.skylire.utils.Dialog;
import com.langvest.skylire.utils.MessageAdapter;
import java.util.Random;

public class AuthActivity extends AppCompatActivity {

    TextView authButton;
    private final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AuthActivity);
        setContentView(R.layout.activity_auth);

        AppMode.getInstance(AuthActivity.this, false).startListenForModeCode();

        authButton = findViewById(R.id.auth_button);
        authButton.setOnClickListener(v -> signIn());
    }

    @SuppressWarnings("deprecation")
    private void signIn() {
        blockAuthButton();
        Intent signInIntent = Auth.getInstance().getGoogleSignInClient(AuthActivity.this).getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if(task.isSuccessful()) {
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch(ApiException e) {
                    unblockAuthButton();
                    new Dialog(AuthActivity.this)
                            .setTitle("Ошибка авторизации")
                            .setText(e.getMessage())
                            .show(true, true);
                }
            } else unblockAuthButton();
        }
    }

    private void blockAuthButton() {
        authButton.setClickable(false);
        authButton.setVisibility(View.INVISIBLE);
    }

    private void unblockAuthButton() {
        authButton.setClickable(true);
        authButton.setVisibility(View.VISIBLE);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Auth.getInstance().getFirebaseAuth().signInWithCredential(credential).addOnCompleteListener(AuthActivity.this, task -> {
            if(task.isSuccessful()) {
                saveNewUserData();
                openChat();
            } else unblockAuthButton();
        });
    }

    private void saveNewUserData() {
        SharedPreferences preferences = getSharedPreferences("common", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("color", String.valueOf(new Random().nextInt(20)));
        String newIcon = "";
        if(Math.random() < 0.5) {
            int min = 0;
            int max = MessageAdapter.iconFormats.length-1;
            int index = new Random().nextInt(max-min+1)+min;
            newIcon = String.valueOf(index);
        }
        editor.putString("icon", newIcon);
        editor.apply();
    }

    private void openChat() {
        startActivity(new Intent(AuthActivity.this, ChatActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}