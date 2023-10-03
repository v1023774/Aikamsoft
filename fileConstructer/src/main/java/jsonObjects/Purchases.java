package jsonObjects;

public class Purchases implements JsonObjects {

    private String name;

    private int expenses;

    public Purchases(String name, int expenses) {
        this.name = name;
        this.expenses = expenses;
    }
}
