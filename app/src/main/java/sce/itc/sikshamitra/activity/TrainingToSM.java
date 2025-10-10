package sce.itc.sikshamitra.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.databasehelper.DatabaseHelper;
import sce.itc.sikshamitra.databinding.ActivityTrainingShikshaMitraBinding;

public class TrainingToSM extends AppCompatActivity {
    private static final String TAG = "TrainingToSMActivity";
    private Toolbar toolbar;
    private ActivityTrainingShikshaMitraBinding binding;
    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_training_shiksha_mitra);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Training To Shiksha Mitra");
        }
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        populateData();

        clickEvent();
    }

    private void clickEvent() {
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()){

                }
            }
        });

    }

    private void populateData() {
        dbHelper = DatabaseHelper.getInstance(this);
    }

    private boolean checkValidation()
    {
        if (binding.editSmCount.getText().toString().trim().isEmpty()) {
            binding.editSmCount.setError("Please enter training date");
            return false;
        }

        return true;
    }
}