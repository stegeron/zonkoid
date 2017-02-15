package eu.urbancoders.zonkysniper.core;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.events.UnresolvableError;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.05.2016
 */
public abstract class ZSViewActivity extends AppCompatActivity {

    public final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getAttributes().windowAnimations = R.style.FadeAnimation;
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Spusti libovolnou activity
     *
     * @param activity
     */
    public boolean startActivity(Class<? extends ZSViewActivity> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        return true;
    }

    @Subscribe
    public void dummyEventHandler(Void v) {

    }

    @Subscribe
    public void onUnresolvableError(UnresolvableError.Request evt) {
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.toolbar), evt.getError().getError_description(), Snackbar.LENGTH_INDEFINITE);
        View view = snackbar.getView();
        view.setBackgroundResource(R.color.warningYellow);
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.BLACK);

        snackbar.setAction("x", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.setActionTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorButton));
        snackbar.show();
    }
}
