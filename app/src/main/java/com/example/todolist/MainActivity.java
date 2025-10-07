package com.example.todolist;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<ToDoListItems> toDoListItems;
    private ToDoListItemsAdapter arrayAdapter;
    private FloatingActionButton btnAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.toDoList);
        btnAdd = findViewById(R.id.btnAdd);

        toDoListItems = new ArrayList<>();
        toDoListItems.add(new ToDoListItems(01, "buy milk", "go to the store and buy milk", false));

        arrayAdapter = new ToDoListItemsAdapter(this, toDoListItems);
        listView.setAdapter(arrayAdapter);

        listView.setAdapter(arrayAdapter);

        btnAdd.setOnClickListener(v -> {
            // Tạo layout nhập liệu
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Thêm công việc mới");

            // Tạo view gồm 2 ô nhập
            android.widget.LinearLayout layout = new android.widget.LinearLayout(MainActivity.this);
            layout.setOrientation(android.widget.LinearLayout.VERTICAL);
            layout.setPadding(50, 40, 50, 10);

            final android.widget.EditText inputTitle = new android.widget.EditText(MainActivity.this);
            inputTitle.setHint("Tiêu đề công việc");
            layout.addView(inputTitle);

            final android.widget.EditText inputDescription = new android.widget.EditText(MainActivity.this);
            inputDescription.setHint("Mô tả chi tiết");
            layout.addView(inputDescription);

            builder.setView(layout);

            // Nút Thêm
            builder.setPositiveButton("Thêm", (dialog, which) -> {
                String title = inputTitle.getText().toString().trim();
                String description = inputDescription.getText().toString().trim();

                if (title.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo item mới
                int newId = toDoListItems.size() + 1;
                // ID duy nhất
                ToDoListItems newItem = new ToDoListItems(newId, title, description, false);
                toDoListItems.add(newItem);

                // Cập nhật ListView
                arrayAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Đã thêm công việc mới", Toast.LENGTH_SHORT).show();
            });

            // Nút Hủy
            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

            builder.show();
        });
    }
}