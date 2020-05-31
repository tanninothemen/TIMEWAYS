package vn.com.newc.timeways.ToDoList;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.com.newc.timeways.R;

public class FragmentToDoList extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private View view;
    private ListView lvToDoList;
    public static ArrayList<ToDoListWork> toDoListWorkArrayList;
    public static ArrayList<ToDoListWork> toDoListWorkCompleteArrayList;
    public static ToDoListWorkAdapter toDoListWorkAdapter;
    public static ToDoListWorkCompleteAdapter toDoListWorkCompleteAdapter;
    private Dialog dialogAddToDoList, dialogUpdateToDoList;
    private EditText edtAddWorkDateDue, edtAddWorkTimeDue, edtUpdateWorkDue, edtAddWorkContent, edtUpdateWorkContent;
    private Button btnAddWorkToList, btnUpdateWorkToList;
    private Button btnCancelAddWork, btnCancelUpdateWork;
    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;
    private Calendar calendarAddDate, calendarAddTime, calendarUpdate;
    public static FirebaseAuth mAuth;
    public static FirebaseUser user;
    public static String userID;
    public static DatabaseReference mData;
    private ImageButton imgbtnAddToDoListWork;
    public static Context context;
    private int day, month, year, hour, minutes;
    private Spinner spinnerWorkStatus;
    private List<String> arrayStatusWorkSpinner;
    private ArrayAdapter<String> arrayAdapterSpinner;

    //  Hàm chính
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_to_do_list, container, false);

        init();
        initDatabase();
        registerListener();

        return view;
    }

    // khởi tạo view
    private void init() {
        context = view.getContext();
        spinnerWorkStatus = (Spinner) view.findViewById(R.id.spinnerToDoListStatusWork);
        arrayStatusWorkSpinner = new ArrayList<>();
        arrayStatusWorkSpinner.add("CÔNG VIỆC ĐANG THỰC HIỆN");
        arrayStatusWorkSpinner.add("CÔNG VIỆC ĐÃ HOÀN THÀNH");
        arrayAdapterSpinner = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, arrayStatusWorkSpinner);
        arrayAdapterSpinner.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinnerWorkStatus.setAdapter(arrayAdapterSpinner);

        imgbtnAddToDoListWork = (ImageButton) view.findViewById(R.id.imageButtonAddWorkToDoList);
        lvToDoList = (ListView) view.findViewById(R.id.listViewToDoList);
        toDoListWorkArrayList = new ArrayList<>();
        toDoListWorkCompleteArrayList=new ArrayList<>();
        toDoListWorkAdapter = new ToDoListWorkAdapter(context, R.layout.custom_row_todolist, toDoListWorkArrayList);
        toDoListWorkCompleteAdapter = new ToDoListWorkCompleteAdapter(context, R.layout.custom_row_todolist, toDoListWorkCompleteArrayList);
        lvToDoList.setAdapter(toDoListWorkAdapter);
    }

    //khởi tạo dữ liệu database
    private void initDatabase() {
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();
    }

    // khởi tạo listview đang thực hiện
    public static void initListViewToDoList(String typeToDoList, final ArrayList<ToDoListWork> arrayList, final BaseAdapter baseAdapter) {
        mData.child("ToDoList").child(userID).child(typeToDoList).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ToDoListWork toDoListWork = dataSnapshot.getValue(ToDoListWork.class);
                arrayList.add(toDoListWork);
                baseAdapter.notifyDataSetChanged();
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

    // đăng ký listener
    private void registerListener() {
        imgbtnAddToDoListWork.setOnClickListener(this);
        lvToDoList.setOnItemClickListener(this);
        spinnerWorkStatus.setOnItemSelectedListener(this);
    }

    // implement OnClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButtonAddWorkToDoList:
                initDialogAddWork();
                break;
            case R.id.editTextToDoListAddWorkDateDueDialog:
                openAddDatePicker();
                break;
            case R.id.editTextToDoListAddWorkTimeDueDialog:
                openAddTimePicker();
                break;
            case R.id.buttonToDoListAddWork:
                try {
                    checkAddWorkData();
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Lỗi thêm công việc " + e.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonToDoListCancelWork:
                dialogAddToDoList.cancel();
                break;
            case R.id.buttonCancelUpdateWorkToDoList:
                dialogUpdateToDoList.cancel();
                break;
            case R.id.editTextUpdateWorkDueDialog:
                break;
            case R.id.buttonUpdateWorkToToDoList:
                break;
        }
    }

    // khởi tạo dialog thêm công việc to do list
    private void initDialogAddWork() {
        dialogAddToDoList = new Dialog(context);
        dialogAddToDoList.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddToDoList.setContentView(R.layout.dialog_todolist_add);

        edtAddWorkContent = (EditText) dialogAddToDoList.findViewById(R.id.editTextToDoListAddWorkContentDialog);
        edtAddWorkDateDue = (EditText) dialogAddToDoList.findViewById(R.id.editTextToDoListAddWorkDateDueDialog);
        edtAddWorkTimeDue = (EditText) dialogAddToDoList.findViewById(R.id.editTextToDoListAddWorkTimeDueDialog);
        btnAddWorkToList = (Button) dialogAddToDoList.findViewById(R.id.buttonToDoListAddWork);
        btnCancelAddWork = (Button) dialogAddToDoList.findViewById(R.id.buttonToDoListCancelWork);

        edtAddWorkDateDue.setInputType(InputType.TYPE_NULL);
        edtAddWorkTimeDue.setInputType(InputType.TYPE_NULL);

        edtAddWorkTimeDue.setOnClickListener(this);
        edtAddWorkDateDue.setOnClickListener(this);
        btnAddWorkToList.setOnClickListener(this);
        btnCancelAddWork.setOnClickListener(this);

        dialogAddToDoList.show();
    }

    //dialog ngày đáo hạn
    private void openAddDatePicker() {
        calendarAddDate = Calendar.getInstance();
        day = calendarAddDate.get(Calendar.DAY_OF_MONTH);
        month = calendarAddDate.get(Calendar.MONTH);
        year = calendarAddDate.get(Calendar.YEAR);
        // time picker dialog
        datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yearNow, int monthNow, int dayOfMonthNow) {
                calendarAddDate.set(yearNow, monthNow, dayOfMonthNow, 0, 0, 0);
                edtAddWorkDateDue.setText(dayOfMonthNow + "/" + (monthNow + 1) + "/" + yearNow);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    // dialog thời gian đáo hạn
    private void openAddTimePicker() {
        calendarAddTime = Calendar.getInstance();
        hour = calendarAddTime.get(Calendar.HOUR_OF_DAY);
        minutes = calendarAddTime.get(Calendar.MINUTE);
        // time picker dialog
        timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        calendarAddTime.set(0, 0, 0, sHour, sMinute, 0);
                        if (sMinute < 10) {
                            edtAddWorkTimeDue.setText(sHour + ":0" + sMinute);
                        } else {
                            edtAddWorkTimeDue.setText(sHour + ":" + sMinute);
                        }

                    }
                }, hour, minutes, true);
        timePickerDialog.show();
    }

    //kiểm tra điều kiện trước khi lưu công việc
    private void checkAddWorkData() throws ParseException {
        String workContent = edtAddWorkContent.getText().toString().trim();
        String workDateDue = edtAddWorkDateDue.getText().toString().trim();
        String workTimeDue = edtAddWorkTimeDue.getText().toString().trim();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String currentDateString = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String currentTimeString = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        Date currentDate, selectedDate, currentTime, selectedTime;
        boolean emptyContentData = workContent.isEmpty();
        boolean emptyTimeDue = workTimeDue.isEmpty();
        boolean emptyDateDue = workDateDue.isEmpty();

        if (emptyContentData) {
            Toast.makeText(context, "Bạn phải nhập nội dung công việc", Toast.LENGTH_SHORT).show();
        } else {
            if (emptyTimeDue && emptyDateDue) {
                saveWorkNotDue(workContent);
            } else if (!emptyTimeDue && emptyDateDue) {
                Toast.makeText(context, "Bạn phải chọn ngày đáo hạn công việc", Toast.LENGTH_SHORT).show();
            } else if (emptyTimeDue && !emptyDateDue) {
                currentDate = dateFormat.parse(currentDateString);
                selectedDate = dateFormat.parse(workDateDue);
                boolean selectedDateOvercurrentDate = selectedDate.compareTo(currentDate) < 0;
                if (selectedDateOvercurrentDate) {
                    Toast.makeText(context, "Ngày bạn chọn đã kết thúc", Toast.LENGTH_SHORT).show();
                } else {
                    saveWorkNotTimeDue(workContent, workDateDue);
                }
            } else if (!emptyTimeDue && !emptyDateDue) {
                currentDate = dateFormat.parse(currentDateString);
                selectedDate = dateFormat.parse(workDateDue);
                currentTime = timeFormat.parse(currentTimeString);
                selectedTime = timeFormat.parse(workTimeDue);
                boolean selectedDateOvercurrentDate = selectedDate.compareTo(currentDate) < 0;
                boolean selectedDateEqualcurrentDate = selectedDate.compareTo(currentDate) == 0;
                boolean selectedTimeOvercurrentTime = selectedTime.compareTo(currentTime) < 0;
                if (selectedDateOvercurrentDate) {
                    Toast.makeText(context, "Ngày bạn chọn đã kết thúc", Toast.LENGTH_SHORT).show();
                } else if (selectedDateEqualcurrentDate && selectedTimeOvercurrentTime) {
                    Toast.makeText(context, "Thời gian bạn chọn đã kết thúc", Toast.LENGTH_SHORT).show();
                } else {
                    saveWorkHaveDue(workContent, workDateDue, workTimeDue);
                }
            }
        }
    }

    // lưu công việc có ngày và thời gian đáo hạn
    private void saveWorkHaveDue(String workContent, String workDateDue, String workTimeDue) {
        //Toast.makeText(context, "lưu công việc có ngày và thời gian đáo hạn", Toast.LENGTH_SHORT).show();
        String workID = mData.push().getKey();
        ToDoListWork toDoListWork = new ToDoListWork(workID, workContent, workTimeDue, workDateDue, false);
        mData.child("ToDoList").child(userID).child("Working").child(workID).setValue(toDoListWork, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Toast.makeText(context, "Lưu công việc", Toast.LENGTH_SHORT).show();
                    dialogAddToDoList.cancel();
                    toDoListWorkArrayList.clear();
                    initListViewToDoList("Working", toDoListWorkArrayList, toDoListWorkAdapter);
                } else {
                    Toast.makeText(context, "Xảy ra lỗi! Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // lưu công việc không có đáo hạn
    private void saveWorkNotDue(String workContent) {
        //Toast.makeText(context, "lưu công việc không có đáo hạn", Toast.LENGTH_SHORT).show();
        String workID = mData.push().getKey();
        ToDoListWork toDoListWork = new ToDoListWork(workID, workContent, false);
        mData.child("ToDoList").child(userID).child("Working").child(workID).setValue(toDoListWork, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Toast.makeText(context, "Lưu công việc", Toast.LENGTH_SHORT).show();
                    dialogAddToDoList.cancel();
                    toDoListWorkArrayList.clear();
                    initListViewToDoList("Working", toDoListWorkArrayList, toDoListWorkAdapter);
                } else {
                    Toast.makeText(context, "Xảy ra lỗi! Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // lưu công việc có ngày đáo hạn, nhưng không có thời gian đáo hạn
    private void saveWorkNotTimeDue(String workContent, String workDateDue) {
        //Toast.makeText(context, "lưu công việc không có thời gian đáo hạn nhưng có ngày đáo hạn", Toast.LENGTH_SHORT).show();
        String workID = mData.push().getKey();
        ToDoListWork toDoListWork = new ToDoListWork(workID, workContent, workDateDue, false);
        mData.child("ToDoList").child(userID).child("Working").child(workID).setValue(toDoListWork, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Toast.makeText(context, "Lưu công việc", Toast.LENGTH_SHORT).show();
                    dialogAddToDoList.cancel();
                    toDoListWorkArrayList.clear();
                    initListViewToDoList("Working",toDoListWorkArrayList, toDoListWorkAdapter);
                } else {
                    Toast.makeText(context, "Xảy ra lỗi! Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // thay đổi trạng thái của công việc
    public static void changeStatusToDoList(final ToDoListWork toDoListWork, String workID){
        mData.child("ToDoList").child(userID).child("Working").child(workID).removeValue();
        toDoListWork.workStatus=true;
        mData.child("ToDoList").child(userID).child("Complete").child(workID).setValue(toDoListWork, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError==null){
                    Toast.makeText(context, "Công việc đã chuyển sang danh sách công việc hoàn thành. Bạn có thể xem lại danh sách ở danh mục CÔNG VIỆC ĐÃ HOÀN THÀNH", Toast.LENGTH_LONG).show();
                    toDoListWorkArrayList.clear();
                    initListViewToDoList("Working", toDoListWorkArrayList, toDoListWorkAdapter);
                }
            }
        });
    }


    // Kiểm tra dữ liệu của công việc được thêm
//    private void checkDataOfWork() {
//        String workContent = edtAddWorkContent.getText().toString().trim();
//        String workDue = edtAddWorkDue.getText().toString().trim();
//        if (workContent.isEmpty()) {
//            Toast.makeText(context, "Bạn phải điền tên công việc", Toast.LENGTH_SHORT).show();
//        } else {
//            long workDueMillis;
//            if (workDue.isEmpty()) {
//                workDueMillis = 0;
//            } else {
//                workDueMillis = calendarAdd.getTimeInMillis();
//            }
//            saveWorkOnDatabase(workContent, workDueMillis);
//        }
//    }

    //Lưu công việc có thời hạn
//    private void saveWorkOnDatabase(String workContent, long workDue) {
//        if (workDue == 0) {
//            toDoListWork = new ToDoListWork(workContent, (long) 0);
//        } else {
//            toDoListWork = new ToDoListWork(workContent, workDue);
//        }
//        mData.child("ToDoList").child(userID).push().setValue(toDoListWork, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                if (databaseError == null) {
//                    Toast.makeText(context, "Đã thêm 1 công việc mới", Toast.LENGTH_SHORT).show();
//                    dialogAddToDoList.cancel();
//                } else {
//                    Toast.makeText(context, "Lỗi thêm công việc", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }


    // mở dialog update thời gian đáo hạn
//    private void openUpdateTimePicker() {
//        calendarUpdate = Calendar.getInstance();
//        // time picker dialog
//        timePickerDialog = new TimePickerDialog(context,
//                new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
//                        calendarUpdate.set(0, 0, 0, sHour, sMinute, 0);
//                        edtAddWorkDue.setText(sHour + ":" + sMinute);
//                    }
//                }, hour, minutes, true);
//        timePickerDialog.show();
//    }



    // implement onItemClick


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (arrayStatusWorkSpinner.get(position).equals("CÔNG VIỆC ĐANG THỰC HIỆN")){
            toDoListWorkArrayList.clear();toDoListWorkArrayList.clear();
            lvToDoList.setAdapter(toDoListWorkAdapter);
            initListViewToDoList("Working",toDoListWorkArrayList,toDoListWorkAdapter);
        }
        if (arrayStatusWorkSpinner.get(position).equals("CÔNG VIỆC ĐÃ HOÀN THÀNH")){
            toDoListWorkCompleteArrayList.clear();
            lvToDoList.setAdapter(toDoListWorkCompleteAdapter);
            initListViewToDoList("Complete",toDoListWorkCompleteArrayList, toDoListWorkCompleteAdapter);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    // khởi tạo dialog sửa công việc to do list
//    public void initDialogUpdateWork() {
//        dialogUpdateToDoList = new Dialog(context);
//        dialogUpdateToDoList.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogUpdateToDoList.setContentView(R.layout.dialog_todolist_update);
//
//        edtUpdateWorkContent = (EditText) dialogUpdateToDoList.findViewById(R.id.editTextUpdateWorkContentDialog);
//        edtUpdateWorkDue = (EditText) dialogUpdateToDoList.findViewById(R.id.editTextUpdateWorkDueDialog);
//        edtUpdateWorkDue.setInputType(InputType.TYPE_NULL);
//        btnUpdateWorkToList = (Button) dialogUpdateToDoList.findViewById(R.id.buttonUpdateWorkToToDoList);
//        btnCancelUpdateWork = (Button) dialogUpdateToDoList.findViewById(R.id.buttonCancelUpdateWorkToDoList);
//
//        edtUpdateWorkContent.setText("");
//        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
//        String timeWorkDue = format.format("");
//        edtUpdateWorkDue.setText(timeWorkDue);
//
//        btnUpdateWorkToList.setOnClickListener(this);
//        btnCancelUpdateWork.setOnClickListener(this);
//        edtUpdateWorkDue.setOnClickListener(this);
//
//        dialogUpdateToDoList.show();
//
//    }

}
