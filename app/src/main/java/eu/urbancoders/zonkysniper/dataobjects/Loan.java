package eu.urbancoders.zonkysniper.dataobjects;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.05.2016
 */
public class Loan {

    /**
     * "id":31156,
     * "name":"Refinancování půjček",
     * "story":"Jedná se o refinancování drahé půjčky. Byli jsme v situaci, kdy jsme pro synka potřebovali rychle vybudovat vlastní pokoj v podkroví (je vážně nemocný). Nyní chceme touto refinancování snížit náklady na půjčku.",
     * "photos":[{
     * "name":"6",
     * "url":"/loans/31156/photos/1884"
     * }],
     * "nickName":"gnom",
     * "termInMonths":84,
     * "interestRate":0.049900,
     * "rating":{
     * "type":"AAAA"
     * },
     * "topped":null,
     * "amount":270000.00,
     * "remainingInvestment":241400.00,
     * "covered":false,
     * "datePublished":"2016-05-13T21:29:59.365+02:00",
     * "published":true,
     * "deadline":"2016-05-20T21:27:44.015+02:00",
     * "investmentsCount":39,
     * "questionsCount":3
     * }
     */

    int id;
    String name;
    String story;
    String nickName;
    int termInMonths;
    double interestRate;
    String topped;
    double amount;
    double remainingInvestment;
    boolean covered;
    Date datePublished;
    boolean published;
    Date deadline;
    int investmentsCount;
    int questionsCount;
    String rating;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getTermInMonths() {
        return termInMonths;
    }

    public void setTermInMonths(int termInMonths) {
        this.termInMonths = termInMonths;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public String getTopped() {
        return topped;
    }

    public void setTopped(String topped) {
        this.topped = topped;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getRemainingInvestment() {
        return remainingInvestment;
    }

    public void setRemainingInvestment(double remainingInvestment) {
        this.remainingInvestment = remainingInvestment;
    }

    public boolean isCovered() {
        return covered;
    }

    public void setCovered(boolean covered) {
        this.covered = covered;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(Date datePublished) {
        this.datePublished = datePublished;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public int getInvestmentsCount() {
        return investmentsCount;
    }

    public void setInvestmentsCount(int investmentsCount) {
        this.investmentsCount = investmentsCount;
    }

    public int getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount(int questionsCount) {
        this.questionsCount = questionsCount;
    }
}
