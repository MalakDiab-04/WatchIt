package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Subscriptions
{
    private  boolean status;
    private  String  plan;
    private  LocalDate subscribeDate;

    public Subscriptions(boolean status, String plan, LocalDate subscribeDate) {
        this.status = status;
        this.plan = plan;
        this.subscribeDate = subscribeDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public LocalDate getSubscribeDate() {
        return subscribeDate;
    }

    public void setSubscribeDate(LocalDate subscribeDate) {
        this.subscribeDate = subscribeDate;
    }
    @Override
    public String toString() {

        if(!status)
        {
            return "false;" + " ;" + " ";
        }
        else
        {
            String subscriptionDate = subscribeDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return "true;"+plan+";"+subscriptionDate;
        }

    }

    public static boolean dueDate(LocalDate subscribeDate) {
        long diff = ChronoUnit.DAYS.between(subscribeDate, LocalDate.now());
        if (diff > 30)
        {
            return true;
        }
        else
            return false;
}



}
