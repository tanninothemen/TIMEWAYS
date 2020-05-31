package vn.com.newc.timeways.SignUp;

import android.app.Activity;

public interface SignUpMVP {
    interface Model{

        void missingMessageEntered();

        void passwordNotSame();

        void onSignUpSuccess();

        void onSignUpFailed();
    }
    interface View{

        void notifyMissingEnterd();

        void notifyPasswordNotSame();

        void processingRegistration();

        void notifySignUpFailed();
    }
    interface Presenter{
        void getHandleRegistration(Activity activity, String email, String password, String retypePassword);
    }
}
