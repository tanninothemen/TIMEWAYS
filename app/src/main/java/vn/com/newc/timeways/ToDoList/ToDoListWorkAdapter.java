package vn.com.newc.timeways.ToDoList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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
        ViewHolder viewHolder;
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

        ToDoListWork toDoListWork = (ToDoListWork) getItem(position);

        viewHolder.tvWorkContent.setText(toDoListWork.WorkContent);
        if (toDoListWork.workDateDue == null) {
            viewHolder.tvWorkDue.setText("");
        } else if (toDoListWork.WorkTimeDue == null) {
            viewHolder.tvWorkDue.setText("Ngày đáo hạn: " + toDoListWork.workDateDue);
        } else {
            viewHolder.tvWorkDue.setText("Ngày đáo hạn: " + toDoListWork.workDateDue + " - " + toDoListWork.WorkTimeDue);
        }

        return convertView;
    }
}
