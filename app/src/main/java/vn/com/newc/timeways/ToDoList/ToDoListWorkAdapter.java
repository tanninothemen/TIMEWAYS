package vn.com.newc.timeways.ToDoList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import vn.com.newc.timeways.R;

public class ToDoListWorkAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<ToDoListWork> toDoListWorkList;

    public ToDoListWorkAdapter(Context context, int layout, List<ToDoListWork> toDoListWorkList) {
        this.context = context;
        this.layout = layout;
        this.toDoListWorkList = toDoListWorkList;
    }

    @Override
    public int getCount() {
        return toDoListWorkList.size();
    }

    @Override
    public Object getItem(int position) {
        return toDoListWorkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        CheckBox ckbWorkStatus;
        TextView tvWorkContent, tvWorkDue;
        ImageButton imgbtDeleteWork;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            viewHolder.tvWorkContent = (TextView) convertView.findViewById(R.id.textViewToDoListWorkContent);
            viewHolder.tvWorkDue = (TextView) convertView.findViewById(R.id.textViewToDoListWorkDue);
            viewHolder.imgbtDeleteWork = (ImageButton) convertView.findViewById(R.id.imageButtonDeleteToDoList);
            viewHolder.ckbWorkStatus = (CheckBox) convertView.findViewById(R.id.checkboxToDoListWorkStatus);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ToDoListWork toDoListWork = (ToDoListWork) getItem(position);

        viewHolder.tvWorkContent.setText(toDoListWork.WorkContent);
        if (toDoListWork.workDateDue == null) {
            viewHolder.tvWorkDue.setText("");
        } else if (toDoListWork.WorkTimeDue == null) {
            viewHolder.tvWorkDue.setText("Ngày đáo hạn: " + toDoListWork.workDateDue);
        } else {
            viewHolder.tvWorkDue.setText("Ngày đáo hạn: " + toDoListWork.workDateDue + " - " + toDoListWork.WorkTimeDue);
        }

        viewHolder.tvWorkContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentToDoList fragmentToDoList=new FragmentToDoList();
                fragmentToDoList.initDialogUpdateWork(toDoListWork);
            }
        });
        viewHolder.ckbWorkStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (viewHolder.ckbWorkStatus.isChecked()) {
                    final AlertDialog.Builder alertCompleteWork = new AlertDialog.Builder(context);
                    alertCompleteWork.setTitle("Hoàn thành công việc");
                    alertCompleteWork.setMessage("Bạn xác nhận đã hoàn thành công việc?");
                    alertCompleteWork.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentToDoList.changeStatusToDoList(toDoListWork, toDoListWork.workID);
                        }
                    });
                    alertCompleteWork.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            viewHolder.ckbWorkStatus.setChecked(false);
                            dialog.cancel();
                        }
                    });
                    alertCompleteWork.show();
                }
            }
        });
        viewHolder.imgbtDeleteWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDeleteWork = new AlertDialog.Builder(context);
                alertDeleteWork.setTitle("Xóa công việc");
                alertDeleteWork.setMessage("Bạn có chắc muốn xóa công việc "+toDoListWork.WorkContent+" không?");
                alertDeleteWork.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentToDoList.deleteToDoListWork(toDoListWork.workID, toDoListWork,"Working");
                    }
                });
                alertDeleteWork.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDeleteWork.show();
            }
        });

        return convertView;
    }
}
