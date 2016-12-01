package eu.urbancoders.zonkysniper.investing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.ZSViewActivity;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.MyInvestment;
import eu.urbancoders.zonkysniper.dataobjects.Rating;
import eu.urbancoders.zonkysniper.events.Invest;
import eu.urbancoders.zonkysniper.integration.ZonkyClient;
import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;

/**
 * Zadani captcha a investice
 */
public class InvestingActivity extends ZSViewActivity {

    boolean captchaLoaded = false;
    String captchaResponse = "";
    Button buttonInvest;
    Loan loan = null;
    int toInvest;
    Activity self;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investing);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView walletSum = (TextView) toolbar.findViewById(R.id.walletSum);
        if(ZonkySniperApplication.wallet != null) {
            walletSum.setText(getString(R.string.balance) + ZonkySniperApplication.wallet.getAvailableBalance() + getString(R.string.CZK));
        }

        self = this;

        Intent intent = getIntent();
        loan = (Loan) intent.getSerializableExtra("loan");
        toInvest = intent.getIntExtra("amount", 0);

        final WebView webview = (WebView) findViewById(R.id.captchaView);
        CookieManager.getInstance().setAcceptCookie(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                captchaLoaded = true;
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url); //this is controversial - see comments and other answers
                return true;
            }

        });
        webview.getSettings().setJavaScriptEnabled(true);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptFileSchemeCookies(true);
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(webview, true);
            cookieManager.acceptThirdPartyCookies(webview);
        }

        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setGeolocationEnabled(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setDatabaseEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        // Zoom out if the content width is greater than the width of the veiwport
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true); // allow pinch to zooom
        webview.getSettings().setDisplayZoomControls(false); // disable the default zoom controls on the page

        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setAppCachePath(appCachePath);

        webview.addJavascriptInterface(new CaptchaJavaScriptInterface(this, webview), "HtmlViewer");

        webview.loadUrl("https://app.zonky.cz/captcha.html");

        // obrazek jako pozadi headeru
        ImageView headerImage = (ImageView) findViewById(R.id.headerImage);
        Picasso.with(ZonkySniperApplication.getInstance().getApplicationContext())
                .load(ZonkyClient.BASE_URL + loan.getPhotos().get(0).getUrl())
                .into(headerImage);

        // detaily pujcky
        TextView header = (TextView) findViewById(R.id.header);
        TextView interestRate = (TextView) findViewById(R.id.interestRate);
        header.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(loan.getAmount()) + " Kč na "
                + loan.getTermInMonths() + " měsíců");
        header.setTextColor(Color.parseColor(Rating.getColor(loan.getRating())));

        // vybarvena urokova sazba
        interestRate.setText(Rating.getDesc(loan.getRating()) + " | " + new DecimalFormat("#.##").format(loan.getInterestRate() * 100) + "%");
        interestRate.setTextColor(Color.parseColor(Rating.getColor(loan.getRating())));

        toolbar.setTitle(loan.getName());

        // akce po stisku tlacitka
        buttonInvest = (Button) findViewById(R.id.buttonInvest);
        buttonInvest.setText(String.format(getString(R.string.invest), toInvest));
        buttonInvest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!captchaLoaded) {
                    Snackbar.make(view, R.string.waitForCaptcha, Snackbar.LENGTH_LONG).show();
                } else {
                    webview.loadUrl("javascript:window.HtmlViewer.showHTML" +
                            "(document.forms[0].elements[0].value);");
//                            "(document.getElementById(\"captcha_response\").value);");

                    Log.i(TAG, "Zavolal jsem javascript na ziskani CAPTCHY?");
                    // ted je potreba pockat na nacteni z webview - viz CaptchaJavascriptInterface
                }
            }
        });
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
                // tjadaa, investujeme...
                MyInvestment investment = new MyInvestment();
                investment.setLoanId(loan.getId());
                investment.setAmount(toInvest);
                investment.setCaptcha_response(captchaResponse);
                if(ZonkySniperApplication.getInstance().getUser() != null) {
                    investment.setInvestorNickname(ZonkySniperApplication.getInstance().getUser().getNickName());
                    investment.setInvestorId(ZonkySniperApplication.getInstance().getUser().getId());
                }
                EventBus.getDefault().post(new Invest.Request(investment));

                self.finish();
            } else {
                Log.i(TAG, "Captcha neni nactena jeste...");
                Snackbar.make(view, R.string.are_you_robot, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.finish();
                return true;
        }
        return true;
    }
}
