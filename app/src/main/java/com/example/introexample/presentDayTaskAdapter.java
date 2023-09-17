package com.example.introexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class presentDayTaskAdapter extends RecyclerView.Adapter<presentDayTaskAdapter.ViewHolder> {
    private List<Task> taskList;
    private onTaskItemClickListener onTaskItemClickListener;


    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }
    public presentDayTaskAdapter(List<Task> tasks){
        this.taskList = tasks;
    }
    @NonNull
    @Override
    public presentDayTaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.singletaskitemview, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull presentDayTaskAdapter.ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
    public interface onTaskItemClickListener {
        void onTaskItemClick(Task task);
    }
    public void setOnTaskItemClickListener(onTaskItemClickListener listener) {
        this.onTaskItemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textTitle, tasktime;
        private Button priorityflag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.tasktitle);
            tasktime =itemView.findViewById(R.id.tasktime);
            priorityflag = itemView.findViewById(R.id.prioritydisplay);
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
        void bind(Task task) {
            textTitle.setText(task.getTitle());
            String prio = String.valueOf(task.getPriority());
            priorityflag.setText(prio);
            tasktime.setText(task.getDueTime());
        }
    }
}
