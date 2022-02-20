package com.example.selfgrowth.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.selfgrowth.R;
import com.example.selfgrowth.http.model.DashboardStatistics;
import com.example.selfgrowth.http.model.TaskConfig;
import com.example.selfgrowth.http.request.TaskRequest;
import com.example.selfgrowth.ui.task.TaskFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Strings;

import java.util.List;

public class DashboardItemListViewAdapter extends BaseAdapter {

    private final TaskRequest taskRequest = new TaskRequest();
    private final Context context;
    private final List<DashboardStatistics.DashboardApp> dataList;

    public DashboardItemListViewAdapter(Context context, List<DashboardStatistics.DashboardApp> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //判断是否有缓存
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.dashboard_item_list_view, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            //得到缓存的布局
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(dataList.get(position).getName());
        viewHolder.time.setText(showTime(dataList.get(position).getMinutes()));
        return convertView;
    }

    private String showTime(int minutes) {
        int hours = minutes / 60;
        if (hours > 0) {
            return String.format("%d 小时 %d 分钟", hours, minutes % 60);
        }
        return String.format("%d 分钟", minutes);
    }

    /**
     * ViewHolder类
     */
    private static final class ViewHolder {
        private final TextView name;
        private final TextView time;

        /**
         * 构造器
         * @param view 视图组件（ListView的子项视图）
         */
        ViewHolder(View view) {
            name = view.findViewById(R.id.application_name);
            time = view.findViewById(R.id.application_time);
        }
    }
}