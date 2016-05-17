package eu.urbancoders.zonkysniper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
    public boolean startUCActivity(Class<? extends ZSViewActivity> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        return true;
    }

    @Subscribe
    public void dummyEventHandler(Void v) {

    }

    @Subscribe
    public void onUnresolvableError(UnresolvableError.Request evt) {
        Snackbar.make(findViewById(android.R.id.content), evt.getError().getError_description(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
