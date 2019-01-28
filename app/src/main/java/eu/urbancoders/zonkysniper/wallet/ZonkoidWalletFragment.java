package eu.urbancoders.zonkysniper.wallet;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;

import java.util.List;

import javax.annotation.Nonnull;

import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSFragment;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.events.GetConfiguration;
import eu.urbancoders.zonkysniper.events.ShowHideAd;

/**
 * Zobrazení zůstatku u Zonkoida, platba inapp, historie plateb, stažení výpisu poplatků a spol.
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 25.05.2017
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

public class ZonkoidWalletFragment extends ZSFragment {

    private static final String TAG = ZonkoidWalletFragment.class.getName();

    TextView balance, feeVariants;
    WalletActivity walletActivity;
    public static ProgressBar kolecko;
    Button buyAdRemove;
    EditText voucherCode;

    public static ZonkoidWalletFragment newInstance() {
        ZonkoidWalletFragment fragment = new ZonkoidWalletFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ZonkoidWalletFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_zonkoid_wallet, container, false);

        walletActivity = (WalletActivity) getActivity();

        balance = rootView.findViewById(R.id.balance);

        feeVariants = rootView.findViewById(R.id.feeVariants);
        feeVariants.setText(Html.fromHtml(getResources().getString(R.string.feeVariants)));

        kolecko = rootView.findViewById(R.id.kolecko);

        buyAdRemove = rootView.findViewById(R.id.buyAdRemove);

        voucherCode = rootView.findViewById(R.id.voucherCode);
        voucherCode.setText(ZonkySniperApplication.getSharedPrefs().getString(Constants.VOUCHER_ID, ""));

        Checkout checkout = ZonkySniperApplication.getInstance().getCheckout();
        final Inventory.Request request = Inventory.Request.create();
        request.loadAllPurchases();
        request.loadSkus(ProductTypes.SUBSCRIPTION, Constants.SUBSCRIPTION_AD_REMOVE);
        checkout.loadInventory(request, new Inventory.Callback() {
            boolean adRemovePurchased = false;

            @Override
            public void onLoaded(@Nonnull Inventory.Products products) {
                final Inventory.Product product = products.get(ProductTypes.SUBSCRIPTION);
                if (product.supported) {
                    final List<Purchase> purchases = product.getPurchases();
                    if (purchases.size() > 0) {
                        for (int i = 0; i < purchases.size(); i++) {
                            if (purchases.get(i).sku.equals(Constants.SUBSCRIPTION_AD_REMOVE)) {
                                onShowHideAd(null); // zneuziju tuhle eventu na disablovani tlacitka
                                break;
                            }
                        }
                    }
                }
            }
        });


        return rootView;
    }

    /**
     * Pokud koupil predplatne na odstraneni reklamy, disabluj tlacitko
     * @param evt
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowHideAd(@Nullable ShowHideAd.Request evt) {
        // mam predplaceno, zmenim tlacitko
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                buyAdRemove.setText(R.string.bought);
                buyAdRemove.setEnabled(false);
                buyAdRemove.getBackground().setColorFilter(getResources().getColor(R.color.greyLighter), PorterDuff.Mode.MULTIPLY);
                buyAdRemove.setTextColor(getResources().getColor(R.color.greyDark));
            }
        });
    }

    /**
     * Dosazeni spravnych poctu kreditu za danou cenu
     * @param evt
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConfigurationReceived(GetConfiguration.Response evt) {
        // nothing to do
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static void roztocKolecko() {
        kolecko.setVisibility(View.VISIBLE);
    }

    public static void zastavKolecko() {
        kolecko.setVisibility(View.GONE);
    }
}
