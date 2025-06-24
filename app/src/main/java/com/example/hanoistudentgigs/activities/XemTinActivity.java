package com.example.hanoistudentgigs.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hanoistudentgigs.R;
public class XemTinActivity extends AppCompatActivity {
    private TextView txtTitle, txtCompany, txtSalary, txtLocation, txtContact, txtDescription;
    private Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xemtin);
        // Liên kết các View
        txtTitle = findViewById(R.id.txtTitle);
        txtCompany = findViewById(R.id.txtCompany);
        txtSalary = findViewById(R.id.txtSalary);
        txtLocation = findViewById(R.id.txtLocation);
        txtContact = findViewById(R.id.txtContact);
        txtDescription = findViewById(R.id.txtDescription);
        btnBack = findViewById(R.id.btnBack);
        // Nhận dữ liệu từ Intent và gán vào TextView
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            txtTitle.setText(extras.getString("title", ""));
            txtCompany.setText(extras.getString("company", ""));
            txtSalary.setText(extras.getString("salary", ""));
            txtLocation.setText(extras.getString("location", ""));
            txtContact.setText(extras.getString("contact", ""));
            txtDescription.setText(extras.getString("description", ""));
        }
        // Nút quay lại
        btnBack.setOnClickListener(v -> {
            finish(); // chỉ cần finish để quay lại activity trước đó (QLTinActivity)
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed(); // giữ lại behavior mặc định (quay lại)
    }
}