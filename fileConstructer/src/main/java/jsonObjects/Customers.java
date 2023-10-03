package jsonObjects;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Customers implements JsonObjects {
    private String name;
    private List<Purchases> purchases = new ArrayList();

    private Long totalExpenses;

    public void setTotalExpenses(Long totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public Customers(@NotNull final String name) {
        this.name = name;
    }

    public void addJsonToResultList(Purchases purchases) {
        this.purchases.add(purchases);
    }
}
