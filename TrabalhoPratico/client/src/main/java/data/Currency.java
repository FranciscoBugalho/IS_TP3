package data;

public class Currency {

    private String name;
    private float to_euro;

    public Currency(String name, float to_euro) {
        this.name = name;
        this.to_euro = to_euro;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getTo_euro() {
        return to_euro;
    }

    public void setTo_euro(float to_euro) {
        this.to_euro = to_euro;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "name='" + name + '\'' +
                ", to_euro=" + to_euro +
                '}';
    }
}
