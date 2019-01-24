package eu.urbancoders.zonkysniper.events;

import org.solovyev.android.checkout.Purchase;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 14.07.2017
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

public class BookPurchase extends AbstractEvent {

    public static class Request {
        Purchase purchase;
        Double priceToPay;
        int investorId;

        public Request(Purchase purchase, Double priceToPay, int investorId) {
            this.purchase = purchase;
            this.priceToPay = priceToPay;
            this.investorId = investorId;
        }

        public Purchase getPurchase() {
            return purchase;
        }

        public Double getPriceToPay() {
            return priceToPay;
        }

        public int getInvestorId() {
            return investorId;
        }
    }

    public static class Response {
        Boolean purchaseBooked = false;
        Purchase purchase;

        public Response(Boolean purchaseBooked, Purchase purchase) {
            this.purchaseBooked = purchaseBooked;
            this.purchase = purchase;
        }

        public Boolean isPurchaseBooked() {
            return purchaseBooked;
        }

        public Purchase getPurchase() {
            return purchase;
        }
    }

    public static class Failure {

    }
}
