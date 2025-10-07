package com.example.todolist;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ToDoListItemsAdapter extends BaseAdapter {
    AppCompatActivity context;
    ArrayList<ToDoListItems> toDoListItems;

    public ToDoListItemsAdapter(AppCompatActivity context, ArrayList<ToDoListItems> toDoListItems) {
        this.context = context;
        this.toDoListItems = toDoListItems;
    }

    @Override
    public int getCount() {
        return toDoListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return toDoListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return toDoListItems.get(position).getToDoItemID();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_to_do, parent, false);
        }

        ToDoListItems item = toDoListItems.get(position);

        LinearLayout foreground = view.findViewById(R.id.foreground_layout);
        LinearLayout background = view.findViewById(R.id.background_layout);
        CheckBox cbCompleted = view.findViewById(R.id.cbCompleted);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvDescription = view.findViewById(R.id.tvDescription);
        Button btnEdit = view.findViewById(R.id.btnEdit);
        Button btnDelete = view.findViewById(R.id.btnDelete);


        // Set data
        tvTitle.setText(item.getTitle());
        tvDescription.setText(item.getDescription());
        cbCompleted.setChecked(item.isCompleted());

        // Xử lý checkbox
        cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setCompleted(isChecked);
        });

        // Xử lý nút Edit/Delete
        btnEdit.setOnClickListener(v -> {
            // Tạo view cho dialog
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_item, null);
            EditText edtTitle = dialogView.findViewById(R.id.edtTitle);
            EditText edtDescription = dialogView.findViewById(R.id.edtDescription);

            // Gán dữ liệu hiện tại
            edtTitle.setText(item.getTitle());
            edtDescription.setText(item.getDescription());

            // Tạo dialog
            new AlertDialog.Builder(context)
                    .setTitle("Chỉnh sửa công việc")
                    .setView(dialogView)
                    .setPositiveButton("Lưu", (dialog, which) -> {
                        // Cập nhật dữ liệu
                        item.setTitle(edtTitle.getText().toString().trim());
                        item.setDescription(edtDescription.getText().toString().trim());

                        // Làm mới ListView
                        notifyDataSetChanged();
                        foreground.animate()
                                .translationX(0)
                                .setDuration(550)
                                .start();

                        Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        btnDelete.setOnClickListener(v -> {
            // TODO: Viết logic xóa
            new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa công việc này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    toDoListItems.remove(position);

                    notifyDataSetChanged();

                    Toast.makeText(context, "Đã xóa thành công!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
        });

        final int[] buttonWidth = {0};
        background.post(() -> {
            int editWidth = btnEdit.getWidth();
            int deleteWidth = btnDelete.getWidth();
            buttonWidth[0] = editWidth + deleteWidth;
        });

        // Xử lý vuốt sang phải để mở button
        foreground.setOnTouchListener(new View.OnTouchListener() {
            float downX = 0;
            boolean isOpen = false;
            final float SWIPE_THRESHOLD = 120; // khoảng cách cần vuốt

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float moveX = event.getX();
                        float deltaX = moveX - downX;

                        // Vuốt sang trái
                        if (deltaX < -SWIPE_THRESHOLD && !isOpen) {
                            foreground.animate()
                                    .translationX(-buttonWidth[0]) // dịch sang trái
                                    .setDuration(550)
                                    .start();
                            isOpen = true;
                        }
                        // Vuốt sang phải để đóng lại
                        else if (deltaX > SWIPE_THRESHOLD / 2 && isOpen) {
                            foreground.animate()
                                    .translationX(0)
                                    .setDuration(550)
                                    .start();
                            isOpen = false;
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        return true;
                }
                return false;
            }
        });

        return view;
    }
}
