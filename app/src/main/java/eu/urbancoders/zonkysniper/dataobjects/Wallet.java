package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;

import eu.urbancoders.zonkysniper.core.ZonkySniperApplication;

/**
 * Šrajtofle, portmonka, kešeně, prkenice...
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 18.05.2016
 */
public class Wallet implements Serializable {

    /**
     * {
     * "id":2122,
     * "balance":327.33,
     * "availableBalance":127.33,
     * "blockedBalance":200.00,
     * "creditSum":28446.00,
     * "debitSum":0.00,
     * "variableSymbol":"2000130779",
     * "account":{
     *      "id":1,
     *      "accountNo":"0000002020010045",
     *      "accountBank":"6000",
     *      "accountName":"P2P JUMBO"
     *      }
     *  }
     */


    int id;
    double balance;
    double availableBalance;
    double blockedBalance;
    double creditSum;
    double debitSum;
    int variableSymbol;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getAvailableBalance() {
        // TODO FEEEEJJJJKKKKK
        if(true)
            return 123456d;

        return availableBalance;
    }

    public void setAvailableBalance(double availableBalance) {
        this.availableBalance = availableBalance;
    }

    public double getBlockedBalance() {
        return blockedBalance;
    }

    public void setBlockedBalance(double blockedBalance) {
        this.blockedBalance = blockedBalance;
    }

    public int getVariableSymbol() {
        return variableSymbol;
    }

    public void setVariableSymbol(int variableSymbol) {
        this.variableSymbol = variableSymbol;
    }

    public double getCreditSum() {
        return creditSum;
    }

    public void setCreditSum(double creditSum) {
        this.creditSum = creditSum;
    }

    public double getDebitSum() {
        return debitSum;
    }

    public void setDebitSum(double debitSum) {
        this.debitSum = debitSum;
    }
}
