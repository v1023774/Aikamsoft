package jsonObjects;

public class LastName implements JsonObject {

    public String lastName;
    public String firstName;

    public LastName(String lastName, String firstName) {
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public LastName(String lastName) {
        this.lastName = lastName;
    }
}
