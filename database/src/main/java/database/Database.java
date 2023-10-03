package database;

import com.sun.istack.NotNull;
import database.Models.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.time.ZoneId;
import java.util.Date;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
     * @param lastName - фамилия покупателя.
     */
    public List<Customer> getCustomersListByFirstName(@NotNull final String lastName) {
        final Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        final List<Customer> customers = session.createQuery("FROM Customer where lastName = :lastName").
                setParameter("lastName", lastName).getResultList();
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
        final Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        final Query query = session.createQuery("select c.lastName, c.firstName\n" +
                        "from Customer as c \n" +
                        "         join Purchases as pu on c.id = pu.customer_id\n" +
                        "         join Product as p on p.id = pu.product_id\n" +
                        "where p.name = :productName\n" +
                        "group by pu.customer_id, c.lastName,  c.firstName\n" +
                        "having count(pu.product_id) >= :minTimes").
                setParameter("productName", productName).setParameter("minTimes", minTimes);

        final List<Object[]> customers = (List<Object[]>) query.list();
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
        final Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        final Query query = session.createQuery("select  c.lastName, c.firstName\n" +
                        "from Customer as c\n" +
                        "         join Purchases as pu on c.id = pu.customer_id\n" +
                        "         join Product as p on p.id = pu.product_id\n" +
                        "group by c.lastName, c.firstName\n" +
                        "having sum(p.price) >= :min and sum(p.price) <= :max").
                setParameter("min", min).setParameter("max", max);

        final List<Object[]> customers = (List<Object[]>) query.list();
        session.getTransaction().commit();
        return customers;
    }

    /**
     * Поиск покупателей, купивших меньше всего товаров. Возвращается не более, чем указанное число покупателей.
     *
     * @param quantity - количество пользователей
     */
    public List<Object[]> getBadCustomers(final int quantity) {
        final Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        final Query query = session.createQuery("select c.firstName, c.lastName\n" +
                "from Customer as c\n" +
                "left outer join Purchases pu on c.id = pu.customer_id\n" +
                "group by c.firstName, c.lastName\n" +
                "having count(pu.product_id) >= 0\n" +
                "order by count(pu.product_id)\n" +
                "asc").setMaxResults(quantity);

        final List<Object[]> customers = (List<Object[]>) query.list();
        session.getTransaction().commit();
        return customers;
    }

    /**
     * Данные по покупателям за этот период, упорядоченные по общей стоимости покупок по убыванию.
     *
     * @param dateStart - начальная дата
     * @param dateEnd   - конечная дата
     *                  return массивы объектов с фамилией именем и общей стоимостью покупок
     */
    public List<Object[]> getCustomersOrderedListByValue(@NotNull final Date dateStart, @NotNull final Date dateEnd) {
        final Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        Query query = session.createQuery("select  c.lastName, c.firstName, sum(p.price) as summ\n" +
                "from Customer as c\n" +
                "         join Purchases pu on c.id = pu.customer_id\n" +
                "         join Product p on p.id = pu.product_id\n" +
                "where pu.date > :dateStart and pu.date < :dateEnd\n" +
                "group by c.lastName, c.firstName\n" +
                "order by summ\n" +
                "desc").setParameter("dateStart", convert(dateStart)).setParameter("dateEnd", convert(dateEnd));
        final List<Object[]> customers = (List<Object[]>) query.list();
        session.getTransaction().commit();
        return customers;
    }

    /**
     * Общее число дней за период из двух дат, включительно, без выходных
     *
     * @param startDate - начальная дата
     * @param endDate   - конечная дата
     */
    public static long getNumberOfWorkingDays(@NotNull final Date startDate, @NotNull final Date endDate) {

        LocalDate startLocalDate = convertDateToLocalDate(startDate);
        LocalDate endLocalDat =  convertDateToLocalDate(endDate);
        final long totalDays = ChronoUnit.DAYS.between(startLocalDate, endLocalDat) + 1;
        long weekends = 0;

        for (LocalDate date = startLocalDate; !date.isAfter(endLocalDat); date = date.plusDays(1)) {
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                weekends++;
            }
        }

        return totalDays - weekends;
    }

    /**
     * Метод конвертирует дату из java.util.Date в java.sql.Date
     *
     * @param date - дата типа java.util.Date
     */
    private static java.sql.Date convert(java.util.Date date) {
        return new java.sql.Date(date.getTime());
    }

    /**
     * Метод конвертирует дату из Date в LocalDate
     *
     * @param date - дата типа java.util.Date
     */
    private static LocalDate convertDateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }


    /**
     * Данные по покупателям за этот период, упорядоченные по общей стоимости покупок по убыванию.
     *
     * @param dateStart - начальная дата
     * @param dateEnd   - конечная дата
     *                  return массивы объектов с фамилией именем и общей стоимостью покупок
     */
    public List<Object[]> getProducts(@NotNull final Date dateStart, @NotNull final Date dateEnd,
                                      @NotNull final String firstName, @NotNull final String lastName) {
        final Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        final Query query = session.createQuery("select p.name, sum(p.price)\n" +
                        "from Product as p\n" +
                        "         join Purchases pu on p.id = pu.product_id\n" +
                        "         join Customer c on pu.customer_id = c.id\n" +
                        "where pu.date > :dateStart\n" +
                        "  and pu.date < :dateEnd\n" +
                        "  and c.lastName = :lastName\n" +
                        "  and c.firstName = :firstName\n" +
                        "group by 1\n" +
                        "order by 2\n" +
                        "        desc").setParameter("dateStart", convert(dateStart)).setParameter("dateEnd", convert(dateEnd))
                .setParameter("firstName", firstName).setParameter("lastName", lastName);
        final List<Object[]> customers = (List<Object[]>) query.list();
        session.getTransaction().commit();
        return customers;
    }
}
