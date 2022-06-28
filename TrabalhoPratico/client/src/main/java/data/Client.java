package data;

public class Client extends Person{

    private String manager_person_email;
    private float payments;
    private float credits;
    private float balance;
    private float bill;
    private float payed;
    private boolean payments_last_months;

    public Client(String email, String name, String manager_person_email) {
        super(email, name);
        this.manager_person_email = manager_person_email;
    }

    public Client(String email, String name, String manager_person_email, float payments, float credits, float balance, float bill, float payed, boolean payments_last_months) {
        super(email, name);
        this.manager_person_email = manager_person_email;
        this.payments = payments;
        this.credits = credits;
        this.balance = balance;
        this.bill = bill;
        this.payed = payed;
        this.payments_last_months = payments_last_months;
    }

    public float getPayments() {
        return payments;
    }

    public void setPayments(float payments) {
        this.payments = payments;
    }

    public float getCredits() {
        return credits;
    }

    public void setCredits(float credits) {
        this.credits = credits;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getManager_person_email() {
        return manager_person_email;
    }

    public void setManager_person_email(String manager_person_email) {
        this.manager_person_email = manager_person_email;
    }

    public float getBill() {
        return bill;
    }

    public void setBill(float bill) {
        this.bill = bill;
    }

    public float getPayed() {
        return payed;
    }

    public void setPayed(float payed) {
        this.payed = payed;
    }

    public boolean isPayments_last_months() {
        return payments_last_months;
    }

    public void setPayments_last_months(boolean payments_last_months) {
        this.payments_last_months = payments_last_months;
    }

    @Override
    public String toString() {
        return "Client{" +
                "person_email='" + super.getEmail() + '\'' +
                "manager_person_email='" + manager_person_email + '\'' +
                '}';
    }
}
