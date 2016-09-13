package eu.urbancoders.zonkysniper.user;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.ZSViewActivity;
import eu.urbancoders.zonkysniper.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.PasswordResetter;
import eu.urbancoders.zonkysniper.events.PasswordReset;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PasswordResetActivity extends ZSViewActivity {

    EditText usernamePassReset;
    Button buttonCaptcha;
    boolean captchaLoaded = false;
    String captchaResponse = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        usernamePassReset = (EditText) findViewById(R.id.usernamePassReset);
        if(ZonkySniperApplication.getInstance().isLoginAllowed()
                && !ZonkySniperApplication.getInstance().getUsername().equalsIgnoreCase("nekdo@zonky.cz")) {
            usernamePassReset.setText(ZonkySniperApplication.getInstance().getUsername());
        }

        final WebView webview = (WebView) findViewById(R.id.captchaView);
        CookieManager.getInstance().setAcceptCookie(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new CaptchaJavaScriptInterface(this, webview), "HtmlViewer");

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                captchaLoaded = true;
            }
        });

        webview.loadUrl("https://app.zonky.cz/captcha.html");


        // akce po stisku tlacitka
        buttonCaptcha = (Button) findViewById(R.id.buttonCaptcha);
        buttonCaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!captchaLoaded) {
                    Snackbar.make(view, R.string.waitForCaptcha, Snackbar.LENGTH_LONG).show();
                } else {
                    webview.loadUrl("javascript:window.HtmlViewer.showHTML" +
                            "(document.getElementById(\"captcha_response\").value);");

                    // ted je potreba pocka na nacteni z webview - viz CaptchaJavascriptInterface
                }
            }
        });

    }

    @Subscribe
    public void onPasswordReset(PasswordReset.Response evt) {
        Snackbar.make(findViewById(R.id.buttonCaptcha), "Další instrukce najdete v emailu.", Snackbar.LENGTH_LONG).show();
        buttonCaptcha.setEnabled(false);
    }

    @Subscribe
    public void onPasswordResetFailure(PasswordReset.Failure evt) {
        Snackbar.make(findViewById(R.id.buttonCaptcha), "Chyba "+evt.getCode()+", Reset hesla se nepodařil.", Snackbar.LENGTH_LONG).show();
    }

    class CaptchaJavaScriptInterface {

        private Context ctx;
        private View view;

        CaptchaJavaScriptInterface(Context ctx, View view) {
            this.ctx = ctx;
            this.view = view;
        }

        @JavascriptInterface
        public void showHTML(String captchaHtml) {
            captchaResponse = captchaHtml;

            if (!captchaResponse.isEmpty()) {
                PasswordResetter passwordResetter = new PasswordResetter(usernamePassReset.getText().toString(), captchaResponse);
                EventBus.getDefault().post(new PasswordReset.Request(passwordResetter));
            } else {
                Snackbar.make(view, R.string.are_you_robot, Snackbar.LENGTH_LONG).show();
            }
        }

    }

}
