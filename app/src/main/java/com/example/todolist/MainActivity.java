package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<ToDoListItems> toDoListItems;
    private ToDoListItemsAdapter arrayAdapter;
    private FloatingActionButton btnAdd;
    private ToDoDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.toDoList);
        btnAdd = findViewById(R.id.btnAdd);
        dbHelper = new ToDoDbHelper(this);

        toDoListItems = dbHelper.getAllTodos();

        arrayAdapter = new ToDoListItemsAdapter(this, toDoListItems);
        listView.setAdapter(arrayAdapter);

		btnAdd.setOnClickListener(v -> {
			BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
			View sheetView = LayoutInflater.from(MainActivity.this).inflate(R.layout.bottom_sheet_add_item, null);
			bottomSheetDialog.setContentView(sheetView);

			EditText inputTitle = sheetView.findViewById(R.id.edtTitle);
			EditText inputDescription = sheetView.findViewById(R.id.edtDescription);
			Button btnCancel = sheetView.findViewById(R.id.btnCancel);
			Button btnAddConfirm = sheetView.findViewById(R.id.btnAddConfirm);
			Button btnPickStart = sheetView.findViewById(R.id.btnPickStart);
			Button btnPickEnd = sheetView.findViewById(R.id.btnPickEnd);

			final Long[] startHolder = { null };
			final Long[] endHolder = { null };

			btnPickStart.setOnClickListener(xx -> {
				java.util.Calendar cal = java.util.Calendar.getInstance();
				new android.app.DatePickerDialog(MainActivity.this, (view1, y, m, d) -> {
					java.util.Calendar c = java.util.Calendar.getInstance();
					c.set(y, m, d, 0, 0, 0);
					c.set(java.util.Calendar.MILLISECOND, 0);
					startHolder[0] = c.getTimeInMillis();
					btnPickStart.setText(String.format("%02d/%02d/%04d", d, m+1, y));
				}, cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH), cal.get(java.util.Calendar.DAY_OF_MONTH)).show();
			});

			btnPickEnd.setOnClickListener(xx -> {
				java.util.Calendar now = java.util.Calendar.getInstance();
				int initHour = now.get(java.util.Calendar.HOUR_OF_DAY);
				int initMin = now.get(java.util.Calendar.MINUTE);
				new android.app.TimePickerDialog(MainActivity.this, (view12, hourOfDay, minute) -> {
					java.util.Calendar base = java.util.Calendar.getInstance();
					if (startHolder[0] != null) {
						base.setTimeInMillis(startHolder[0]);
						// normalize to start of day of the chosen date
						base.set(java.util.Calendar.HOUR_OF_DAY, 0);
						base.set(java.util.Calendar.MINUTE, 0);
						base.set(java.util.Calendar.SECOND, 0);
						base.set(java.util.Calendar.MILLISECOND, 0);
					} else {
						// if date not chosen yet, use today's date at 00:00
						base.set(java.util.Calendar.HOUR_OF_DAY, 0);
						base.set(java.util.Calendar.MINUTE, 0);
						base.set(java.util.Calendar.SECOND, 0);
						base.set(java.util.Calendar.MILLISECOND, 0);
					}
					base.add(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
					base.add(java.util.Calendar.MINUTE, minute);
					startHolder[0] = base.getTimeInMillis();
					endHolder[0] = null; // no end date/time
					btnPickEnd.setText(String.format("%02d:%02d", hourOfDay, minute));
				}, initHour, initMin, true).show();
			});

			btnCancel.setOnClickListener(x -> bottomSheetDialog.dismiss());
			btnAddConfirm.setOnClickListener(x -> {
				String title = inputTitle.getText().toString().trim();
				String description = inputDescription.getText().toString().trim();
				if (title.isEmpty()) {
					Toast.makeText(MainActivity.this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
					return;
				}
				long newId = dbHelper.insertTodo(title, description, false, startHolder[0], null);
				ToDoListItems newItem = new ToDoListItems((int)newId, title, description, false, startHolder[0]);
				toDoListItems.add(newItem);
				arrayAdapter.notifyDataSetChanged();
				Toast.makeText(MainActivity.this, "Đã thêm công việc mới", Toast.LENGTH_SHORT).show();
				bottomSheetDialog.dismiss();
			});

			bottomSheetDialog.show();
		});
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            int position = data.getIntExtra("position", -1);
            String newTitle = data.getStringExtra("newTitle");
            String newDescription = data.getStringExtra("newDescription");
            Long newStart = data.hasExtra("startDate") ? data.getLongExtra("startDate", 0L) : null;
            if (newStart != null && newStart == 0L) newStart = null;

            if (position != -1) {
                ToDoListItems item = toDoListItems.get(position);
                item.setTitle(newTitle);
                item.setDescription(newDescription);
				item.setDateTimeMillis(newStart);
                dbHelper.updateTodo(item.getToDoItemID(), newTitle, newDescription, item.isCompleted(), newStart, null);
                arrayAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
