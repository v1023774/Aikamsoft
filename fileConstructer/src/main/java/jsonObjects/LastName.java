package jsonObjects;

public class LastName implements JsonObjects {

    private final String lastName;
    private String firstName;

    public LastName(String lastName, String firstName) {
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public LastName(String lastName) {
        this.lastName = lastName;
    }
}
