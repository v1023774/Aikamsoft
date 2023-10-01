package database;

import com.sun.istack.NotNull;
import database.Models.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;


public class Database {
    private final SessionFactory sessionFactory;

    public Database() {
        Configuration configuration = new Configuration().addAnnotatedClass(Customer.class)
                .addAnnotatedClass(Product.class)
                .addAnnotatedClass(Purchases.class);
        sessionFactory = configuration.buildSessionFactory();
    }

    /**
     * Выбирает всех покупателей с указанным именем
     *
     * @param firstName - имя покупателя.
     */
    public List<Customer> getCustomersListByFirstName(@NotNull final String firstName) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        final List<Customer> customers = session.createQuery("FROM Customer where firstName = :firstName").
                setParameter("firstName", firstName).getResultList();
        session.getTransaction().commit();
        return customers;
    }

    /**
     * Поиск покупателей, купивших товар не менее, чем указанное число раз
     *
     * @param productName - название товара.
     * @param minTimes    - имя покупателя.
     */
    public List<Object[]> getCustomersByProduct(@NotNull final String productName, @NotNull final Long minTimes) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        Query query = session.createQuery("select c.lastName, c.firstName\n" +
                        "from Customer as c \n" +
                        "         join Purchases as pu on c.id = pu.customer_id\n" +
                        "         join Product as p on p.id = pu.product_id\n" +
                        "where p.name = :productName\n" +
                        "group by pu.customer_id, c.lastName,  c.firstName\n" +
                        "having count(pu.product_id) >= :minTimes").
                setParameter("productName", productName).setParameter("minTimes", minTimes);

        List<Object[]> customers = (List<Object[]>) query.list();
        session.getTransaction().commit();
        return customers;
    }

    /**
     * Поиск покупателей, у которых общая стоимость всех покупок за всё время попадает в интервал
     *
     * @param min - Нижний интервал
     * @param max - Верхний интервал.
     */
    public List<Object[]> getCustomersByInterval(@NotNull final Long min, @NotNull final Long max) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        Query query = session.createQuery("select  c.lastName, c.firstName\n" +
                        "from Customer as c\n" +
                        "         join Purchases as pu on c.id = pu.customer_id\n" +
                        "         join Product as p on p.id = pu.product_id\n" +
                        "group by c.lastName, c.firstName\n" +
                        "having sum(p.price) >= :min and sum(p.price) <= :max").
                setParameter("min", min).setParameter("max", max);

        List<Object[]> customers = (List<Object[]>) query.list();
        session.getTransaction().commit();
        return customers;
    }

    /**
     * Поиск покупателей, купивших меньше всего товаров. Возвращается не более, чем указанное число покупателей.
     *
     * @param quantity - количество пользователей
     */
    public List<Object[]> getBadCustomers(@NotNull final int quantity) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        Query query = session.createQuery("select c.firstName, c.lastName\n" +
                        "from Customer as c\n" +
                        "left outer join Purchases pu on c.id = pu.customer_id\n" +
                        "group by c.firstName, c.lastName\n" +
                        "having count(pu.product_id) >= 0\n" +
                        "order by count(pu.product_id)\n" +
                        "asc").setMaxResults(quantity);

        List<Object[]> customers = (List<Object[]>) query.list();
        session.getTransaction().commit();
        return customers;
    }
}
