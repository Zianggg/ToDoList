package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditItemActivity extends AppCompatActivity {

    private EditText edtTitle, edtDescription;
    private Button btnSave, btnCancel, btnPickStartEdit, btnPickEndEdit;
    private Long startDateMillis = null;
    private int itemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnPickStartEdit = findViewById(R.id.btnPickStartEdit);
        btnPickEndEdit = findViewById(R.id.btnPickEndEdit);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        if (intent.hasExtra("startDate")) {
            startDateMillis = intent.getLongExtra("startDate", 0L);
            if (startDateMillis == 0L) startDateMillis = null;
        }
        // no endDate anymore
        itemPosition = intent.getIntExtra("position", -1);

        edtTitle.setText(title);
        edtDescription.setText(description);
        if (startDateMillis != null) {
            java.util.Date dt = new java.util.Date(startDateMillis);
            btnPickStartEdit.setText(new java.text.SimpleDateFormat("dd/MM/yyyy").format(dt));
            btnPickEndEdit.setText(new java.text.SimpleDateFormat("HH:mm").format(dt));
        }

        btnPickStartEdit.setOnClickListener(v -> {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            new android.app.DatePickerDialog(this, (view, y, m, d) -> {
                java.util.Calendar c = java.util.Calendar.getInstance();
                c.set(y, m, d, 0, 0, 0);
                c.set(java.util.Calendar.MILLISECOND, 0);
                startDateMillis = c.getTimeInMillis();
                btnPickStartEdit.setText(String.format("%02d/%02d/%04d", d, m+1, y));
            }, cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH), cal.get(java.util.Calendar.DAY_OF_MONTH)).show();
        });

        btnPickEndEdit.setOnClickListener(v -> {
            java.util.Calendar now = java.util.Calendar.getInstance();
            int initHour = now.get(java.util.Calendar.HOUR_OF_DAY);
            int initMin = now.get(java.util.Calendar.MINUTE);
            new android.app.TimePickerDialog(this, (view, hourOfDay, minute) -> {
                java.util.Calendar base = java.util.Calendar.getInstance();
                if (startDateMillis != null) {
                    base.setTimeInMillis(startDateMillis);
                }
                base.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
                base.set(java.util.Calendar.MINUTE, minute);
                base.set(java.util.Calendar.SECOND, 0);
                base.set(java.util.Calendar.MILLISECOND, 0);
                startDateMillis = base.getTimeInMillis();
                btnPickEndEdit.setText(String.format("%02d:%02d", hourOfDay, minute));
            }, initHour, initMin, true).show();
        });

        btnSave.setOnClickListener(v -> {
            String newTitle = edtTitle.getText().toString().trim();
            String newDescription = edtDescription.getText().toString().trim();

            if (newTitle.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("newTitle", newTitle);
            resultIntent.putExtra("newDescription", newDescription);
            resultIntent.putExtra("position", itemPosition);
            if (startDateMillis != null) resultIntent.putExtra("startDate", startDateMillis);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        btnCancel.setOnClickListener(v -> finish());
    }
}
