package data;

public class Manager extends Person{

    private float revenues;

    public Manager(String email, String name) {
        super(email, name);
    }

    public Manager(String email, String name, float revenues) {
        super(email, name);
        this.revenues = revenues;
    }

    public float getRevenues() {
        return revenues;
    }

    public void setRevenues(float revenues) {
        this.revenues = revenues;
    }
}
