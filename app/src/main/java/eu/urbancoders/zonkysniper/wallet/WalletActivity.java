package eu.urbancoders.zonkysniper.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.urbancoders.zonkysniper.R;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.core.ZSViewActivity;
import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;
import eu.urbancoders.zonkysniper.dataobjects.ZonkoidWallet;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.Portfolio;
import eu.urbancoders.zonkysniper.events.BookPurchase;
import eu.urbancoders.zonkysniper.events.GetZonkoidWallet;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.EmptyRequestListener;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.Sku;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WalletActivity extends ZSViewActivity {

    private static final String TAG = WalletActivity.class.getName();
    protected Portfolio portfolio;
    private ActivityCheckout mCheckout;
    private boolean processingPurchase;
    private ZonkoidWallet zonkoidWallet;

    private List<Sku> SKUs = new ArrayList<>(0);
    private static final String zonkoid_consumable_prefix = "zonkoid_consumable_";

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    public ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_wallet);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        TabLayout.Tab tab = tabLayout.getTabAt(getIntent().getIntExtra("tab", 0));
        tab.select();

        final Billing billing = ZonkySniperApplication.getInstance().getBilling();
        mCheckout = Checkout.forActivity(this, billing);
        mCheckout.start();
        final Inventory.Request request = Inventory.Request.create();
        request.loadPurchases(ProductTypes.IN_APP);
        request.loadSkus(ProductTypes.IN_APP, getInAppSkus());
        mCheckout.loadInventory(request, new WalletActivity.InventoryCallback());
    }

    /**
     * Seznam SKUs
     * @return
     */
    private List<String> getInAppSkus() {
        final List<String> skus = new ArrayList<>();
        skus.addAll(Arrays.asList(
                zonkoid_consumable_prefix + "10",
                zonkoid_consumable_prefix + "20",
                zonkoid_consumable_prefix + "30",
                zonkoid_consumable_prefix + "40",
                zonkoid_consumable_prefix + "50",
                zonkoid_consumable_prefix + "60",
                zonkoid_consumable_prefix + "70",
                zonkoid_consumable_prefix + "80",
                zonkoid_consumable_prefix + "90",
                zonkoid_consumable_prefix + "100",
                zonkoid_consumable_prefix + "110",
                zonkoid_consumable_prefix + "120"
        ));
        return skus;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCheckout.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_messaging, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return WalletFragment.newInstance();
            } else if(position == 1) {
                return ZonkoidWalletFragment.newInstance();
            } else {
                return PlaceholderFragment.newInstance(999);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Zonky peněženka";
                case 1:
                    return "Zonkoid peněženka";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_messages_from_zonky, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.messages_title);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    @Override
    public void onDestroy() {
        mCheckout.stop();
        super.onDestroy();
    }

    /**
     * Zaplatit za investice
     * @param view
     */
    public void buyItem(View view) {

        if(processingPurchase) {
            yellowWarning(view, "Ještě nebyla dokončena předchozí platba. Zkuste to prosím později.", 0);
            return;
        }

        if(SKUs.isEmpty()) {
            Log.w(TAG, "Nepodarilo se nacist SKUs.");
            yellowWarning(view, getString(R.string.failed_sku_load), Snackbar.LENGTH_INDEFINITE);
        } else {
            Sku sku2buy = null;
            for(Sku sku : SKUs) {
                if(sku.detailedPrice.amount/1000000d <= Math.abs(zonkoidWallet.getBalance())) {
                    sku2buy = sku;
                }
            }
            if(sku2buy != null) {
                // zablokovat tlacitko, aby nemohl platit omylem vickrat
                processingPurchase = true;
                mCheckout.startPurchaseFlow(
                        ProductTypes.IN_APP,
                        sku2buy.id.code,
                        String.valueOf(ZonkySniperApplication.getInstance().getUser().getId()),
                        new PurchaseListener(sku2buy.detailedPrice.amount/1000000d)
                );
            } else {
                Log.i(TAG, "Částka pro zaplacení je příliš nízká ("+ zonkoidWallet.getBalance() +"), minimálně lze platit " + SKUs.get(0).price );
                yellowWarning(view, String.format(getString(R.string.minimum_price_warning),
                        Constants.FORMAT_NUMBER_WITH_DECIMALS.format(SKUs.get(0).detailedPrice.amount/1000000d) + " Kč"), Snackbar.LENGTH_LONG);
            }
        }
    }

    /**
     * Po zaplaceni zavolat validaci, zalogovani a konzumaci
     */
    private class PurchaseListener extends EmptyRequestListener<Purchase> {
        final double priceToPay;

        @Override
        public void onError(int response, @Nonnull Exception e) {
            System.out.print(true);// todo odblokovat tlacitko
        }

        @Override
        public void onSuccess(@Nonnull Purchase purchase) {
            // zvalidovat transakci na serveru a zabookovat
            EventBus.getDefault().post(new BookPurchase.Request(purchase, priceToPay, ZonkySniperApplication.getInstance().getUser().getId()));
        }

        public PurchaseListener(double priceToPay) {
            this.priceToPay = priceToPay;
        }
    }

    /**
     * Po konzumaci znovu nacist Zonkoid Wallet
     */
    private class ConsumeListener extends EmptyRequestListener<Object> {
        @Override
        public void onSuccess(@Nonnull Object result) {
            Log.i(TAG, "Consumed " + result.toString());
            if (ZonkySniperApplication.getInstance().getUser() != null) {
                // po konzumaci refreshnout Zonkoid Wallet
                EventBus.getDefault().post(new GetZonkoidWallet.Request(ZonkySniperApplication.getInstance().getUser().getId()));
            }
        }
    }

    /**
     * Po nacteni inventare overit, jestli neni nezkonzumovana platba a kdyztak zkonzumovat
     */
    private class InventoryCallback implements Inventory.Callback {
        @Override
        public void onLoaded(@Nonnull Inventory.Products products) {
            final Inventory.Product product = products.get(ProductTypes.IN_APP);
            if (!product.supported) {
                // todo billing is not supported, user can't purchase anything. Show warning in this case
                return;
            }

            SKUs = product.getSkus();

            /**
             * {"packageName":"eu.urbancoders.zonkysniper",
             * "productId":"zonkoid_consumable_10",
             * "purchaseTime":1498126587499,
             * "purchaseState":0,
             * "purchaseToken":"labkdchennlilbigbhmlpihe.AO-J1Ox1j1......0wTTnfbEJItWKUhQnin0iy2dHPfw"}
             */

            // konzumovat hned jak vlezu na stranku, kdyby neco zbyvalo/spadlo
            for (final Purchase purchase : product.getPurchases()) {

                // pro kazdou SKU, ktera je consumable
                if (purchase.sku != null && purchase.sku.startsWith(zonkoid_consumable_prefix)) {

                    // zjistit cenu, kterou klient zaplati
                    Double priceToPay = 0d;
                    for(Sku skuPrice : SKUs) {
                        if(skuPrice.id.code.equalsIgnoreCase(purchase.sku)) {
                            priceToPay = Double.valueOf(skuPrice.detailedPrice.amount)/1000000d;
                        }
                    }

                    // zvalidovat transakci na serveru a zabookovat
                    EventBus.getDefault().post(new BookPurchase.Request(purchase, priceToPay, ZonkySniperApplication.getInstance().getUser().getId()));
                }
            }
        }
    }

    /**
     * Pokud se podarilo platbu propsat do ZonkyCommander a ucetni vratil, ze je OK, pak konzumuj
     * @param evt
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPurchaseBooked(final BookPurchase.Response evt) {

        if(evt.isPurchaseBooked()) {
            // konzumovat
            mCheckout.whenReady(new Checkout.EmptyListener() {
                @Override
                public void onReady(@Nonnull BillingRequests requests) {
                    requests.consume(evt.getPurchase().token, new ConsumeListener());
                }
            });
        } else {
            Log.e(TAG, "Nepodarilo se zabookovat platbu: PurchaseToken: " + evt.getPurchase().token);
        }
    }

    public void setZonkoidWallet(ZonkoidWallet zonkoidWallet) {
        processingPurchase = false;
        this.zonkoidWallet = zonkoidWallet;
    }

    public ZonkoidWallet getZonkoidWallet() {
        return zonkoidWallet;
    }
}
