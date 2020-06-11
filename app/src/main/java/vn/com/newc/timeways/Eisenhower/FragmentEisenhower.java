package vn.com.newc.timeways.Eisenhower;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
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

public class FragmentEisenhower extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemSelectedListener {
    private View view;
    private Context context;
    private DatabaseReference mData;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userID;
    private ImageButton ibtnAddWorkEisenhower;
    private DatePickerDialog datePickerDialog;
    private Dialog dialogAddEisenhower, dialogEditEisenhower;
    private EditText edtAddWorkContent, edtAddWorkDateStart, edtAddWorkDateEnd,
            edtEditWorkContent, edtEditWorkDateStart, edtEditWorkDateEnd;
    private Button btnAddWorkToList, btnCancelAddWork, btnCompleteWorkList, btnDeletWorkList, btnEditWorkList;
    private Spinner spinnerWorkType, spinnerEditWorkType;
    private ListView lvEisenhowerDO, lvEisenhowerDECIDE, lvEisenhowerDELEGATE, lvEisenhowerDELETE;
    private List<String> typeEisenhowerWork;
    private ArrayAdapter<String> spinnerAdapter;
    private EisenhowerWork eisenhowerWork;
    private Calendar calendar;
    private String workType, workTypeEdit;
    private ArrayList<EisenhowerWork> arrayListEisenhowerDO, arrayListEisenhowerDECIDE, arrayListEisenhowerDELEGATE, arrayListEisenhowerDELETE;
    private EIsenhowerAdaper arrayAdapterDO, arrayAdapterDECIDE, arrayAdapterDELEGATE, arrayAdapterDELETE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_eisenhower, container, false);
        init();
        initDatabase();
        registerListerner();
        initEisenhowerListView(arrayListEisenhowerDO, arrayAdapterDO, "DO");
        initEisenhowerListView(arrayListEisenhowerDECIDE, arrayAdapterDECIDE, "DECIDE");
        initEisenhowerListView(arrayListEisenhowerDELEGATE, arrayAdapterDELEGATE, "DELEGATE");
        initEisenhowerListView(arrayListEisenhowerDELETE, arrayAdapterDELETE, "DELETE");
        return view;
    }

    // khởi tạo view
    private void init() {
        context = view.getContext();
        typeEisenhowerWork = new ArrayList<>();
        typeEisenhowerWork.add("DO");
        typeEisenhowerWork.add("DECIDE");
        typeEisenhowerWork.add("DELEGATE");
        typeEisenhowerWork.add("DELETE");
        spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, typeEisenhowerWork);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ibtnAddWorkEisenhower = (ImageButton) view.findViewById(R.id.buttonAddWorkEisenhower);
        lvEisenhowerDO = (ListView) view.findViewById(R.id.listViewEisenhowerDO);
        lvEisenhowerDECIDE = (ListView) view.findViewById(R.id.listViewEisenhowerDECIDE);
        lvEisenhowerDELEGATE = (ListView) view.findViewById(R.id.listViewEisenhowerDELEGATE);
        lvEisenhowerDELETE = (ListView) view.findViewById(R.id.listViewEisenhowerDELETE);
        arrayListEisenhowerDO = new ArrayList<>();
        arrayListEisenhowerDECIDE = new ArrayList<>();
        arrayListEisenhowerDELEGATE = new ArrayList<>();
        arrayListEisenhowerDELETE = new ArrayList<>();
        arrayAdapterDO = new EIsenhowerAdaper(context, R.layout.custom_row_eisenhower_list, arrayListEisenhowerDO);
        arrayAdapterDECIDE = new EIsenhowerAdaper(context, R.layout.custom_row_eisenhower_list, arrayListEisenhowerDECIDE);
        arrayAdapterDELEGATE = new EIsenhowerAdaper(context, R.layout.custom_row_eisenhower_list, arrayListEisenhowerDELEGATE);
        arrayAdapterDELETE = new EIsenhowerAdaper(context, R.layout.custom_row_eisenhower_list, arrayListEisenhowerDELETE);
        lvEisenhowerDO.setAdapter(arrayAdapterDO);
        lvEisenhowerDECIDE.setAdapter(arrayAdapterDECIDE);
        lvEisenhowerDELEGATE.setAdapter(arrayAdapterDELEGATE);
        lvEisenhowerDELETE.setAdapter(arrayAdapterDELETE);

    }

    // khởi tạo database
    private void initDatabase() {
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();
    }

    // đăng ký các listener
    private void registerListerner() {
        ibtnAddWorkEisenhower.setOnClickListener(this);
        lvEisenhowerDO.setOnItemClickListener(this);
        lvEisenhowerDECIDE.setOnItemClickListener(this);
        lvEisenhowerDELEGATE.setOnItemClickListener(this);
        lvEisenhowerDELETE.setOnItemClickListener(this);
    }

    // khởi tạo các listview Eisenhower
    private void initEisenhowerListView(final ArrayList<EisenhowerWork> arrayListEisenhower, final EIsenhowerAdaper arrayAdapter, String typeWork) {
        mData.child("Eisenhower").child(userID).child(typeWork).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                eisenhowerWork = dataSnapshot.getValue(EisenhowerWork.class);
                arrayListEisenhower.add(eisenhowerWork);
                arrayAdapter.notifyDataSetChanged();
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

    // khởi tạo dialog thêm công việc
    private void openDialogAddWork() {
        dialogAddEisenhower = new Dialog(context);
        dialogAddEisenhower.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddEisenhower.setContentView(R.layout.dialog_add_eisenhower_work);

        edtAddWorkContent = (EditText) dialogAddEisenhower.findViewById(R.id.editTextEisenhowerAddWorkContentDialog);
        edtAddWorkDateStart = (EditText) dialogAddEisenhower.findViewById(R.id.editTextEisenhowerAddDateStart);
        edtAddWorkDateEnd = (EditText) dialogAddEisenhower.findViewById(R.id.editTextEisenhowerAddDateEnd);
        btnAddWorkToList = (Button) dialogAddEisenhower.findViewById(R.id.buttonAddEisenhowerWorkDialog);
        btnCancelAddWork = (Button) dialogAddEisenhower.findViewById(R.id.buttonCancelEisenhowerWorkDialog);
        spinnerWorkType = (Spinner) dialogAddEisenhower.findViewById(R.id.spinnerEisenhowerTypeWork);

        edtAddWorkDateStart.setInputType(InputType.TYPE_NULL);
        edtAddWorkDateEnd.setInputType(InputType.TYPE_NULL);
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        edtAddWorkDateStart.setText(currentDate);
        spinnerWorkType.setAdapter(spinnerAdapter);

        spinnerWorkType.setOnItemSelectedListener(this);
        edtAddWorkDateStart.setOnClickListener(this);
        edtAddWorkDateEnd.setOnClickListener(this);
        btnAddWorkToList.setOnClickListener(this);
        btnCancelAddWork.setOnClickListener(this);

        dialogAddEisenhower.show();
    }

    // implement onclick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAddWorkEisenhower:
                openDialogAddWork();
                break;
            case R.id.buttonAddEisenhowerWorkDialog:
                try {
                    checkConditionSaveWork(edtAddWorkContent, edtAddWorkDateStart, edtAddWorkDateEnd);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e("abc", e.getMessage());
                }
                break;
            case R.id.buttonCancelEisenhowerWorkDialog:
                dialogAddEisenhower.cancel();
                break;
            case R.id.editTextEisenhowerAddDateStart:
                openDialogDatePicker(edtAddWorkDateStart);
                break;
            case R.id.editTextEisenhowerAddDateEnd:
                openDialogDatePicker(edtAddWorkDateEnd);
                break;

        }
    }

    //kiểm tra điều kiện trước khi lưu công việc
    private void checkConditionSaveWork(EditText edtAddWorkContent, EditText edtAddWorkDateStart, EditText edtAddWorkDateEnd) throws ParseException {
        String workName = edtAddWorkContent.getText().toString().trim();
        String workDateStart = edtAddWorkDateStart.getText().toString().trim();
        String workDateEnd = edtAddWorkDateEnd.getText().toString().trim();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date currentTime = Calendar.getInstance().getTime();
        if (workName.isEmpty()) {
            Toast.makeText(context, "Bạn hãy nhập tên công việc", Toast.LENGTH_SHORT).show();
        } else {
            if (workDateEnd.isEmpty()) {
                Toast.makeText(context, "Bạn hãy chọn ngày kết thúc công việc", Toast.LENGTH_SHORT).show();
            } else {
                Date endDate = simpleDateFormat.parse(workDateEnd);
                Date startDate = simpleDateFormat.parse(workDateStart);
                if (endDate.compareTo(startDate) <= 0 && endDate.compareTo(currentTime) <= 0) {
                    Toast.makeText(context, "Vui lòng nhập ngày kết thúc công việc sau ngày bắt đầu và ngày hiện tại", Toast.LENGTH_SHORT).show();
                } else {
                    handleSaveWorkWithDue(workName, workType, workDateStart, workDateEnd);
                }
            }
        }
    }

    //lưu công việc có thời hạn
    private void handleSaveWorkWithDue(final String workName, final String workType, String workDateStart, String workDateEnd) {
        String workID = mData.push().getKey();
        eisenhowerWork = new EisenhowerWork(workID, workName, workType, workDateStart, workDateEnd);
        mData.child("Eisenhower").child(userID).child(workType).child(workID).setValue(eisenhowerWork, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Toast.makeText(context, "Đã lưu công việc " + workName + " vào khu vực " + workType, Toast.LENGTH_LONG).show();
                    dialogAddEisenhower.cancel();

                } else {
                    Toast.makeText(context, "Xảy ra lỗi. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //mở giao diện lịch
    private void openDialogDatePicker(final EditText editTextDate) {
        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        // time picker dialog
        datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yearNow, int monthNow, int dayOfMonthNow) {
                calendar.set(yearNow, monthNow, dayOfMonthNow, 0, 0, 0);
                editTextDate.setText(dayOfMonthNow + "/" + (monthNow + 1) + "/" + yearNow);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    // khởi tạo dialog tùy chỉnh công việc
    private void openDialogEditWork(final String workID, final String workName, final String workType, final String workDateStart, final String workDateEnd, final ArrayList<EisenhowerWork> arrayListEdit, final EIsenhowerAdaper arrayAdapterEdit) {
        dialogEditEisenhower = new Dialog(context);
        dialogEditEisenhower.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogEditEisenhower.setContentView(R.layout.dialog_edit_eisenhower_work);

        edtEditWorkContent = (EditText) dialogEditEisenhower.findViewById(R.id.editTextEisenhowerEditWorkContentDialog);
        edtEditWorkDateStart = (EditText) dialogEditEisenhower.findViewById(R.id.editTextEisenhowerEditDateStart);
        edtEditWorkDateEnd = (EditText) dialogEditEisenhower.findViewById(R.id.editTextEisenhowerEditDateEnd);
        spinnerEditWorkType = (Spinner) dialogEditEisenhower.findViewById(R.id.spinnerEditEisenhowerTypeWork);
        btnCompleteWorkList = (Button) dialogEditEisenhower.findViewById(R.id.buttonCompleteEisenhowerWorkDialog);
        btnEditWorkList = (Button) dialogEditEisenhower.findViewById(R.id.buttonEditEisenhowerWorkDialog);
        btnDeletWorkList = (Button) dialogEditEisenhower.findViewById(R.id.buttonDeleteEisenhowerWorkDialog);

        edtEditWorkContent.setText(workName);
        edtEditWorkDateStart.setInputType(InputType.TYPE_NULL);
        edtEditWorkDateEnd.setInputType(InputType.TYPE_NULL);
        edtEditWorkDateStart.setText(workDateStart);
        edtEditWorkDateEnd.setText(workDateEnd);
        edtEditWorkDateStart.setOnClickListener(this);
        edtEditWorkDateEnd.setOnClickListener(this);
        spinnerEditWorkType.setAdapter(spinnerAdapter);
        spinnerEditWorkType.setOnItemSelectedListener(this);
        spinnerEditWorkType.setEnabled(false);

        final String workContentEdit=edtEditWorkContent.getText().toString().trim();
        final String workDateStartEdit=edtEditWorkDateStart.getText().toString().trim();
        final String workDateEndEdit=edtEditWorkDateEnd.getText().toString().trim();


        btnCompleteWorkList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCompleteWork(workID, workName, workType, workDateStart, workDateEnd, arrayListEdit, arrayAdapterEdit);
            }
        });
        btnDeletWorkList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDeleteWork(workID, workName, workType, workDateStart, workDateEnd, arrayListEdit, arrayAdapterEdit);
            }
        });
        btnEditWorkList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (workContentEdit.isEmpty() ||workDateStartEdit.isEmpty() || workDateEndEdit.isEmpty()){
                        Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin chỉnh sửa", Toast.LENGTH_SHORT).show();
                    }else {
                        handleEditWork(workID, workContentEdit, workType, workDateStartEdit, workDateEndEdit, arrayListEdit, arrayAdapterEdit);
                    }

            }
        });
        dialogEditEisenhower.show();
    }

    //xử lý cập nhật lại công việc
    private void handleEditWork(String workID, String workName, final String workType, String workDateStart, String workDateEnd, final ArrayList<EisenhowerWork> arrayListEdit, final EIsenhowerAdaper arrayAdapterEdit) {
        EisenhowerWork eisenhowerWork = new EisenhowerWork(workID, workName, workType, workDateStart, workDateEnd);
        mData.child("Eisenhower").child(userID).child(workType).child(workID).setValue(eisenhowerWork, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError==null){
                    Toast.makeText(context, "Cập nhật công việc thành công", Toast.LENGTH_SHORT).show();
                    dialogEditEisenhower.cancel();
                    arrayListEdit.clear();
                    arrayAdapterEdit.notifyDataSetChanged();
                    initEisenhowerListView(arrayListEdit, arrayAdapterEdit, workType);
                }else {
                    Toast.makeText(context, "Có lỗi xảy ra vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //xử lý xóa công việc
    private void handleDeleteWork(final String workID, final String workName, final String workType, String workDateStart, String workDateEnd, final ArrayList<EisenhowerWork> arrayListEdit, final EIsenhowerAdaper arrayAdapterEdit) {
        final AlertDialog.Builder alertDelete = new AlertDialog.Builder(context);
        alertDelete.setTitle("Xóa công việc");
        alertDelete.setMessage("Bạn có chắc chắn xóa công việc này không?");
        alertDelete.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mData.child("Eisenhower").child(userID).child(workType).child(workID).removeValue();
                Toast.makeText(context, "Đã xóa công việc " + workName, Toast.LENGTH_SHORT).show();
                dialogEditEisenhower.dismiss();
                arrayListEdit.clear();
                arrayAdapterEdit.notifyDataSetChanged();
                initEisenhowerListView(arrayListEdit, arrayAdapterEdit, workType);

            }
        });
        alertDelete.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDelete.show();
    }

    //xử lý hoàn thành công việc
    private void handleCompleteWork(String workID, final String workNameEdit, final String workTypeEdit, String workDateStart, String workDateEnd, final ArrayList<EisenhowerWork> arrayListEdit, final EIsenhowerAdaper arrayAdapterEdit) {
        if (workNameEdit.isEmpty()) {
            Toast.makeText(context, "Bạn không được để trống tên công việc", Toast.LENGTH_SHORT).show();
        } else {
            mData.child("Eisenhower").child(userID).child(workTypeEdit).child(workID).child("workName").setValue(workNameEdit + "(hoàn thành)", new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        Toast.makeText(context, "Bạn đã hoàn thành công việc " + workNameEdit, Toast.LENGTH_SHORT).show();
                        dialogEditEisenhower.dismiss();
                        arrayListEdit.clear();
                        arrayAdapterEdit.notifyDataSetChanged();
                        initEisenhowerListView(arrayListEdit, arrayAdapterEdit, workType);
                    } else {
                        Toast.makeText(context, "Xảy ra lỗi, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinnerEisenhowerTypeWork:
                workType = parent.getItemAtPosition(position).toString();

                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.listViewEisenhowerDO:
                String workIdDO = arrayListEisenhowerDO.get(position).workID;
                String workNameDO = arrayListEisenhowerDO.get(position).workName;
                String workTypeDO = arrayListEisenhowerDO.get(position).workType;
                String workDateStartDO = arrayListEisenhowerDO.get(position).workDateStart;
                String workDateEndDO = arrayListEisenhowerDO.get(position).workDateEnd;
                openDialogEditWork(workIdDO, workNameDO, workTypeDO, workDateStartDO, workDateEndDO, arrayListEisenhowerDO, arrayAdapterDO);
                break;
            case R.id.listViewEisenhowerDECIDE:
                String workIdDECIDE = arrayListEisenhowerDECIDE.get(position).workID;
                String workNameDECIDE = arrayListEisenhowerDECIDE.get(position).workName;
                String workTypeDECIDE = arrayListEisenhowerDECIDE.get(position).workType;
                String workDateStartDECIDE = arrayListEisenhowerDECIDE.get(position).workDateStart;
                String workDateEndDECIDE = arrayListEisenhowerDECIDE.get(position).workDateEnd;
                openDialogEditWork(workIdDECIDE, workNameDECIDE, workTypeDECIDE, workDateStartDECIDE, workDateEndDECIDE, arrayListEisenhowerDECIDE, arrayAdapterDECIDE);
                break;
            case R.id.listViewEisenhowerDELEGATE:
                String workIdDELEGATE = arrayListEisenhowerDELEGATE.get(position).workID;
                String workNameDELEGATE = arrayListEisenhowerDELEGATE.get(position).workName;
                String workTypeDELEGATE = arrayListEisenhowerDELEGATE.get(position).workType;
                String workDateStartDELEGATE = arrayListEisenhowerDELEGATE.get(position).workDateStart;
                String workDateEndDELEGATE = arrayListEisenhowerDELEGATE.get(position).workDateEnd;
                openDialogEditWork(workIdDELEGATE, workNameDELEGATE, workTypeDELEGATE, workDateStartDELEGATE, workDateEndDELEGATE, arrayListEisenhowerDELEGATE, arrayAdapterDELEGATE);
                break;
            case R.id.listViewEisenhowerDELETE:
                String workIdDELETE = arrayListEisenhowerDELETE.get(position).workID;
                String workNameDELETE = arrayListEisenhowerDELETE.get(position).workName;
                String workTypeDELETE = arrayListEisenhowerDELETE.get(position).workType;
                String workDateStartDELETE = arrayListEisenhowerDELETE.get(position).workDateStart;
                String workDateEndDELETE = arrayListEisenhowerDELETE.get(position).workDateEnd;
                openDialogEditWork(workIdDELETE, workNameDELETE, workTypeDELETE, workDateStartDELETE, workDateEndDELETE, arrayListEisenhowerDELETE, arrayAdapterDELETE);
                break;
        }
    }

}
