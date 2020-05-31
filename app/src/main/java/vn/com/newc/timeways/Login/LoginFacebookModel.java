package vn.com.newc.timeways.Login;

import android.app.Activity;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class LoginFacebookModel {

    private static final String TAG = "FacebookLoginLog";
    private FirebaseAuth mAuth;
    private LoginMVP.LoginFacebookModel modelCallback;

    public LoginFacebookModel(LoginMVP.LoginFacebookModel modelCallback) {
        this.modelCallback = modelCallback;
    }

    public void initAuthentication(){
        mAuth = FirebaseAuth.getInstance();
    }

    public void handleFacebookAccessToken(Activity activity, AccessToken token) {
        initAuthentication();
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                            modelCallback.onLoginFacebookSuccess();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithCredential:failure", task.getException());
//                            Toast.makeText(FacebookLoginActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);

                            modelCallback.onLoginFacebookFailed();
                        }

                        // ...
                    }
                });
    }
}
