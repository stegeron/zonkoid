package eu.urbancoders.zonkysniper.events;

import com.google.android.gms.ads.AdView;

/**
 * Zobrazit nebo skryt reklamy
 */
public class ShowHideAd extends AbstractEvent {

    public static class Request {
        boolean adRemovePurchased;
        AdView mAdView;

        public Request(boolean adRemovePurchased) {
            this.adRemovePurchased = adRemovePurchased;
//            this.mAdView = mAdView;
        }

        public AdView getmAdView() {
            return mAdView;
        }

        public boolean isAdRemovePurchased() {
            return adRemovePurchased;
        }
    }

    public static class Response {

    }

    public static class Failure {

    }
}
