package ua.djhans.model;

/**
 * Created by Administrator on 23.01.2016.
 */
public class Contact {
    private long id;
    private String name;

    public Contact() {
    }

    public Contact(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{" + id + ", " + name + '}';
    }
}
