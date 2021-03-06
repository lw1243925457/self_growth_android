package com.example.selfgrowth.ui.task;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.selfgrowth.R;
import com.example.selfgrowth.enums.LabelEnum;
import com.example.selfgrowth.enums.TaskCycleEnum;
import com.example.selfgrowth.enums.TaskLearnTypeEnum;
import com.example.selfgrowth.enums.TaskTypeEnum;
import com.example.selfgrowth.model.TaskConfig;
import com.example.selfgrowth.service.backend.TaskService;

import org.angmarch.views.NiceSpinner;

public class AddTaskFragment extends Fragment {

    private final TaskService taskService = TaskService.getInstance();
    private final String currentGroup;
    private LabelEnum label = LabelEnum.DEFAULT;
    private TaskCycleEnum cycle = TaskCycleEnum.DEFAULT;
    private TaskLearnTypeEnum learnType = TaskLearnTypeEnum.DEFAULT;
    private TaskTypeEnum taskType = TaskTypeEnum.DEFAULT;

    public AddTaskFragment(String currentGroup) {
        this.currentGroup = currentGroup;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_add, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        NiceSpinner labels = view.findViewById(R.id.add_task_label);
        labels.attachDataSource(LabelEnum.names());
        labels.setOnSpinnerItemSelectedListener((parent, view1, position, id) -> {
            label = LabelEnum.fromString((String) parent.getItemAtPosition(position));
            ((EditText)view.findViewById(R.id.add_task_label_show)).setText("任务标签    " + label.getName());
        });

        NiceSpinner cycleTypes = view.findViewById(R.id.add_task_cycleType);
        cycleTypes.attachDataSource(TaskCycleEnum.names());
        cycleTypes.setOnSpinnerItemSelectedListener(((parent, view1, position, id) -> {
            String item = (String) parent.getItemAtPosition(position);
            cycle = TaskCycleEnum.fromString(item);
            ((EditText)view.findViewById(R.id.add_task_cycleType_show)).setText("任务周期    " + item);
        }));

        NiceSpinner learnTypes = view.findViewById(R.id.add_task_learnType);
        learnTypes.attachDataSource(TaskLearnTypeEnum.names());
        learnTypes.setOnSpinnerItemSelectedListener(((parent, view1, position, id) -> {
            String item = (String) parent.getItemAtPosition(position);
            learnType = TaskLearnTypeEnum.fromString(item);
            ((EditText)view.findViewById(R.id.add_task_learnType_show)).setText("任务类型    " + item);
        }));

        NiceSpinner taskTypes = view.findViewById(R.id.add_task_outputType);
        taskTypes.attachDataSource(TaskTypeEnum.names());
        taskTypes.setOnSpinnerItemSelectedListener(((parent, view1, position, id) -> {
            String item = (String) parent.getItemAtPosition(position);
            taskType = TaskTypeEnum.fromString(item);
            ((EditText)view.findViewById(R.id.add_task_outputType_show)).setText("输出类型    " + item);
        }));

        view.findViewById(R.id.add_task_button).setOnClickListener(v -> {
            final String taskGroup = ((EditText)view.findViewById(R.id.add_task_group)).getText().toString();
            final String taskName = ((EditText)view.findViewById(R.id.add_task_name)).getText().toString();
            final String desc = ((EditText)view.findViewById(R.id.add_task_dsc)).getText().toString();
            final TaskConfig taskConfig = TaskConfig.builder()
                    .name(taskName)
                    .description(desc)
                    .label(label)
                    .cycleType(cycle)
                    .learnType(learnType)
                    .group(taskGroup.isEmpty() ? currentGroup : taskGroup)
                    .taskTypeEnum(taskType)
                    .isComplete(false)
                    .build();
            taskService.add(taskConfig, view);

            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setPositiveButton(R.string.yes, (dialog, which) -> Log.d("add task", "continue"));
            builder.setNegativeButton(R.string.no, (dialog, which) -> requireActivity().getSupportFragmentManager().popBackStack());
            AlertDialog dialog = builder.create();
            dialog.setTitle("是否继续添加任务");
            dialog.setCancelable(false);
            dialog.show();
        });
    }
}
