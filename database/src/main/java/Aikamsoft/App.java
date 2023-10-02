package Aikamsoft;

import database.Database;
import database.Models.Customer;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;


public class App {

    public static void main(String[] args) {

        String str_date1 = "2000-01-01";
        String str_date2 = "2005-01-26";
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = formatter.parse(str_date1);
            Date date2 = formatter.parse(str_date2);


        Database database = new Database();

        List<Object[]> results = database.getCustomersOrderedListByValue(date1,date2);
        for (Object[] c : results) {
            System.out.println(c[0]);
            System.out.println(c[1]);
            System.out.println(c[2]);
        }
            System.out.println(database.getNumberOfWorkingDays(LocalDate.of(2020, 1, 14), LocalDate.of(2020, 1, 26)));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }
}
