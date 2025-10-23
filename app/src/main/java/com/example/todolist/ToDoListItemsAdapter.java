package com.example.todolist;

import android.annotation.SuppressLint;
import android.content.Intent;
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
        TextView tvDateRange = view.findViewById(R.id.tvDateRange);
        Button btnEdit = view.findViewById(R.id.btnEdit);
        Button btnDelete = view.findViewById(R.id.btnDelete);

        // Đảm bảo mỗi lần bind lại, item được đóng (không còn dịch sang trái)
        foreground.setTranslationX(0);


        // Set data
        tvTitle.setText(item.getTitle());
        tvDescription.setText(item.getDescription());
        cbCompleted.setChecked(item.isCompleted());

        // Hiển thị ngày giờ nếu có (dùng một mốc duy nhất)
        if (item.getDateTimeMillis() != null) {
            String text = formatDateTime(item.getDateTimeMillis());
            tvDateRange.setText(text);
            tvDateRange.setVisibility(View.VISIBLE);
        } else {
            tvDateRange.setText("");
            tvDateRange.setVisibility(View.GONE);
        }

        // Xử lý checkbox
        cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setCompleted(isChecked);
            ToDoDbHelper db = new ToDoDbHelper(context);
            db.setCompleted(item.getToDoItemID(), isChecked);
        });

        // Xử lý nút Edit/Delete
        btnEdit.setOnClickListener(v -> {
            // Đóng swipe trước khi chuyển màn hình để trạng thái luôn sạch sẽ khi quay lại
            foreground.animate().translationX(0).setDuration(150).start();
            Intent intent = new Intent(context, EditItemActivity.class);
            intent.putExtra("title", item.getTitle());
            intent.putExtra("description", item.getDescription());
            intent.putExtra("position", position);
            if (item.getDateTimeMillis() != null) intent.putExtra("startDate", item.getDateTimeMillis());
            context.startActivityForResult(intent, 100);
        });

        btnDelete.setOnClickListener(v -> {
            // TODO: Viết logic xóa
            new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa công việc này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    ToDoDbHelper db = new ToDoDbHelper(context);
                    db.deleteTodo(item.getToDoItemID());
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
            final float SWIPE_THRESHOLD = 100; // khoảng cách cần vuốt

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float moveX = event.getX();
                        float deltaX = moveX - downX;

                        // Đảm bảo đã có chiều rộng nút, nếu chưa thì đo lại ngay
                        if (buttonWidth[0] == 0) {
                            int editWidthNow = btnEdit.getWidth();
                            int deleteWidthNow = btnDelete.getWidth();
                            if (editWidthNow == 0 || deleteWidthNow == 0) {
                                btnEdit.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                                btnDelete.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                                editWidthNow = btnEdit.getMeasuredWidth();
                                deleteWidthNow = btnDelete.getMeasuredWidth();
                            }
                            buttonWidth[0] = editWidthNow + deleteWidthNow;
                        }

                        // Trạng thái hiện tại dựa theo vị trí dịch, tránh kẹt state khi quay lại/cancel
                        boolean isOpenNow = foreground.getTranslationX() != 0f;

                        // Vuốt sang trái
                        if (deltaX < -SWIPE_THRESHOLD && !isOpenNow) {
                            foreground.animate()
                                    .translationX(-buttonWidth[0]) // dịch sang trái
                                    .setDuration(550)
                                    .start();
                        }
                        // Vuốt sang phải để đóng lại
                        else if (deltaX > SWIPE_THRESHOLD / 2 && isOpenNow) {
                            foreground.animate()
                                    .translationX(0)
                                    .setDuration(550)
                                    .start();
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

    private String formatDateTime(long millis) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(new java.util.Date(millis));
    }
}
