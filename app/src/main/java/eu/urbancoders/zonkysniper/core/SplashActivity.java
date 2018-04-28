package eu.urbancoders.zonkysniper.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import eu.urbancoders.zonkysniper.MainNewActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainNewActivity.class);
        startActivity(intent);
        finish();
    }
}
