package com.example.selfgrowth.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.codingending.popuplayout.PopupLayout;
import com.example.selfgrowth.R;
import com.example.selfgrowth.config.AppConfig;
import com.example.selfgrowth.enums.LabelEnum;
import com.example.selfgrowth.enums.LianQiStateEnum;
import com.example.selfgrowth.enums.StatisticsTypeEnum;
import com.example.selfgrowth.enums.TaskCycleEnum;
import com.example.selfgrowth.enums.TaskLearnTypeEnum;
import com.example.selfgrowth.enums.TaskTypeEnum;
import com.example.selfgrowth.enums.TiXiuStateEnum;
import com.example.selfgrowth.model.DashboardResult;
import com.example.selfgrowth.model.TaskConfig;
import com.example.selfgrowth.model.XiuXianState;
import com.example.selfgrowth.service.backend.DashboardService;
import com.example.selfgrowth.service.backend.TaskLogService;
import com.example.selfgrowth.service.backend.xiuxian.XiuXianService;
import com.example.selfgrowth.ui.custum.TableView;
import com.example.selfgrowth.ui.dashboard.DailyDashboardFragment;
import com.example.selfgrowth.ui.dashboard.DashboardFragment;
import com.example.selfgrowth.ui.dashboard.DataShareFragment;
import com.example.selfgrowth.ui.dashboard.PeriodDashboardFragment;
import com.example.selfgrowth.utils.MyTimeUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.Locale;

public class XiuXianFragment extends Fragment {

    private final XiuXianService xiuXianService = XiuXianService.getInstance();
    private final DashboardService dashboardService = DashboardService.getInstance();
    private final TaskLogService taskLogService = TaskLogService.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (AppConfig.isShowTourist()) {
            View view = inflater.inflate(R.layout.tourist, container, false);
            ((ViewFlipper) view.findViewById(R.id.viewFlipper1)).startFlipping();
            view.findViewById(R.id.close).setOnClickListener(v -> {
                AppConfig.closeShowTourist();
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.tourist, new XiuXianFragment())
                        .addToBackStack(null)
                        .commit();
            });
            return view;
        }
        View view = inflater.inflate(R.layout.xiu_xian, container, false);
        refresh(view);
        Toast.makeText(getContext(),"?????????????????????????????????-->????????????????????????????????????????????????????????????", Toast.LENGTH_LONG).show();
        Toast.makeText(getContext(),"?????????????????????????????????APP???????????????????????????", Toast.LENGTH_LONG).show();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void refresh(View view) {
        final XiuXianState res = xiuXianService.yesterdaySettlement(view);

        XiuXianState.LianQiState qiXiuState = res.getQiXiuState();
        ((TextView) view.findViewById(R.id.qi_xiu_upgrade_need)).setText(String.format(Locale.CHINA,
                "%s%d???(??????%d)????????????%d??????+%d??????", qiXiuState.getState().getName(), qiXiuState.getLevel(),
                res.getReincarnationAmountOfQiXiu(), qiXiuState.getUpgradeNeedQiLi(), qiXiuState.getUpgradeNeedYuanLi()));

        int qiXiuUpgradeProcess = (int) (((double)res.getQiLi() / (double)qiXiuState.getUpgradeNeedQiLi()) * 100);
        ProgressBar qiXiuBar = view.findViewById(R.id.qi_xiu_upgrade_process);
        qiXiuBar.setProgress(qiXiuUpgradeProcess);
        qiXiuBar.setOnClickListener(bv -> {
            View settingView = View.inflate(view.getContext(), R.layout.state_instruction, null);
            TableView table = settingView.findViewById(R.id.state_detail);
            table.clearTableContents().setHeader("????????????", "????????????", "??????", "????????????????????????");
            for (LianQiStateEnum e: LianQiStateEnum.values()) {
                int need = e.getUpgradeNeed() * res.getReincarnationAmountOfQiXiu();
                table.addContent(String.valueOf(e.getIndex()), e.getName(),
                        String.valueOf(e.getMaxState() - e.getMinState()),
                        String.format(Locale.CHINA, "??????:%d,??????:%d", need, need / 2));
            }
            table.refreshTable();
            PopupLayout popupLayout= PopupLayout.init(view.getContext(), settingView);
            popupLayout.show(PopupLayout.POSITION_TOP);
        });

        XiuXianState.TiXiuState tiXiuState = res.getTiXiuState();
        ((TextView) view.findViewById(R.id.ti_xiu_upgrade_need)).setText(String.format(Locale.CHINA,
                "%s%d???(??????%d)????????????%d??????+%d??????", tiXiuState.getState().getName(), tiXiuState.getLevel(),
                res.getReincarnationAmountOfTiXiu(), tiXiuState.getUpgradeNeedTiLi(), tiXiuState.getUpgradeNeedYuanLi()));

        int tiXiuUpgradeProcess = (int) (((double)res.getTiLi() / (double)tiXiuState.getUpgradeNeedTiLi()) * 100);
        ProgressBar tiXiuBar =  view.findViewById(R.id.ti_xiu_upgrade_process);
        tiXiuBar.setProgress(tiXiuUpgradeProcess);
        tiXiuBar.setOnClickListener(bv -> {
            View settingView = View.inflate(view.getContext(), R.layout.state_instruction, null);
            TableView table = settingView.findViewById(R.id.state_detail);
            table.clearTableContents().setHeader("????????????", "????????????", "??????", "????????????????????????");
            for (TiXiuStateEnum e: TiXiuStateEnum.values()) {
                int need = e.getUpgradeNeed() * res.getReincarnationAmountOfTiXiu();
                table.addContent(String.valueOf(e.getIndex()), e.getName(),
                        String.valueOf(e.getMaxState() - e.getMinState()),
                        String.format(Locale.CHINA, "??????:%d,??????:%d", need, need / 2));
            }
            table.refreshTable();
            PopupLayout popupLayout= PopupLayout.init(view.getContext(), settingView);
            popupLayout.show(PopupLayout.POSITION_TOP);
        });

        ((TextView) view.findViewById(R.id.current_yuan_li)).setText(String.format(Locale.CHINA,
                "?????????%d", res.getYuanLi()));
        ((TextView) view.findViewById(R.id.current_qi_li)).setText(String.format(Locale.CHINA,
                "?????????%d", res.getQiLi()));
        ((TextView) view.findViewById(R.id.current_ti_li)).setText(String.format(Locale.CHINA,
                "?????????%d", res.getTiLi()));

        DashboardResult stat = dashboardService.getPeriodData(new Date(), StatisticsTypeEnum.DAY, view, true);
        ((TextView) view.findViewById(R.id.today_yuan_li)).setText(String.format(Locale.CHINA,
                "?????????%d", stat.getSleepTime()));
        ((TextView) view.findViewById(R.id.today_qi_li)).setText(String.format(Locale.CHINA,
                "?????????%d", stat.getLearnTime()));
        ((TextView) view.findViewById(R.id.today_ti_li)).setText(String.format(Locale.CHINA,
                "?????????%d", stat.getRunningTime()));

        TableView todayData = view.findViewById(R.id.today_data);
        todayData.clearTableContents()
                .setHeader("????????????", "??????", "?????????")
                .addContent("??????????????????", MyTimeUtils.toString(stat.getSleepTime()), String.valueOf(stat.getSleepTime()))
                .addContent("??????????????????", MyTimeUtils.toString(stat.getLearnTime()), String.valueOf(stat.getLearnTime()))
                .addContent("??????????????????", MyTimeUtils.toString(stat.getRunningTime()), String.valueOf(stat.getRunningTime()))
                .refreshTable();

        ((TextView) view.findViewById(R.id.qi_xiu_log)).setText(res.getQiXiuUpgradeMsg());
        ((TextView) view.findViewById(R.id.ti_xiu_log)).setText(res.getTiXiuUpgradeMsg());
        ((TextView) view.findViewById(R.id.xiu_xian_log)).setText(res.getYesterdayLog());

        view.findViewById(R.id.reload_xiu_xian_data).setOnClickListener(v -> {
            xiuXianService.reloadStateFromOldDate(view);
            refresh(view);
        });

        setRoute(view, R.id.xiu_xian_today_data, new DashboardFragment());

        view.findViewById(R.id.xiu_xian_data_overview).setOnClickListener(v -> {
            View settingView = View.inflate(view.getContext(), R.layout.data_overview_menu, null);
            setRoute(settingView, R.id.xiu_xian_daily_dashboard, new DailyDashboardFragment());
            setRoute(settingView, R.id.xiu_xian_week_dashboard, new PeriodDashboardFragment(StatisticsTypeEnum.WEEK));
            setRoute(settingView, R.id.xiu_xian_month_dashboard, new PeriodDashboardFragment(StatisticsTypeEnum.MONTH));
            setRoute(settingView, R.id.xiu_xian_year_dashboard, new PeriodDashboardFragment(StatisticsTypeEnum.YEAR));
            PopupLayout popupLayout= PopupLayout.init(view.getContext(), settingView);
            popupLayout.show(PopupLayout.POSITION_TOP);
        });

        view.findViewById(R.id.share_xiu_xian_data).setOnClickListener(v -> {
            View settingView = View.inflate(view.getContext(), R.layout.data_share_menu, null);
            setRoute(settingView, R.id.xiu_xian_daily_dashboard, new DataShareFragment(StatisticsTypeEnum.DAY));
            setRoute(settingView, R.id.xiu_xian_week_dashboard, new DataShareFragment(StatisticsTypeEnum.WEEK));
            setRoute(settingView, R.id.xiu_xian_month_dashboard, new DataShareFragment(StatisticsTypeEnum.MONTH));
            setRoute(settingView, R.id.xiu_xian_year_dashboard, new DataShareFragment(StatisticsTypeEnum.YEAR));
            PopupLayout popupLayout= PopupLayout.init(view.getContext(), settingView);
            popupLayout.show(PopupLayout.POSITION_TOP);
        });

        view.findViewById(R.id.xiu_xian_blog).setOnClickListener(v -> {
            View settingView = View.inflate(view.getContext(), R.layout.add_blog, null);
            PopupLayout popupLayout= PopupLayout.init(view.getContext(), settingView);
            settingView.findViewById(R.id.confirm).setOnClickListener(bv -> {
                String title = ((EditText) settingView.findViewById(R.id.title)).getText().toString();
                String url = ((EditText) settingView.findViewById(R.id.url)).getText().toString();
                if (title.isEmpty() || url.isEmpty()) {
                    Snackbar.make(view, "??????????????????url", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }
                taskLogService.add(TaskConfig.builder()
                        .name(title)
                        .description(url)
                        .label(LabelEnum.LEARN)
                        .cycleType(TaskCycleEnum.DEFAULT)
                        .learnType(TaskLearnTypeEnum.OUTPUT)
                        .group("????????????")
                        .taskTypeEnum(TaskTypeEnum.NOTE)
                        .isComplete(true)
                        .completeDate(new Date())
                        .isDelete(false)
                        .build());
                Snackbar.make(view, "????????????", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                popupLayout.dismiss();
            });

            settingView.findViewById(R.id.cancel).setOnClickListener(bv -> {
                popupLayout.dismiss();
            });
            popupLayout.show(PopupLayout.POSITION_TOP);
        });

        view.findViewById(R.id.xiu_xian_book).setOnClickListener(v -> {
            View settingView = View.inflate(view.getContext(), R.layout.add_book, null);
            PopupLayout popupLayout= PopupLayout.init(view.getContext(), settingView);
            settingView.findViewById(R.id.confirm).setOnClickListener(bv -> {
                String title = ((EditText) settingView.findViewById(R.id.title)).getText().toString();
                if (title.isEmpty()) {
                    Snackbar.make(view, "???????????????", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }
                taskLogService.add(TaskConfig.builder()
                        .name(title)
                        .description("??????")
                        .label(LabelEnum.LEARN)
                        .cycleType(TaskCycleEnum.DEFAULT)
                        .learnType(TaskLearnTypeEnum.INPUT)
                        .group("????????????")
                        .taskTypeEnum(TaskTypeEnum.BOOK)
                        .isComplete(true)
                        .completeDate(new Date())
                        .isDelete(false)
                        .build());
                Snackbar.make(view, "????????????", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                popupLayout.dismiss();
            });

            settingView.findViewById(R.id.cancel).setOnClickListener(bv -> {
                popupLayout.dismiss();
            });
            popupLayout.show(PopupLayout.POSITION_TOP);
        });
    }

    private void setRoute(View view, int buttonId, Fragment fragment) {
        view.findViewById(buttonId).setOnClickListener(v -> requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.xiu_xian, fragment)
                .addToBackStack(null)
                .commit());
    }
}
