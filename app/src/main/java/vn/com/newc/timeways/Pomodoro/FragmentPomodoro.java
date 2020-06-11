package vn.com.newc.timeways.Pomodoro;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import vn.com.newc.timeways.Main.MainActivity;
import vn.com.newc.timeways.R;

public class FragmentPomodoro extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView imgPomodoroClock;
    private TextView txtPomodoroTime, txtTitle;
    private Button btnFinishTheWork, btnPauseTheWork, btnAddWorkToList, btnCancelAddWork;
    private Dialog dialogAddPomodoro;
    private AlertDialog.Builder alertDialogRestPomodoro, alertDialogReturnWorkPomodoro;
    private DatabaseReference mData;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userID;
    private EditText edtAddWorkContent;
    private boolean isWorkAvailable = false;
    private boolean isWorkTime = true, isRestTime = false;
    private ListView listViewPomodoroWork;
    private PomodoroListAdapter listAdapter;
    private ArrayList<PomodoroWork> workArrayList;
    private int numberSessionWork = 0;
    private PomodoroWork pomodoroWork;
    private CountDownTimer countDownTimerPomodoro, countDownTimerRest;
    private Intent intentNotificationReceiver;
    private PendingIntent pendingIntent;
    private Context context;
    private AlarmManager alarmManager;

    @Nullable
    @Override
    //Hàm main
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pomodoro, container, false);

        init();
        initDatabase();
        regiterListener();
        initPomodoroWorkList();
        return view;
    }

    //tạo danh sách công việc
    private void initPomodoroWorkList() {
        mData.child("Pomodoro").child(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                pomodoroWork = dataSnapshot.getValue(PomodoroWork.class);
                workArrayList.add(pomodoroWork);
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //khởi tạo dữ liệu database Firebase
    private void initDatabase() {
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();
    }

    //đăng ký listener onClick cho các view
    private void regiterListener() {
        imgPomodoroClock.setOnClickListener(this);
        btnFinishTheWork.setOnClickListener(this);
        btnPauseTheWork.setOnClickListener(this);
        //btnRest.setOnClickListener(this);
    }

    //khởi tạo view
    private void init() {
        context=view.getContext();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        listViewPomodoroWork = (ListView) view.findViewById(R.id.listViewPomodoroWorkList);
        workArrayList = new ArrayList<>();
        listAdapter = new PomodoroListAdapter(view.getContext(), R.layout.custom_row_pomodoro_list, workArrayList);
        listViewPomodoroWork.setAdapter(listAdapter);
        txtTitle = (TextView) view.findViewById(R.id.textViewTitle);
        txtPomodoroTime = (TextView) view.findViewById(R.id.textViewPomodoroTime);
        imgPomodoroClock = (ImageView) view.findViewById(R.id.imageViewPomodoroClock);
        btnFinishTheWork = (Button) view.findViewById(R.id.buttonFinishTheWork);
        btnFinishTheWork.setVisibility(View.INVISIBLE);
        btnPauseTheWork = (Button) view.findViewById(R.id.buttonPauseTheWork);
        btnPauseTheWork.setVisibility(View.INVISIBLE);
        //btnRest=(Button) view.findViewById(R.id.buttonRest);
    }

    //implement onClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewPomodoroClock:
                if (isWorkAvailable==false){
                    openAddWorkDialog();
                }else {
                    Toast.makeText(view.getContext(), "Bạn phải hoàn thành công việc hiện tại.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonFinishTheWork:
                finishTheWork();
                break;
            case R.id.buttonPauseTheWork:
                if(btnPauseTheWork.getText().equals("Tạm hoãn")){
                    btnPauseTheWork.setText("Quay lại phiên");
                    interruptTheWork();
                } else if (btnPauseTheWork.getText().equals("Quay lại phiên")){
                    btnPauseTheWork.setText("Tạm hoãn");
                    returnSessionWork(pomodoroWork);

                }
                break;
            case R.id.buttonAddWorkPomodoro:
                String workName=edtAddWorkContent.getText().toString().trim();
                if (workName.isEmpty()){
                    Toast.makeText(view.getContext(), "Bạn phải nhập tên công việc.", Toast.LENGTH_SHORT).show();
                }else {
                    handleSaveWork(workName);
                    btnFinishTheWork.setVisibility(View.VISIBLE);
                    btnPauseTheWork.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.buttonCancelAddWorkPomodoro:
                dialogAddPomodoro.cancel();
                break;

            default:
                break;
        }
    }

    //quay trở lại ban đầu phiên làm việc
    private void returnSessionWork(PomodoroWork pomodoroWork) {

        if (isWorkTime==true && isRestTime==false){
            handlePomodoroSession(pomodoroWork);
        }else if (isRestTime==true && isWorkTime==false){
            handleRestSession();
        }
    }

    //hoãn phiên pomodoro/rest
    private void interruptTheWork() {
        if (isWorkTime==true && isRestTime==false){
            countDownTimerPomodoro.cancel();
            txtPomodoroTime.setText("25:00");
        }else if (isRestTime==true && isWorkTime==false){
            countDownTimerRest.cancel();
            txtPomodoroTime.setText("5:00");
        }
        Toast.makeText(view.getContext(), "Bạn đã gián đoạn phiên trong công việc " + pomodoroWork.workName, Toast.LENGTH_SHORT).show();
    }

    //hoàn thành phiên
    private void finishTheWork() {

        numberSessionWork=0;
        isWorkAvailable=false;
        countDownTimerPomodoro.cancel();
        countDownTimerRest.cancel();
        txtPomodoroTime.setText("25:00");
        Toast.makeText(view.getContext(), "Bạn đã hoàn thành "+pomodoroWork.workName+" với "+pomodoroWork.workSession+" Pomodoro", Toast.LENGTH_SHORT).show();
        btnFinishTheWork.setVisibility(View.INVISIBLE);
        btnPauseTheWork.setVisibility(View.INVISIBLE);
    }

    // mở hộp thoại thêm công việc
    private void openAddWorkDialog() {
        dialogAddPomodoro = new Dialog(view.getContext());
        dialogAddPomodoro.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddPomodoro.setContentView(R.layout.dialog_add_pomodoro_work);

        edtAddWorkContent = (EditText) dialogAddPomodoro.findViewById(R.id.editTextPomodoroAddWorkContentDialog);
        btnAddWorkToList = (Button) dialogAddPomodoro.findViewById(R.id.buttonAddWorkPomodoro);
        btnCancelAddWork = (Button) dialogAddPomodoro.findViewById(R.id.buttonCancelAddWorkPomodoro);

        btnAddWorkToList.setOnClickListener(this);
        btnCancelAddWork.setOnClickListener(this);

        dialogAddPomodoro.show();

    }

    //xử lý lưu công việc Pomodoro lên database
    private void handleSaveWork(final String workName) {
        if (workName.isEmpty()){
            Toast.makeText(view.getContext(), "Bạn phải điền tên công việc", Toast.LENGTH_SHORT).show();
        }else {
            //Xử lý dữ liệu tên công việc và thời gian hiện tại của công việc
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");
            String stringcurrentDate = simpleDateFormat.format(currentTime);
            String stringCurrentTime = simpleTimeFormat.format(currentTime);
            String workID = mData.push().getKey();

            pomodoroWork = new PomodoroWork(workID, workName, stringcurrentDate, stringCurrentTime,0);
            mData.child("Pomodoro").child(userID).child(workID).setValue(pomodoroWork, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError==null){
                        Toast.makeText(view.getContext(), "Lưu công việc "+ workName+". Bắt đầu thời gian làm việc!", Toast.LENGTH_SHORT).show();
                        dialogAddPomodoro.cancel();
                        isWorkAvailable = true;
                        workArrayList.clear();
                        initPomodoroWorkList();
                        handlePomodoroSession(pomodoroWork);
                    }else {
                        Toast.makeText(view.getContext(), "Xảy ra lỗi, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // xử lý phiên làm việc của Pomodoro
    private void handlePomodoroSession(final PomodoroWork pomodoroWork) {
        isWorkTime=true;
        isRestTime=false;
        // 1 giây = 1000 millisecond
        countDownTimerPomodoro = new CountDownTimer(15000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes= (int) (millisUntilFinished / 1000) /60;
                int second= (int) (millisUntilFinished / 1000) % 60;

                String timePomodoroFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, second);
                txtPomodoroTime.setText(timePomodoroFormatted);
            }

            @Override
            public void onFinish() {
                numberSessionWork++;
                String timedOutNotice="Đã hết thời gian làm việc, hãy nghỉ ngơi nào.";
                addCompleteNotification(timedOutNotice);
                updateSessionForWork(numberSessionWork, pomodoroWork);
                showRestDialog();
            }
        }.start();
    }

    //xử lý thời gian nghỉ ngơi giữa các phiên pomodoro
    private void handleRestSession() {
        isWorkTime=false;
        isRestTime=true;
        countDownTimerRest = new CountDownTimer(5000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes= (int) (millisUntilFinished / 1000) /60;
                int second= (int) (millisUntilFinished / 1000) % 60;

                String timePomodoroFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, second);
                txtPomodoroTime.setText(timePomodoroFormatted);
            }

            @Override
            public void onFinish() {
                String timedOutNotice="Đã hết thời gian nghỉ ngơi, quay lại với phiên làm việc nào.";
                showReturnWorkDialog();
                addCompleteNotification(timedOutNotice);
            }
        }.start();
    }

    //cập nhật phiên pomodoro cho công việc
    private void updateSessionForWork(int numberSessionWork, PomodoroWork pomodoroWork) {
        mData.child("Pomodoro").child(userID).child(pomodoroWork.workID).child("workSession").setValue(numberSessionWork);
        workArrayList.clear();
        initPomodoroWorkList();
    }

    // thêm thông báo khi kết thúc phiên Pomodoro
    private void addCompleteNotification(String notification) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.icon_app)
                        .setContentTitle("TIMEWAYS - Pomodoro")
                        .setContentText(notification);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                0);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    //hiển thị hộp thoại nghỉ ngơi
    private void showRestDialog() {
        alertDialogRestPomodoro = new AlertDialog.Builder(view.getContext());
        alertDialogRestPomodoro.setCancelable(false);
        alertDialogRestPomodoro.setTitle("Hoàn thành phiên Pomodoro");
        alertDialogRestPomodoro.setMessage("Bạn đã hoàn thành công việc hay muốn nghỉ ngơi?");
        alertDialogRestPomodoro.setPositiveButton("Hoàn thành", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                numberSessionWork=0;
                isWorkAvailable=false;
                Toast.makeText(view.getContext(), "Bạn đã hoàn thành "+pomodoroWork.workName+" với "+pomodoroWork.workSession+" Pomodoro", Toast.LENGTH_SHORT).show();
                txtPomodoroTime.setText("25:00");
                btnFinishTheWork.setVisibility(View.INVISIBLE);
                btnPauseTheWork.setVisibility(View.INVISIBLE);
            }
        });
        alertDialogRestPomodoro.setNegativeButton("Nghỉ ngơi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isRestTime=true;
                isWorkTime=false;
                handleRestSession();
            }
        });

        alertDialogRestPomodoro.show();
    }

    //hiển thị hộp thoại quay trở lại công việc
    private void showReturnWorkDialog() {
        alertDialogReturnWorkPomodoro = new AlertDialog.Builder(view.getContext());
        alertDialogReturnWorkPomodoro.setCancelable(false);
        alertDialogReturnWorkPomodoro.setTitle("Hết thời gian nghỉ ngơi");
        alertDialogReturnWorkPomodoro.setMessage("Trở lại với công việc nào!");
        alertDialogReturnWorkPomodoro.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handlePomodoroSession(pomodoroWork);
            }
        });
        alertDialogReturnWorkPomodoro.setNegativeButton("Hoàn thành công việc", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                numberSessionWork=0;
                isWorkAvailable=false;
                Toast.makeText(view.getContext(), "Bạn đã hoàn thành "+pomodoroWork.workName+" với "+pomodoroWork.workSession+" Pomodoro", Toast.LENGTH_SHORT).show();
                txtPomodoroTime.setText("25:00");
                btnFinishTheWork.setVisibility(View.INVISIBLE);
                btnPauseTheWork.setVisibility(View.INVISIBLE);
            }
        });
        alertDialogReturnWorkPomodoro.show();
    }
}
