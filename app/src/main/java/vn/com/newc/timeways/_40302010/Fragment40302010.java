package vn.com.newc.timeways._40302010;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.time.Duration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import vn.com.newc.timeways.R;

public class Fragment40302010 extends Fragment implements View.OnClickListener {

    Context context;
    View view;
    EditText edtTimeForWork, edtTime40, edtTime30, edtTime20, edtTime10;
    Button btnCalculateTime, btnResetTime;
    TextView txtTime40, txtTime30, txtTime20, txtTime10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_40_30_20_10, container, false);
        init();
        registerListener();
        return view;
    }

    private void registerListener() {
        btnCalculateTime.setOnClickListener(this);
        btnResetTime.setOnClickListener(this);
    }

    private void init() {
        context=view.getContext();
        edtTimeForWork= (EditText) view.findViewById(R.id.editTextTimeForWork);
        edtTime40= (EditText) view.findViewById(R.id.editTextTime40);
        edtTime30= (EditText) view.findViewById(R.id.editTextTime30);
        edtTime20= (EditText) view.findViewById(R.id.editTextTime20);
        edtTime10= (EditText) view.findViewById(R.id.editTextTime10);
        btnCalculateTime = (Button) view.findViewById(R.id.buttonCalculatorTime);
        btnResetTime = (Button) view.findViewById(R.id.buttonResetTime);
        txtTime40 = (TextView) view.findViewById(R.id.textViewWork40);
        txtTime30 = (TextView) view.findViewById(R.id.textViewWork30);
        txtTime20 = (TextView) view.findViewById(R.id.textViewWork20);
        txtTime10 = (TextView) view.findViewById(R.id.textViewWork10);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonCalculatorTime:
                checkCondition();
                break;
            case R.id.buttonResetTime:
                resetTime();
                break;
        }
    }

    private void resetTime() {
        edtTimeForWork.setText("");
        edtTime40.setText("");
        edtTime30.setText("");
        edtTime20.setText("");
        edtTime10.setText("");
        String empty="Chưa có công việc";
        txtTime10.setText(empty);
        txtTime20.setText(empty);
        txtTime30.setText(empty);
        txtTime40.setText(empty);
    }

    private void checkCondition() {
        String txtTimeTimeForWork=edtTimeForWork.getText().toString().trim();
        String txtTime40=edtTime40.getText().toString().trim();
        String txtTime30=edtTime30.getText().toString().trim();
        String txtTime20=edtTime20.getText().toString().trim();
        String txtTime10=edtTime10.getText().toString().trim();
        if (txtTimeTimeForWork.isEmpty() || txtTime10.isEmpty() || txtTime20.isEmpty() || txtTime30.isEmpty() || txtTime40.isEmpty()){
            Toast.makeText(context, "Vui lòng điền đầy thông tin.", Toast.LENGTH_SHORT).show();
        }
        else {
            if(Integer.parseInt(txtTimeTimeForWork)>24){
                Toast.makeText(context, "Bạn hãy chọn thời gian nhỏ hơn 24 giờ", Toast.LENGTH_SHORT).show();
            } else if (Integer.parseInt(txtTimeTimeForWork)<1){
                Toast.makeText(context, "Bạn hãy chọn thời gian lớn hơn 1 giờ", Toast.LENGTH_SHORT).show();
            } else {
                handleCalculateTime(txtTimeTimeForWork, txtTime40, txtTime30, txtTime20, txtTime10);
            }
        }
    }

    private void handleCalculateTime(String txtTimeTimeForWork, String txtTime40, String txtTime30, String txtTime20, String txtTime10) {
        try {
            long duration = TimeUnit.HOURS.toMillis(Long.parseLong(txtTimeTimeForWork));
            long time40Percent = duration*40/100;
            long time30Percent = duration*30/100;
            long time20Percent = duration*20/100;
            long time10Percent = duration*10/100;

            this.txtTime40.setText(txtTime40+"\n"+stringAfterFormat(time40Percent));
            this.txtTime30.setText(txtTime30+"\n"+stringAfterFormat(time30Percent));
            this.txtTime20.setText(txtTime20+"\n"+stringAfterFormat(time20Percent));
            this.txtTime10.setText(txtTime10+"\n"+stringAfterFormat(time10Percent));
            Toast.makeText(context, "Thời gian đã được tính toán", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(context, "có lỗi xảy ra", Toast.LENGTH_SHORT).show();
            Log.e("Error40302010", ""+e.getMessage());
        }

    }

    private String stringAfterFormat(long duration) {
        int seconds = (int) (duration / 1000) % 60 ;
        int minutes = (int) ((duration / (1000*60)) % 60);
        int hours   = (int) ((duration / (1000*60*60)) % 24);
        String hoursFormate24="";
        if (hours<10){
            hoursFormate24= String.valueOf(hours);
        }else {
            hoursFormate24="0"+hours;
        }
        return String.valueOf(hoursFormate24+":"+minutes+":"+seconds);
    }


}
