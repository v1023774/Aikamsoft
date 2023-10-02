package database.Models;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.Objects;

/**
 * Класс сущности товара
 */
@Entity
@Table(name = "Product")
public class Product {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "name")
    public String name;

    @Column(name = "price")
    public int price;

    public Product() {
    }

    public Product(@NotNull final String name, final int price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return price == product.price && name.equals(product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}