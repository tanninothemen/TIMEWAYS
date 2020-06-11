package vn.com.newc.timeways.SignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import vn.com.newc.timeways.Login.LoginActivity;
import vn.com.newc.timeways.R;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, SignUpMVP.View {

    private EditText edtEmail, edtPassword, edtRetypePassword;
    private Button btnSignUpAccount, btnBackToLogin;
    private SignUpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();
        initPresenter();
        registerListener();
    }

    private void initPresenter() {
        presenter=new SignUpPresenter(this);
    }

    private void registerListener() {
        btnSignUpAccount.setOnClickListener(this);
        btnBackToLogin.setOnClickListener(this);
    }

    private void init() {
        edtEmail            =(EditText) findViewById(R.id.editTextEnterEmail);
        edtPassword         =(EditText) findViewById(R.id.editTextEnterPassword);
        edtRetypePassword   =(EditText) findViewById(R.id.editTextRetypePassword);
        btnSignUpAccount    =(Button) findViewById(R.id.buttonSignUpAccount);
        btnBackToLogin      =(Button) findViewById(R.id.buttonBackToLogin);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonSignUpAccount:
                String email            =edtEmail.getText().toString().trim();
                String password         =edtPassword.getText().toString().trim();
                String retypePassword   =edtRetypePassword.getText().toString().trim();
                presenter.getHandleRegistration(this, email, password, retypePassword);
                break;
            case R.id.buttonBackToLogin:
                Intent intent=new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void notifyMissingEnterd() {
        Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void notifyPasswordNotSame() {
        Toast.makeText(this, "Mật khẩu không trùng khớp. Vui lòng nhập lại.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void processingRegistration() {
        Toast.makeText(this, "Tạo tài khoản thành công. Hãy đăng nhập bằng tài khoản của bạn", Toast.LENGTH_SHORT).show();
        Intent intentBackToLogin=new Intent(this, LoginActivity.class);
        startActivity(intentBackToLogin);
    }

    @Override
    public void notifySignUpFailed() {
        Toast.makeText(this, "Email này đã được sử dụng. Vui lòng sử dụng tài khoản email khác.", Toast.LENGTH_SHORT).show();
    }
}
