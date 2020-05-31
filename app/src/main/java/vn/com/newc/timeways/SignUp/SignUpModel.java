package vn.com.newc.timeways.SignUp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.transition.Visibility;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpModel {

    private SignUpMVP.Model modelcalback;
    private FirebaseAuth mAuth;
    private static final String TAG="Sign Up Login";

    public SignUpModel(SignUpMVP.Model modelcalback) {
        this.modelcalback = modelcalback;
    }

    public void handleSignUp(Activity activity, String email, String password, String retypePassword) {
        initFirebaseAuth();

        if (email.isEmpty() || password.isEmpty() || retypePassword.isEmpty()) {
            modelcalback.missingMessageEntered();
        } else if (!password.equals(retypePassword)) {
            modelcalback.passwordNotSame();
        } else {
            createFirebaseRegularAccount(activity, email, password);
        }
    }

    private void initFirebaseAuth() {
        mAuth=FirebaseAuth.getInstance();
    }

    private void createFirebaseRegularAccount(Activity activity, String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                            modelcalback.onSignUpSuccess();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            modelcalback.onSignUpFailed();
                        }

                        // ...
                    }
                });
    }
}
