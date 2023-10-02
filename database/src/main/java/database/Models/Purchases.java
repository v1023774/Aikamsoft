package database.Models;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;
/**
 * Класс сущности покупки
 */
@Entity
@Table(name = "Purchases")
public class Purchases {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "customer_id")
    public int customer_id;

    @Column(name = "product_id")
    public int product_id;

    @Column(name = "date")
    public Date date;

    public Purchases() {
    }

    public Purchases(final int customer_id, final int product_id) {
        this.customer_id = customer_id;
        this.product_id = product_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Purchases purchases = (Purchases) o;
        return customer_id == purchases.customer_id && product_id == purchases.product_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customer_id, product_id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }
}