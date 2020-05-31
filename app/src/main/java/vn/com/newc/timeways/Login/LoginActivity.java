package vn.com.newc.timeways.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import vn.com.newc.timeways.Main.MainActivity;
import vn.com.newc.timeways.R;
import vn.com.newc.timeways.SignUp.SignUpActivity;

public class LoginActivity extends AppCompatActivity implements LoginMVP.View, View.OnClickListener {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private ImageButton imgbtnFacebook, imgbtnGoogle;
    private TextView tvSignUp;
    private LoginMVP.Presenter presenter;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 234;
    private final String TAG = "Login Google";
    private CallbackManager mCallbackManager;
    private String duplicateNotifications="Email của tài khoản này đã được sử dụng. Vui lòng đăng nhập bằng tài khoản khác.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        initPresenter();
        registerListener();

        findKeyHash();

        initGoogleSignInOptions();
        initGoogleSignInClient();
    }

    private void init() {
        imgbtnGoogle        = (ImageButton) findViewById(R.id.imageButtonLoginWithGoogle);
        edtEmail            = (EditText) findViewById(R.id.editTextEnterEmail);
        edtPassword         = (EditText) findViewById(R.id.editTextEnterPassword);
        btnLogin            = (Button) findViewById(R.id.buttonLogin);
        imgbtnFacebook      = (ImageButton) findViewById(R.id.imageButtonLoginWithFacebook);
        tvSignUp            = (TextView) findViewById(R.id.textViewRegistration);
    }

    private void initPresenter() {
        presenter = new LoginPresenter(this);
    }

    private void registerListener() {
        btnLogin.setOnClickListener(this);
        imgbtnFacebook.setOnClickListener(this);
        imgbtnGoogle.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
    }

    private void initCallbackManager() {
        mCallbackManager = CallbackManager.Factory.create();
    }

    private void registerCallbackFacebook() {
        initCallbackManager();

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                presenter.getHandleLoginFacebook(LoginActivity.this, loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
    }

    private void initGoogleSignInOptions() {
        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
    }

    private void initGoogleSignInClient() {
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin:
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                presenter.getHandleLogin(this, email, password);
                break;
            case R.id.imageButtonLoginWithGoogle:
                signInGoogle();
                break;
            case R.id.imageButtonLoginWithFacebook:
                registerCallbackFacebook();
                break;
            case R.id.textViewRegistration:
                switchToRegisterActvitivy();
                break;
        }
    }

    private void switchToRegisterActvitivy() {
        Intent intentToRegiter=new Intent(this, SignUpActivity.class);
        startActivity(intentToRegiter);
    }

    private void findKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "vn.com.newc.timeways",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void noticeEmptyData() {
        Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void processLogin() {
        Intent intentMain = new Intent(this, MainActivity.class);
        startActivity(intentMain);
    }

    @Override
    public void noticeLoginFailed() {
        Toast.makeText(this, "Tài khoản hoặc mật khẩu của bạn không chính xác.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void processLoginWithFacebook() {
        Intent intentMain = new Intent(this, MainActivity.class);
        startActivity(intentMain);
    }

    @Override
    public void noticeLoginFacebookFailed() {
        Toast.makeText(this, duplicateNotifications, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void processLoginWithGoogle() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void noticeLoginGoogleFailed() {
        Snackbar.make(findViewById(R.id.layoutLogin), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                presenter.getHandleLoginGoogle(this, account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.d(TAG, "Google sign in failed", e);
                // ...
            }
        }else {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }


}
