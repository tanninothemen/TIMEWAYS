package vn.com.newc.timeways.ToDoList;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import vn.com.newc.timeways.R;
import vn.com.newc.timeways.Work;

public class FragmentToDoList extends Fragment implements View.OnClickListener {

    private View view;
    private ListView lvToDoList;
    private ArrayList<Work> workArrayList;
    private ToDoListWorkAdapter toDoListWorkAdapter;
    private Dialog dialogAddToDoList, dialogUpdateToDoList;
    private EditText edtAddWorkContent, edtUpdateWorkContent;
    private EditText edtAddWorkDue, edtUpdateWorkDue;
    private Button btnAddWorkToList, btnUpdateWorkToList;
    private Button btnCancelAddWork, btnCancelUpdateWork;
    private TimePickerDialog timePickerDialog;
    private Calendar calendarAdd, calendarUpdate;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userID;
    private DatabaseReference mData;
    private ImageButton imgbtnAddToDoListWork;
    private Work work;
    public static Context context;
    private int hour, minutes;

    //  Hàm chính
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_to_do_list, container, false);

        init();
        registerListener();
        getDataIntoListView();

        return view;
    }

    //đổ dữ liệu công việc vào listview
    private void getDataIntoListView() {
        mData.child("ToDoList").child(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Work work = dataSnapshot.getValue(Work.class);
                workArrayList.add(work);
                toDoListWorkAdapter.notifyDataSetChanged();
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

    // Hàm đăng ký listener
    private void registerListener() {
        imgbtnAddToDoListWork.setOnClickListener(this);
    }

    // Hàm khởi tạo
    private void init() {
        context                     = view.getContext();
        mData                       = FirebaseDatabase.getInstance().getReference();
        mAuth                       = FirebaseAuth.getInstance();
        user                        = mAuth.getCurrentUser();
        userID                      = user.getUid();
        imgbtnAddToDoListWork       = (ImageButton) view.findViewById(R.id.imageButtonAddWorkToDoList);
        lvToDoList                  = (ListView) view.findViewById(R.id.listViewToDoList);
        workArrayList               = new ArrayList<>();
        toDoListWorkAdapter         = new ToDoListWorkAdapter(view.getContext(), R.layout.custom_row_todolist, workArrayList);
        lvToDoList.setAdapter(toDoListWorkAdapter);


    }

    // implement OnClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButtonAddWorkToDoList:
                initDialogAddWork();
                break;
            case R.id.buttonAddWorkToToDoList:
                checkDataOfWork();
                break;
            case R.id.buttonCancelWorkToDoList:
                dialogAddToDoList.cancel();
                break;
            case R.id.editTextAddWorkDueDialog:
                openAddTimePicker();
                break;
            case R.id.buttonCancelUpdateWorkToDoList:
                dialogUpdateToDoList.cancel();
                break;
            case R.id.editTextUpdateWorkDueDialog:
                openUpdateTimePicker();
                break;
            case R.id.buttonUpdateWorkToToDoList:
                String workUpdateContent=edtUpdateWorkContent.getText().toString().trim();
                long workUpdateDue=calendarUpdate.getTimeInMillis();
                Toast.makeText(context, ""+workUpdateContent+" - "+workUpdateDue, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // Kiểm tra dữ liệu của công việc được thêm
    private void checkDataOfWork() {
        String workContent = edtAddWorkContent.getText().toString().trim();
        String workDue = edtAddWorkDue.getText().toString().trim();
        if (workContent.isEmpty()) {
            Toast.makeText(context, "Bạn phải điền tên công việc", Toast.LENGTH_SHORT).show();
        } else {
            long workDueMillis;
            if (workDue.isEmpty()) {
                workDueMillis = 0;
            } else {
                workDueMillis = calendarAdd.getTimeInMillis();
            }
            saveWorkOnDatabase(workContent, workDueMillis);
        }
    }

    //Lưu công việc có thời hạn
    private void saveWorkOnDatabase(String workContent, long workDue) {
        if (workDue == 0) {
            work = new Work(workContent, (long) 0);
        } else {
            work = new Work(workContent, workDue);
        }
        mData.child("ToDoList").child(userID).push().setValue(work, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Toast.makeText(context, "Đã thêm 1 công việc mới", Toast.LENGTH_SHORT).show();
                    dialogAddToDoList.cancel();
                } else {
                    Toast.makeText(context, "Lỗi thêm công việc", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // mở dialog thêm thời gian đáo hạn
    private void openAddTimePicker() {
        calendarAdd = Calendar.getInstance();
        hour = calendarAdd.get(Calendar.HOUR_OF_DAY);
        minutes = calendarAdd.get(Calendar.MINUTE);
        // time picker dialog
        timePickerDialog = new TimePickerDialog(view.getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        calendarAdd.set(0, 0, 0, sHour, sMinute, 0);
                        edtAddWorkDue.setText(sHour + ":" + sMinute);
                    }
                }, hour, minutes, true);
        timePickerDialog.show();
    }

    // mở dialog update thời gian đáo hạn
    private void openUpdateTimePicker() {
        calendarUpdate = Calendar.getInstance();
        // time picker dialog
        timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        calendarUpdate.set(0, 0, 0, sHour, sMinute, 0);
                        edtAddWorkDue.setText(sHour + ":" + sMinute);
                    }
                }, hour, minutes, true);
        timePickerDialog.show();
    }

    // khởi tạo dialog thêm công việc to do list
    private void initDialogAddWork() {
        dialogAddToDoList = new Dialog(context);
        dialogAddToDoList.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddToDoList.setContentView(R.layout.dialog_todolist_add);

        edtAddWorkContent = (EditText) dialogAddToDoList.findViewById(R.id.editTextAddWorkContentDialog);
        edtAddWorkDue = (EditText) dialogAddToDoList.findViewById(R.id.editTextAddWorkDueDialog);
        edtAddWorkDue.setInputType(InputType.TYPE_NULL);
        btnAddWorkToList = (Button) dialogAddToDoList.findViewById(R.id.buttonAddWorkToToDoList);
        btnCancelAddWork = (Button) dialogAddToDoList.findViewById(R.id.buttonCancelWorkToDoList);

        btnAddWorkToList.setOnClickListener(this);
        btnCancelAddWork.setOnClickListener(this);
        edtAddWorkDue.setOnClickListener(this);

        dialogAddToDoList.show();
    }

    // khởi tạo dialog sửa công việc to do list
    public void initDialogUpdateWork() {
        dialogUpdateToDoList = new Dialog(context);
        dialogUpdateToDoList.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUpdateToDoList.setContentView(R.layout.dialog_todolist_update);

        edtUpdateWorkContent = (EditText) dialogUpdateToDoList.findViewById(R.id.editTextUpdateWorkContentDialog);
        edtUpdateWorkDue = (EditText) dialogUpdateToDoList.findViewById(R.id.editTextUpdateWorkDueDialog);
        edtUpdateWorkDue.setInputType(InputType.TYPE_NULL);
        btnUpdateWorkToList = (Button) dialogUpdateToDoList.findViewById(R.id.buttonUpdateWorkToToDoList);
        btnCancelUpdateWork = (Button) dialogUpdateToDoList.findViewById(R.id.buttonCancelUpdateWorkToDoList);

        edtUpdateWorkContent.setText("");
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String timeWorkDue = format.format("");
        edtUpdateWorkDue.setText(timeWorkDue);

        btnUpdateWorkToList.setOnClickListener(this);
        btnCancelUpdateWork.setOnClickListener(this);
        edtUpdateWorkDue.setOnClickListener(this);

        dialogUpdateToDoList.show();

    }

}
