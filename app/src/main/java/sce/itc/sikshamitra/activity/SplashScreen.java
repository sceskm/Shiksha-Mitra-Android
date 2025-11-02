package sce.itc.sikshamitra.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Date;

import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.ConstantField;
import sce.itc.sikshamitra.helper.PreferenceCommon;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLoggedIn()) {
                    navigateToHome();
                } else {
                    navigateToLogin();
                }
            }
        }, ConstantField.SPLASH_TIME_OUT);
    }

    private boolean isLoggedIn() {
        // Implement your logic to check if the user is logged in
        boolean isValid = false;
        if (PreferenceCommon.getInstance().getUserId() > 0) {
            if (Common.compareDates(PreferenceCommon.getInstance().getLastLoggedInDateTime(),
                    Common.iso8601Format.format(new Date())) > 0) {
                isValid = false;
            } else {
                isValid = true;
            }

        }
        return isValid; // Placeholder return value
    }

    private void navigateToHome() {
        Intent intent = null;
        if (PreferenceCommon.getInstance().getUserRoleId() == ConstantField.ROLE_ID_FIELD_TEAM)
            intent = new Intent(SplashScreen.this, AgencyHome.class);
        else if (PreferenceCommon.getInstance().getUserRoleId() == ConstantField.ROLE_ID_SHIKSHA_MITRA)
            intent = new Intent(this, Home.class);
        else
            intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SplashScreen.this, Login.class);
        startActivity(intent);
        finish();
    }
}