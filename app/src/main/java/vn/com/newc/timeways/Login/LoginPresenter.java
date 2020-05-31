package vn.com.newc.timeways.Login;

import android.app.Activity;

import com.facebook.AccessToken;
import com.facebook.login.Login;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class LoginPresenter implements LoginMVP.Presenter, LoginMVP.Model, LoginMVP.LoginFacebookModel, LoginMVP.LoginGoogleModel {

    private LoginMVP.View view;
    private LoginModel loginModel;
    private LoginFacebookModel loginFacebookModel;
    private LoginGoogleModel loginGoogleModel;


    public LoginPresenter(LoginMVP.View view) {
        this.view = view;
    }


    @Override
    public void getHandleLogin(Activity activity, String email, String password) {
        loginModel = new LoginModel(this);
        loginModel.handleLogin(activity, email, password);
    }

    @Override
    public void getHandleLoginFacebook(Activity activity, AccessToken accessToken) {
        loginFacebookModel=new LoginFacebookModel(this);
        loginFacebookModel.handleFacebookAccessToken(activity, accessToken);
    }

    @Override
    public void getHandleLoginGoogle(Activity activity, GoogleSignInAccount googleSignInAccount) {
        loginGoogleModel=new LoginGoogleModel(this);
        loginGoogleModel.handleLoginGoogle(activity, googleSignInAccount);
    }

    //--------------View-------------------------------
    @Override
    public void emptyData() {
        view.noticeEmptyData();
    }

    @Override
    public void onLoginSuccess() {
        view.processLogin();
    }

    @Override
    public void onLoginFailed() {
        view.noticeLoginFailed();
    }


    @Override
    public void onLoginFacebookSuccess() {
        view.processLoginWithFacebook();
    }

    @Override
    public void onLoginFacebookFailed() {
        view.noticeLoginFacebookFailed();
    }


    @Override
    public void onLoginGoogleSuccess() {
        view.processLoginWithGoogle();
    }

    @Override
    public void onLoginGoogleFailed() {
        view.noticeLoginGoogleFailed();
    }
}
