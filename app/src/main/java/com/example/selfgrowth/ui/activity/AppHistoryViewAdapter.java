package com.example.selfgrowth.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.selfgrowth.R;
import com.example.selfgrowth.model.AppHistory;
import com.example.selfgrowth.model.AppInfo;
import com.example.selfgrowth.utils.DateUtils;

import org.angmarch.views.NiceSpinner;

import java.util.List;

public class AppHistoryViewAdapter extends BaseAdapter {

    private final Context context;
    private final List<AppHistory> dataList;

    public AppHistoryViewAdapter(Context context, List<AppHistory> dataList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.app_history_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            //得到缓存的布局
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.appName.setText(dataList.get(position).getAppName());
        viewHolder.packageName.setText(dataList.get(position).getPackageName());
        viewHolder.appLabel.setText(dataList.get(position).getLabel());
        viewHolder.createTime.setText(DateUtils.dateString(dataList.get(position).getCreateTime()));
        viewHolder.labelSpinner.setOnSpinnerItemSelectedListener((parent1, view, position1, id) -> {
            String label =parent1.getItemAtPosition(position1).toString();
            viewHolder.appLabel.setText("标签： " + label);
            final SharedPreferences preferences = context.getSharedPreferences(AppInfo.APP_INFO, Context.MODE_PRIVATE);
            dataList.get(position).updateLabel(preferences, label);
        });
        return convertView;
    }

    /**
     * ViewHolder类
     */
    private static final class ViewHolder {
        private final TextView appName;
        private final TextView packageName;
        private final TextView appLabel;
        private final NiceSpinner labelSpinner;
        private final TextView createTime;

        /**
         * 构造器
         * @param view 视图组件（ListView的子项视图）
         */
        ViewHolder(View view) {
            appName = view.findViewById(R.id.app_info_name);
            packageName = view.findViewById(R.id.app_info_package);
            appLabel = view.findViewById(R.id.label_text);
            labelSpinner = view.findViewById(R.id.label_spinner);
            createTime = view.findViewById(R.id.time);
        }
    }
}
