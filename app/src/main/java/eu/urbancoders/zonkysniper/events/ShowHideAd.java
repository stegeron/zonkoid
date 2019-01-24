package eu.urbancoders.zonkysniper.events;

import com.google.android.gms.ads.AdView;

/**
 * Zobrazit nebo skryt reklamy
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
