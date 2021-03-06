package com.example.selfgrowth.ui.dashboard;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.selfgrowth.R;
import com.example.selfgrowth.enums.LabelEnum;
import com.example.selfgrowth.enums.StatisticsTypeEnum;
import com.example.selfgrowth.model.DailyLogModel;
import com.example.selfgrowth.model.DashboardResult;
import com.example.selfgrowth.model.DashboardStatistics;
import com.example.selfgrowth.service.backend.AppStatisticsService;
import com.example.selfgrowth.service.backend.DashboardService;
import com.example.selfgrowth.ui.task.TaskListFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardFragment extends Fragment {

    private final List<String> tabs = Arrays.asList("总览", "其他", "时间线");
    private final int activeColor = Color.parseColor("#FFFFFF");
    private final int normalColor = Color.parseColor("#666666");
    private final int normalSize = 20;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        CollectionAdapter collectionAdapter = new CollectionAdapter(this);
        ViewPager2 viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(collectionAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            //这里可以自定义TabView
            TextView tabView = new TextView(requireContext());

            int[][] states = new int[2][];
            states[0] = new int[]{android.R.attr.state_selected};
            states[1] = new int[]{};

            int[] colors = new int[]{activeColor, normalColor};
            ColorStateList colorStateList = new ColorStateList(states, colors);
            tabView.setText(tabs.get(position));
            tabView.setTextSize(normalSize);
            tabView.setTextColor(colorStateList);

            tab.setCustomView(tabView);
        });
        //要执行这一句才是真正将两者绑定起来
        mediator.attach();
    }


    class CollectionAdapter extends FragmentStateAdapter {

        public CollectionAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return a NEW fragment instance in createFragment(int)
            Fragment fragment = new SubTabFragment(tabs.get(position));
            Bundle args = new Bundle();
            // Our object is just an integer :-P
            args.putInt(TaskListFragment.ARG_OBJECT, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return tabs.size();
        }
    }

    public static class SubTabFragment extends Fragment {

        private final AppStatisticsService appStatisticsService = AppStatisticsService.getInstance();
        private final DashboardService dashboardService = DashboardService.getInstance();
        private final String type;
        private DashboardStatistics data;

        public SubTabFragment(final String type) {
            this.type = type;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            View view;
            if (type.equals("时间线")) {
                view = inflater.inflate(R.layout.recycler_view, container, false);
                initTimelineView(view);
            } else {
                view = inflater.inflate(R.layout.fragment_dashboard, container, false);
                initData(view);
            }
            return view;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void initTimelineView(View view) {
            final DashboardResult result = dashboardService.getPeriodData(new Date(), StatisticsTypeEnum.DAY, view, true);
            List<DailyLogModel> dailyLogs = result.getDailyLogs()
                    .stream()
                    .sorted(Comparator.comparing(DailyLogModel::getDate))
                    .collect(Collectors.toList());
            DailyLogAdapter adapter = new DailyLogAdapter(dailyLogs);
            RecyclerView timelineView = view.findViewById(R.id.recyclerView);
            LinearLayoutManager layoutManage = new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false);
            timelineView.setLayoutManager(layoutManage);
            timelineView.setAdapter(adapter);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void initData(View view) {
            this.data = appStatisticsService.statistics(new Date(), requireContext());
            if (data == null || data.getGroups() == null || data.getGroups().keySet().size() <= 0) {
                return;
            }
            if (type.equals("总览")) {
                todayOverview(view);
                return;
            }
            if (type.equals("其他")) {
                List<DashboardStatistics.DashboardApp> appData;
                DashboardStatistics.DashboardGroup groupData = data.getGroups().get(type);
                if (groupData == null) {
                    appData = new ArrayList<>(0);
                } else {
                    appData = groupData.getApps();
                }
                ListView listView = view.findViewById(R.id.dashboard_group_view_id);
                final DashboardItemListViewAdapter adapter = new DashboardItemListViewAdapter(requireContext(), appData);
                listView.setAdapter(adapter);
            }
        }

        private void todayOverview(View view) {
            if (data == null) {
                return;
            }

            DashboardStatistics.DashboardGroup learnData = data.getGroups().getOrDefault(LabelEnum.LEARN.getName(), null);
            DashboardStatistics.DashboardGroup runningData = data.getGroups().getOrDefault(LabelEnum.RUNNING.getName(), null);
            DashboardStatistics.DashboardGroup sleepData = data.getGroups().getOrDefault(LabelEnum.SLEEP.getName(), null);

            List<DashboardStatistics.DashboardApp> data = new ArrayList<>(20);
            if (learnData == null) {
                data.add(DashboardStatistics.DashboardApp.builder()
                        .name("学习总时长:")
                        .minutes(0)
                        .build());
            } else {
                data.add(DashboardStatistics.DashboardApp.builder()
                        .name("学习总时长:")
                        .minutes(learnData.getMinutes())
                        .build());
                data.addAll(learnData.getApps());
            }
            data.add(DashboardStatistics.DashboardApp.builder()
                    .name("运动总时长:")
                    .minutes(runningData == null ? 0 : runningData.getMinutes())
                    .build());
            data.add(DashboardStatistics.DashboardApp.builder()
                    .name("睡觉总时长:")
                    .minutes(sleepData == null ? 0 : sleepData.getMinutes())
                    .build());
            final DashboardItemListViewAdapter adapter = new DashboardItemListViewAdapter(requireContext(), data);
            final ListView listView = view.findViewById(R.id.dashboard_group_view_id);
            listView.setAdapter(adapter);
        }
    }
}