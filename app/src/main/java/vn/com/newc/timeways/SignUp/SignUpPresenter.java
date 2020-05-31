package vn.com.newc.timeways.SignUp;

import android.app.Activity;

public class SignUpPresenter implements SignUpMVP.Presenter, SignUpMVP.Model {

    private SignUpModel model;
    private SignUpMVP.View view;

    public SignUpPresenter(SignUpMVP.View view) {
        this.view = view;
    }

    @Override
    public void getHandleRegistration(Activity activity, String email, String password, String retypePassword) {
        model=new SignUpModel(this);
        model.handleSignUp(activity, email, password, retypePassword);
    }

    @Override
    public void missingMessageEntered() {
        view.notifyMissingEnterd();
    }

    @Override
    public void passwordNotSame() {
        view.notifyPasswordNotSame();
    }

    @Override
    public void onSignUpSuccess() {
        view.processingRegistration();
    }

    @Override
    public void onSignUpFailed() {
        view.notifySignUpFailed();
    }
}
