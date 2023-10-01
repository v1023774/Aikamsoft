package Aikamsoft;

import database.Database;
import database.Models.Customer;

import java.util.List;

public class App
{
    public static void main( String[] args )
    {
        Database database = new Database();
        List<Object[]>  results = database.getBadCustomers(10);
        for(Object[] c : results){
            System.out.println(c[0]);
            System.out.println(c[1]);
    }
    }
}
