package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.05.2016
 */
public class Loan implements Serializable {

    /**
     {
         "id":43449,
         "name":"Auto místo moto",
         "story":"Edit 1: Děkuji všem... u.",
         "purpose":"1",
         "photos":[
             {
             "name":"file.png",
             "url":"/loans/43449/photos/3346"
             }
         ],
         "userId":57699,
         "nickName":"MirekPraha",
         "termInMonths":54,
         "interestRate":0.049900,
         "rating":"AAAA",
         "topped":null,
         "amount":230000.00,
         "remainingInvestment":53400.00,
         "investmentRate":0.7678260869565218,
         "covered":false,
         "datePublished":"2016-08-18T14:39:12.253+02:00",
         "published":true,
         "deadline":"2016-08-25T14:29:55.104+02:00",
         "investmentsCount":281,
         "questionsCount":0,
         "region":"6",
         "mainIncomeType":"EMPLOYMENT"
     }
     */

    int id;
    String name;
    String story;
    String purpose;
    List<Photo> photos;
    int userId;
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
    MyInvestment myInvestment;
    String mainIncomeType;
    int region;
    String url;

    boolean insuranceActive;

    public boolean isInsuranceActive() {
        return insuranceActive;
    }

    public void setInsuranceActive(boolean insuranceActive) {
        this.insuranceActive = insuranceActive;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public String getMainIncomeType() {
        return mainIncomeType;
    }

    public void setMainIncomeType(String mainIncomeType) {
        this.mainIncomeType = mainIncomeType;
    }

    public int getUserId() {
        return userId;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public MyInvestment getMyInvestment() {
        return myInvestment;
    }

    public void setMyInvestment(MyInvestment myInvestment) {
        this.myInvestment = myInvestment;
    }

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
