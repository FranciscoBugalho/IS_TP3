package data;

public class Client extends Person {

    private String manager_person_email;
    private float payments;
    private float credits;
    private float balance;

    public Client() { }

    public Client(String email, String name, String manager_person_email) {
        super(email, name);
        this.manager_person_email = manager_person_email;
    }

    public Client(String email, String name, String manager_person_email, float payments, float credits, float balance) {
        super(email, name);
        this.manager_person_email = manager_person_email;
        this.payments = payments;
        this.credits = credits;
        this.balance = balance;
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

    @Override
    public String toString() {
        return "Client{" +
                "person_email='" + super.getEmail() + '\'' +
                "manager_person_email='" + manager_person_email + '\'' +
                '}';
    }
}
