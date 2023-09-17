package com.example.introexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarRVAdapter extends RecyclerView.Adapter<CalendarRVAdapter.ViewHolder> {
    private List<Task> taskList;
    private presentDayTaskAdapter.onTaskItemClickListener onTaskItemClickListener;

    public interface onTaskItemClickListener {
        void onTaskItemClick(Task task);
    }

    public void setOnTaskItemClickListener(presentDayTaskAdapter.onTaskItemClickListener listener) {
        this.onTaskItemClickListener = listener;
    }
    public CalendarRVAdapter(List<Task> tasks){
        this.taskList = tasks;
    }

    public void setTaskListCalendar(List<Task> tasks){
        this.taskList = tasks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemview = layoutInflater.inflate(R.layout.calendarrvitem, parent, false);
        return new ViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textTitle, tasktime, taskdate;
        private Button priority;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.tasktitle);
            tasktime =itemView.findViewById(R.id.tasktime);
            taskdate = itemView.findViewById(R.id.taskdate);
            priority = itemView.findViewById(R.id.prioritydisplay1);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position!=RecyclerView.NO_POSITION && onTaskItemClickListener!=null){
                        Task task = taskList.get(position);
                        onTaskItemClickListener.onTaskItemClick(task);
                    }
                }
            });
        }

        public void bind(Task task) {
            textTitle.setText(task.getTitle());
            tasktime.setText(task.getDueTime());
            String d;
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                SimpleDateFormat outputFormat = new SimpleDateFormat("EEE dd-MMM", Locale.ENGLISH);
                Date date = inputFormat.parse(task.getDueDate().toString());
                d = outputFormat.format(date);
                taskdate.setText(d);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            String s = String.valueOf(task.getPriority());
            priority.setText(s);
        }
    }
}
