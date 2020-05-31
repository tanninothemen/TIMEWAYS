package vn.com.newc.timeways.Pomodoro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import vn.com.newc.timeways.R;

public class PomodoroListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<PomodoroWork> pomodoroWorkList;

    public PomodoroListAdapter(Context context, int layout, List<PomodoroWork> pomodoroWorkList) {
        this.context = context;
        this.layout = layout;
        this.pomodoroWorkList = pomodoroWorkList;
    }

    @Override
    public int getCount() {
        return pomodoroWorkList.size();
    }

    @Override
    public Object getItem(int position) {
        return pomodoroWorkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView txtWorkNameCustom, txtWorkDateCustom, txtWorkSessionCustom;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder=new ViewHolder();
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(layout, null);
            viewHolder.txtWorkNameCustom = convertView.findViewById(R.id.textViewPomodoroWorkNameCustom);
            viewHolder.txtWorkDateCustom = convertView.findViewById(R.id.textViewPomodoroWorkDateCustom);
            viewHolder.txtWorkSessionCustom = convertView.findViewById(R.id.textViewPomodoroWorkSessionCustom);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

        PomodoroWork pomodoroWork= (PomodoroWork) getItem(position);

        viewHolder.txtWorkNameCustom.setText(pomodoroWork.workName);
        viewHolder.txtWorkDateCustom.setText(pomodoroWork.workDate);
        viewHolder.txtWorkSessionCustom.setText("Số phiên: "+pomodoroWork.workSession);

        return convertView;
    }
}
