package sce.itc.sikshamitra.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Change title programmatically
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.White));
        toolbar.setTitle("Home");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        clickEvent();
    }

    private void clickEvent() {
        binding.btnLogin.setOnClickListener(v -> {
            if (checkValidInputs()) {
                Intent intent = new Intent(Login.this, Home.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean checkValidInputs() {
        boolean isValid = true;
        if (binding.editUsername.getText().toString().trim().isEmpty()) {
            binding.editUsername.setError("Please enter username");
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            isValid = false;
            return isValid;
        }
        if (binding.editPwd.getText().toString().trim().isEmpty()) {
            binding.editPwd.setError("Please enter password");
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            isValid = false;
            return isValid;
        }

        return isValid;

    }
}