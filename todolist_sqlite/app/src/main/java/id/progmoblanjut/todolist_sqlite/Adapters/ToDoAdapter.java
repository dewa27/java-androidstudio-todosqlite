package id.progmoblanjut.todolist_sqlite.Adapters;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import java.util.Collections;
import java.util.List;

import id.progmoblanjut.todolist_sqlite.DbHelper;
import id.progmoblanjut.todolist_sqlite.MainActivity;
import id.progmoblanjut.todolist_sqlite.Model.ToDoModel;
import id.progmoblanjut.todolist_sqlite.R;
public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
    private DbHelper db;
    private MainActivity activity;
    private Context context;
    public ToDoAdapter(DbHelper db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        db.openDatabase();
        final ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    db.updateStatus(item.getId(), 1);
                } else {
                    db.updateStatus(item.getId(), 0);
                }
            }
        });
        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                EditText input = new EditText(getContext());
                input.setText(item.getTask());
                alertDialog.setTitle("Ubah to-do:");
                alertDialog.setView(input);
                // Set it.
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.updateTask(item.getId(),input.getText().toString());
                        retrieveTasks();
                    }
                });
                alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteTask(item.getId());
                retrieveTasks();
            }
        });
    }
    private boolean toBoolean(int n) {
        return n != 0;
    }
    @Override
    public int getItemCount() {
        return todoList.size();
    }
    public Context getContext() {
        return activity;
    }
    public void retrieveTasks(){
        this.todoList= db.getAllTasks();
        Collections.reverse(this.todoList);
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;
        Button editBtn,deleteBtn;
        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            editBtn=view.findViewById(R.id.button_edit);
            deleteBtn=view.findViewById(R.id.button_delete);
        }
    }
}
