package com.example.hanoistudentgigs.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hanoistudentgigs.R;

public class XemTinActivity extends AppCompatActivity {

    private TextView txtDescription;
    private Button btnBack, btnShare;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xemtin);

        // Liên kết với ID trong layout
        txtDescription = findViewById(R.id.txtDescription);
        btnBack = findViewById(R.id.btnSave);


        // Nhận nội dung từ Intent
        String postContent = getIntent().getStringExtra("post_content");
        if (postContent != null) {
            txtDescription.setText(postContent);
        }

        // Nút quay lại
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(XemTinActivity.this, QLTinActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });



    }


}
