package jsonObjects;

import java.util.ArrayList;
import java.util.List;

public class StatJson implements JsonObjects {
    private String type;

    private Long totalDays;
    private List<Customers> customers = new ArrayList<>();
    private Long totalExpenses = 0L;
    private Double avgExpenses = 0D;

    public StatJson(String type, Long totalDays) {
        this.type = type;
        this.totalDays = totalDays;
    }

    public void addJsonToResultList(Customers customer) {
        this.customers.add(customer);
    }

    public void setTotalExpenses(Long totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public void setAvgExpenses(Double avgExpenses) {
        this.avgExpenses = avgExpenses;
    }

    public Long getTotalExpenses() {
        return totalExpenses;
    }

    public Double getAvgExpenses() {
        return avgExpenses;
    }
}
