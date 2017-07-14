package eu.urbancoders.zonkysniper.events;

import org.solovyev.android.checkout.Purchase;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 14.07.2017
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
