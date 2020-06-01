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

public class ToDoListWorkCompleteAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<ToDoListWork> toDoListWorkList;

    public ToDoListWorkCompleteAdapter(Context context, int layout, List<ToDoListWork> toDoListWorkList) {
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

    public class ViewHolder {
        CheckBox ckbWorkStatus;
        TextView tvWorkContent, tvWorkDue;
        ImageButton imgbtDeleteWork;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder
                viewHolder;
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
        viewHolder.ckbWorkStatus.setChecked(true);
        viewHolder.ckbWorkStatus.setEnabled(false);
        viewHolder.imgbtDeleteWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDeleteWork = new AlertDialog.Builder(context);
                alertDeleteWork.setTitle("Xóa công việc");
                alertDeleteWork.setMessage("Bạn có chắc chắn muốn xóa công việc "+toDoListWork.WorkContent+" không?");
                alertDeleteWork.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentToDoList.deleteToDoListWork(toDoListWork.workID, toDoListWork,"Complete");
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
