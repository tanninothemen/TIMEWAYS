package vn.com.newc.timeways.Login;

import android.app.Activity;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface LoginMVP {
    interface View{
        void noticeEmptyData();

        void processLogin();

        void noticeLoginFailed();

        void processLoginWithFacebook();

        void noticeLoginFacebookFailed();

        void processLoginWithGoogle();

        void noticeLoginGoogleFailed();
    }

    interface Presenter{
        void getHandleLogin(Activity activity, String email, String password);

        void getHandleLoginFacebook(Activity activity, AccessToken accessToken);


        void getHandleLoginGoogle(Activity activity, GoogleSignInAccount googleSignInAccount);
    }

    interface Model{
        void emptyData();

        void onLoginSuccess();

        void onLoginFailed();
    }

    interface LoginFacebookModel{

        void onLoginFacebookSuccess();

        void onLoginFacebookFailed();
    }

    interface LoginGoogleModel{

        void onLoginGoogleSuccess();

        void onLoginGoogleFailed();
    }
}
