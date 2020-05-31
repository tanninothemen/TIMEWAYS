package vn.com.newc.timeways.Login;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginModel {
    private FirebaseAuth mAuth;

    private LoginMVP.Model modelCallback;

    public LoginModel(LoginMVP.Model modelCallback) {
        this.modelCallback = modelCallback;
    }

    private void initAuthentication() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void handleLogin(Activity activity, String email, String password) {

        initAuthentication();

        if (email.isEmpty() || password.isEmpty()) {
            modelCallback.emptyData();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                modelCallback.onLoginSuccess();
                            } else {
                                modelCallback.onLoginFailed();
                            }
                        }
                    });
        }
    }
}
