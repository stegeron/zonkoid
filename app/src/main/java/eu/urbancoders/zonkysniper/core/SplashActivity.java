package eu.urbancoders.zonkysniper.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import eu.urbancoders.zonkysniper.MainNewActivity;
import eu.urbancoders.zonkysniper.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainNewActivity.class);
        startActivity(intent);
        finish();
    }
}
