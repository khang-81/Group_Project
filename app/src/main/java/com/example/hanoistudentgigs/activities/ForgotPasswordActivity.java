package com.example.hanoistudentgigs.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.example.hanoistudentgigs.R;

public class ForgotPasswordActivity extends AppCompatActivity {
    private TextInputEditText editTextEmail;
    private Button buttonSendResetLink;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextResetEmail);
        buttonSendResetLink = findViewById(R.id.buttonSendResetLink);

        buttonSendResetLink.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Vui lòng nhập email của bạn.", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Link đặt lại mật khẩu đã được gửi đến email của bạn.", Toast.LENGTH_LONG).show();
                            finish(); // Đóng màn hình sau khi gửi link
                        } else {
                            Toast.makeText(this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}