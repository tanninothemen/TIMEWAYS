package vn.com.newc.timeways.Eisenhower;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import vn.com.newc.timeways.R;

public class EIsenhowerAdaper extends BaseAdapter {

    private Context context;
    private int layout;
    private List<EisenhowerWork> eisenhowerWorks;

    public EIsenhowerAdaper(Context context, int layout, List<EisenhowerWork> eisenhowerWorks) {
        this.context = context;
        this.layout = layout;
        this.eisenhowerWorks = eisenhowerWorks;
    }

    @Override
    public int getCount() {
        return eisenhowerWorks.size();
    }

    @Override
    public Object getItem(int position) {
        return eisenhowerWorks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView txtEisenhowerWorkName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder=new ViewHolder();
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(layout, null);
            viewHolder.txtEisenhowerWorkName=(TextView) convertView.findViewById(R.id.textViewEisenhowerWorkNameCustom);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

        EisenhowerWork eisenhowerWork= (EisenhowerWork) getItem(position);
        viewHolder.txtEisenhowerWorkName.setText(eisenhowerWork.workName);
        return convertView;
    }
}
