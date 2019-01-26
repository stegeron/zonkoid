package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;
import java.util.List;

/**
 * Informace o investorovi
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 23.10.2016
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

public class Investor implements Serializable {

    /**
     * {
     * "id":13356,
     * "username":"ondrej.steger@gmail.com",
     * "email":"ondrej.steger@gmail.com",
     * "firstName":"Ondřej",
     * "surname":"Steger",
     * "nickName":"kath",
     * "phone":"777929442",
     * "bankAccount":{
     * "id":2864,
     * "accountNo":"0001150191040207",
     * "accountBank":"0100",
     * "accountName":null
     * },
     * "permanentAddress":{
     * "street":"U Lesního divadla",
     * "streetNo":"38",
     * "city":"Liberec",
     * "zipCode":"46014",
     * "country":"CZ",
     * "yearFromLive":null
     * },
     * "contactAddress":{
     * "street":null,
     * "streetNo":null,
     * "city":null,
     * "zipCode":null,
     * "country":"CZ",
     * "yearFromLive":null
     * },
     * "roles":["SCOPE_APP_WEB","ROLE_SECURED_USER","ROLE_INVESTOR"],
     * "unreadNotificationsCount":0,
     * "showNotificationSettings":false,
     * "status":"ACTIVE",
     * "dateRegistered":"2016-02-22T16:10:29.464+01:00",
     * "dateLastLoggedIn":"2016-10-23T22:08:30.924+02:00",
     * "daysSinceLastLogin":0,
     * "userMarketing":"webReg",
     * "webId":"67be5282-0728-4a59-9070-7fe3c755e2e9"
     * }
     */
    int id;
    String username;
    String email;
    String firstName;
    String surname;
    String nickName;
    String phone;
    PermanentAddress permanentAddress = new PermanentAddress();
    int unreadNotificationsCount;
    List<String> roles;
    Status zonkyCommanderStatus = Status.ACTIVE;
    double zonkyCommanderBalance;
    int maximumInvestmentAmount; //patri to sem, ale Zonky to ma v samostatnem requestu

    public int getMaximumInvestmentAmount() {
        return maximumInvestmentAmount;
    }

    public void setMaximumInvestmentAmount(int maximumInvestmentAmount) {
        this.maximumInvestmentAmount = maximumInvestmentAmount;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public double getZonkyCommanderBalance() {
        return zonkyCommanderBalance;
    }

    public void setZonkyCommanderBalance(double zonkyCommanderBalance) {
        this.zonkyCommanderBalance = zonkyCommanderBalance;
    }

    public PermanentAddress getPermanentAddress() {
        return permanentAddress;
    }

    public void setPermanentAddress(PermanentAddress permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Status getZonkyCommanderStatus() {
        return zonkyCommanderStatus;
    }

    /**
     * @deprecated Volat pres ZonkySniperApplication.getInstance().setZonkyCommanderStatus()
     * @param zonkyCommanderStatus
     */
    @Deprecated
    public void setZonkyCommanderStatus(Status zonkyCommanderStatus) {

        this.zonkyCommanderStatus = zonkyCommanderStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getUnreadNotificationsCount() {
        return unreadNotificationsCount;
    }

    public void setUnreadNotificationsCount(int unreadNotificationsCount) {
        this.unreadNotificationsCount = unreadNotificationsCount;
    }

    public enum Status {
        ACTIVE,
        PASSIVE,
        DEBTOR,
        BLOCKED,
        SUBSCRIBER
    }
}
