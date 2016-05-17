package eu.urbancoders.zonkysniper.dataobjects;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 18.05.2016
 */
public class Wallet {

//    {
//        "id":2122,
//        "balance":851.83,
//        "availableBalance":851.83,
//        "blockedBalance":0,
//        "variableSymbol":"2000130779",
//        "account":{
//              "id":1,
//              "accountNo":"0000002020010045",
//              "accountBank":"6000",
//              "accountName":"P2P JUMBO"
//    }


    int id;
    double balance;
    double availableBalance;
    double blockedBalance;
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
}
