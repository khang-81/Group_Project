package com.example.hanoistudentgigs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hanoistudentgigs.R;

public class RoleSelectionActivity extends AppCompatActivity {

    private Button buttonStudent, buttonEmployer, buttonAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        buttonStudent = findViewById(R.id.buttonStudent);
        buttonEmployer = findViewById(R.id.buttonEmployer);
        buttonAdmin = findViewById(R.id.buttonAdmin);

        // Tất cả các nút đều sẽ mở màn hình đăng nhập.
        // Việc phân biệt vai trò sẽ được thực hiện sau khi người dùng đăng nhập thành công.
        View.OnClickListener loginClickListener = v -> {
            startActivity(new Intent(RoleSelectionActivity.this, LoginActivity.class));
        };

        buttonStudent.setOnClickListener(loginClickListener);
        buttonEmployer.setOnClickListener(loginClickListener);
        buttonAdmin.setOnClickListener(loginClickListener);
    }
}
