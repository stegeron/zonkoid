package eu.urbancoders.zonkysniper.investing;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.SettingsUser;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSViewActivity;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.Investor;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.MyInvestment;
import eu.urbancoders.zonkysniper.dataobjects.Rating;
import eu.urbancoders.zonkysniper.events.GetWallet;
import eu.urbancoders.zonkysniper.events.Invest;
import eu.urbancoders.zonkysniper.events.InvestAdditional;
import eu.urbancoders.zonkysniper.integration.ZonkyClient;
import eu.urbancoders.zonkysniper.wallet.WalletActivity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;

/**
 * Zadani captcha a investice
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Copyright 2019
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
public class InvestingActivity extends ZSViewActivity {

    Button buttonInvest;
    Loan loan = null;
    int toInvest;
    Activity self;
    private Toolbar toolbar;
    private TextView walletSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investing);

        Intent intent = getIntent();
        loan = (Loan) intent.getSerializableExtra("loan");
        ZonkySniperApplication.getInstance().setCurrentLoanId(loan.getId());
        toInvest = intent.getIntExtra("amount", 0);

        self = this;

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // reklama
        mAdView = findViewById(R.id.adView);
        initAndLoadAd(mAdView);

        walletSum = toolbar.findViewById(R.id.walletSum);
        walletSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // prejit na SettingsUser, pokud nejsem prihlaseny.
                if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
                    Intent userSettingsIntent = new Intent(InvestingActivity.this, SettingsUser.class);
                    startActivity(userSettingsIntent);
                } else {
                    Intent walletIntent = new Intent(InvestingActivity.this, WalletActivity.class);
                    startActivity(walletIntent);
                }
            }
        });
        if(ZonkySniperApplication.wallet != null) {
            walletSum.setText(getString(R.string.balance) + ZonkySniperApplication.wallet.getAvailableBalance() + getString(R.string.CZK));
        }

        // obrazek jako pozadi headeru
        ImageView headerImage = findViewById(R.id.headerImage);
        if(loan.getPhotos() != null && loan.getPhotos().size() > 0 && loan.getPhotos().get(0) != null) {
            try {
                Picasso.with(ZonkySniperApplication.getInstance().getApplicationContext())
                        .load(ZonkyClient.BASE_URL + loan.getPhotos().get(0).getUrl())
                        .into(headerImage);
            } catch (Exception e) {
                Log.w(TAG, "Chybí obrázek, smůla...");
            }
        }

        // detaily pujcky
        TextView header = findViewById(R.id.header);
        TextView interestRate = findViewById(R.id.interestRate);
        header.setText(Constants.FORMAT_NUMBER_NO_DECIMALS.format(loan.getAmount()) + " Kč na "
                + loan.getTermInMonths() + " měsíců");
        header.setTextColor(Color.parseColor(Rating.getColor(loan.getRating())));

        // vybarvena urokova sazba
        interestRate.setText(Rating.getDesc(loan.getRating()) + " | " + new DecimalFormat("#.##").format(loan.getInterestRate() * 100) + "%");
        interestRate.setTextColor(Color.parseColor(Rating.getColor(loan.getRating())));

        toolbar.setTitle(loan.getName());

        // akce po stisku tlacitka
        buttonInvest = findViewById(R.id.buttonInvest);
        if(loan.getMyInvestment() != null) {
            buttonInvest.setText(String.format(getString(R.string.investAdditional), toInvest));
        } else {
            // prvni investice
            buttonInvest.setText(String.format(getString(R.string.invest), toInvest));
        }
        buttonInvest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doInvest();
                self.finish();
            }
        });

        // pokud uzivatel jeste ani nezadal heslo (rovnou jde z rich notifky po instalaci), soupni ho na Nastaveni prihlasovani
        if (!ZonkySniperApplication.getInstance().isLoginAllowed()) {
            Intent userSettingsIntent = new Intent(InvestingActivity.this, SettingsUser.class);
            startActivity(userSettingsIntent);
        } else {
            EventBus.getDefault().post(new GetWallet.Request());
        }

        // zjistit, v jakem stavu byl naposledy Investor (ACTIVE, PASSIVE nebo BLOCKED?)
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        if (sp.getString(Constants.SHARED_PREF_INVESTOR_STATUS, "").equals(Investor.Status.BLOCKED.name())
            || sp.getString(Constants.SHARED_PREF_INVESTOR_STATUS, "").equals(Investor.Status.PASSIVE.name())
                ) {
            /**
             * Blokni investovani, je potreba zaplatit
             */
            buttonInvest.setEnabled(false);
            Intent zonkoidWalletIntent = new Intent(getApplicationContext(), WalletActivity.class);
            zonkoidWalletIntent.putExtra("tab", 1);
            redWarning(walletSum, getString(R.string.please_pay), zonkoidWalletIntent, "Přejít k platbě");
        }

        if ("OPEN_INVESTING_FROM_NOTIFICATION".equalsIgnoreCase(getIntent().getAction())) {
            // hned zrusit notifikaci, jestli jsme sem vlezli z akcniho tlacitka
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(666);

            // a hned zainvestovat
            doInvest();
            self.finish();
            return;
        }
    }

    private void doInvest() {
        // tjadaa, investujeme...
        MyInvestment investment = new MyInvestment();
        investment.setLoanId(loan.getId());
        investment.setAmount((double) toInvest);
        if(ZonkySniperApplication.getInstance().getUser() != null) {
            investment.setInvestorNickname(ZonkySniperApplication.getInstance().getUser().getNickName());
            investment.setInvestorId(ZonkySniperApplication.getInstance().getUser().getId());
        }
        if(loan.getMyInvestment() != null) {
            // doinvestice
            Double doinvestAmount = loan.getMyInvestment().getFirstAmount() + toInvest;
            investment.setAmount(doinvestAmount);
            investment.setId(loan.getMyInvestment().getId());
            EventBus.getDefault().post(new InvestAdditional.Request(investment));
        } else {
            // prvni investice
            EventBus.getDefault().post(new Invest.Request(investment));
        }
    }

//    class CaptchaJavaScriptInterface {
//
//        private Context ctx;
//        private View view;
//
//        CaptchaJavaScriptInterface(Context ctx, View view) {
//            this.ctx = ctx;
//            this.view = view;
//        }
//
//        @JavascriptInterface
//        public void showHTML(String captchaHtml) {
//            captchaResponse = captchaHtml;
//
//            if (!captchaResponse.isEmpty()) {
//                // tjadaa, investujeme...
//                MyInvestment investment = new MyInvestment();
//                investment.setLoanId(loan.getId());
//                investment.setAmount(Double.valueOf(toInvest));
//                if(!captchaResponse.equalsIgnoreCase("NOT_REQUIRED")) {
//                    investment.setCaptcha_response(captchaResponse);
//                }
//                if(ZonkySniperApplication.getInstance().getUser() != null) {
//                    investment.setInvestorNickname(ZonkySniperApplication.getInstance().getUser().getNickName());
//                    investment.setInvestorId(ZonkySniperApplication.getInstance().getUser().getId());
//                }
//                if(loan.getMyInvestment() != null) {
//                    // doinvestice
//                    Double doinvestAmount = loan.getMyInvestment().getFirstAmount() + toInvest;
//                    investment.setAmount(doinvestAmount);
//                    investment.setId(loan.getMyInvestment().getId());
//                    EventBus.getDefault().post(new InvestAdditional.Request(investment));
//                } else {
//                    // prvni investice
//                    EventBus.getDefault().post(new Invest.Request(investment));
//                }
//
//                self.finish();
//            } else {
//                Log.i(TAG, "Captcha neni nactena jeste...");
//                yellowWarning(view, getString(R.string.are_you_robot), Snackbar.LENGTH_LONG);
//            }
//        }
//    }

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWalletReceived(GetWallet.Response evt) {
        walletSum.setText(getString(R.string.balance) + evt.getWallet().getAvailableBalance() + getString(R.string.CZK));
        ZonkySniperApplication.wallet = evt.getWallet();
    }
}
