package vn.com.newc.timeways.ToDoList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.List;

import vn.com.newc.timeways.Main.MainActivity;
import vn.com.newc.timeways.R;
import vn.com.newc.timeways.Work;

public class ToDoListWorkAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<Work> workList;
    private DatabaseReference mData;

    public ToDoListWorkAdapter(Context context, int layout, List<Work> workList) {
        this.context = context;
        this.layout = layout;
        this.workList = workList;
    }

    @Override
    public int getCount() {
        return workList.size();
    }

    @Override
    public Object getItem(int position) {
        return workList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView tvWorkContent, tvWorkDue;
        ImageButton imgbtEditWork, imgbtDeleteWork;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mData= FirebaseDatabase.getInstance().getReference();
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            viewHolder.tvWorkContent = (TextView) convertView.findViewById(R.id.textViewToDoListWorkContent);
            viewHolder.tvWorkDue = (TextView) convertView.findViewById(R.id.textViewToDoListWorkDue);
            viewHolder.imgbtEditWork = (ImageButton) convertView.findViewById(R.id.imageButtonEditToDoList);
            viewHolder.imgbtDeleteWork = (ImageButton) convertView.findViewById(R.id.imageButtonDeleteToDoList);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Work work = (Work) getItem(position);

        viewHolder.tvWorkContent.setText(work.WorkContent);
        if (work.WorkDue == 0) {
            viewHolder.tvWorkDue.setText("");
        } else {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            String timeWorkDue = format.format(work.WorkDue);
            viewHolder.tvWorkDue.setText("Kết thúc công việc: " + timeWorkDue);
        }
        viewHolder.imgbtEditWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentToDoList mContext = new FragmentToDoList();
                mContext.initDialogUpdateWork();
            }
        });
        viewHolder.imgbtDeleteWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }
}
