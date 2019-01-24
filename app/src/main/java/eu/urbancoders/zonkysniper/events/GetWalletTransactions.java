package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.dataobjects.WalletTransaction;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Ziskani seznamu transakci v penezence
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 19.04.2017
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

public class GetWalletTransactions {

    public static class Request {
        Calendar calendar = new GregorianCalendar();
        int numberOfItemsMax;

        public Request(int numberOfDays, int numberOfItemsMax) {
            calendar.add(Calendar.DAY_OF_YEAR, -numberOfDays);
            this.numberOfItemsMax = numberOfItemsMax;
        }

        public int getNumberOfItemsMax() {
            return numberOfItemsMax;
        }

        public String getTransactionDateFromFormatted() {
            return Constants.DATE_YYYY_MM_DD.format(calendar.getTime());
        }
    }

    public static class Response {
        List<WalletTransaction> walletTransactions;

        public Response(List<WalletTransaction> walletTransactions) {
            this.walletTransactions = walletTransactions;
        }

        public List<WalletTransaction> getWalletTransactions() {
            return walletTransactions;
        }
    }

    public static class Failure {

    }

//    public static void main(String[] a) {
//        GetWalletTransactions.Request req = new GetWalletTransactions.Request(365);
//        System.out.println(req.getTransactionDateFromFormatted());
//    }
}
