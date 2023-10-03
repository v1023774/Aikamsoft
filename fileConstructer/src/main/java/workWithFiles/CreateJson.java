package workWithFiles;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import database.Database;
import database.Models.Customer;
import jsonObjects.*;
import jsonObjects.Error;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Класс для работы с json файлами
 */
public class CreateJson {
    private CreateJson(){}
    private static final Database database = new Database();

    /**
     * Метод конструирует json по параметру и сохраняет его в файл output.json
     *
     * @param parameter - параметр выборки(stat, search)
     * @param input     - путь до файла input.json
     * @param output    - путь до файла output.json
     */
    public static void createOutputJson(@NotNull final String parameter, @NotNull final String input, @NotNull final String output) {

        if (parameter.equals("search")) {
            searchWriter(input, output);
        } else if (parameter.equals("stat")) {
            statWriter(input, output);
        } else {
            wrongParameterConstructor(output);
        }
    }

    private static void wrongParameterConstructor(@NotNull final String output) {
        try (final FileWriter writer = new FileWriter(output)) {
            final Error error = new Error("Error", "Unknown parameter");
            final Gson gson = new Gson();
            writer.write(gson.toJson(error));
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private static void statWriter(@NotNull final String input, @NotNull final String output) {
        try (final FileWriter writer = new FileWriter(output)) {
            final JsonObjects statJson = statConstructor(input);
            final Gson gson = new Gson();
            writer.write(gson.toJson(statJson));
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private static JsonObjects statConstructor(@NotNull final String input) {
        StatJson statJson = null;
        try (final FileReader reader = new FileReader(input)) {
            final JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
            if (jsonObject.has("startDate") && jsonObject.has("endDate") &&
                    jsonObject.getAsJsonObject().keySet().size() == 2) {
                statJson = createStatjson(jsonObject);
            } else {
                return new Error("Error", "Wrong stat parameters");
            }
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return statJson;
    }

    @NotNull
    private static StatJson createStatjson(@NotNull final JsonObject jsonObject) throws ParseException {
        StatJson statJson;
        final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        final Date startDate = formatter.parse(jsonObject.get("startDate").getAsString());
        final Date endDate = formatter.parse(jsonObject.get("endDate").getAsString());
        final long totalDays = Database.getNumberOfWorkingDays(startDate, endDate);

        statJson = new StatJson("stat", totalDays);
        final List<Object[]> customersList = database.getCustomersOrderedListByValue(startDate, endDate);
        long totalExpensesCustomer = 0L;
        long totalExpenses = 0L;
        for (int i = 0; i < customersList.size(); i++) {
            final Customers customer = new Customers(customersList.get(i)[0].toString() + " " + customersList.get(i)[1].toString());
            for (Object[] product : database.getProducts(startDate, endDate, customersList.get(i)[1].toString(), customersList.get(i)[0].toString())) {
                final Purchases purchases = new Purchases(product[0].toString(), Integer.parseInt(product[1].toString()));
                customer.addJsonToResultList(purchases);
                totalExpensesCustomer += Long.parseLong(product[1].toString());
            }
            customer.setTotalExpenses(totalExpensesCustomer);
            totalExpenses += totalExpensesCustomer;
            statJson.addJsonToResultList(customer);
            totalExpensesCustomer = 0L;
        }
        statJson.setTotalExpenses(totalExpenses);
        if (customersList.isEmpty()) {
            statJson.setAvgExpenses(0D);
        } else {
            statJson.setAvgExpenses(Double.valueOf(statJson.getTotalExpenses()) / customersList.size());
        }
        return statJson;
    }

    private static void searchWriter(@NotNull final String input, @NotNull final String output) {
        try (final FileWriter writer = new FileWriter(output)) {
            final JsonObjects searchJson = searchConstructor(input);

            final Gson gson = new Gson();
            writer.write(gson.toJson(searchJson));
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private static JsonObjects searchConstructor(@NotNull final String input) {
        SearchJson searchJson = null;
        try (final FileReader reader = new FileReader(input)) {
            final JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
            if (jsonObject.has("criterias")) {
                searchJson = new SearchJson("search");
                final JsonArray jsonIn = jsonObject.getAsJsonArray("criterias");
                for (int i = 0; i < jsonIn.size(); i++) {
                    final Criteria criteria = new Criteria(jsonIn.get(i));
                    if (jsonIn.get(i).getAsJsonObject().has("lastName") && jsonIn.get(i).getAsJsonObject().keySet().size() == 1) {
                        createLastNameCriteriajson(searchJson, jsonIn, i, criteria);
                    } else if ((jsonIn.get(i).getAsJsonObject().has("productName")) && (jsonIn.get(i).getAsJsonObject().has("minTimes"))
                            && jsonIn.get(i).getAsJsonObject().keySet().size() == 2) {
                        createProductNameCriteriajson(searchJson, jsonIn, i, criteria);
                    } else if ((jsonIn.get(i).getAsJsonObject().has("minExpenses")) && (jsonIn.get(i).getAsJsonObject().has("maxExpenses"))
                            && jsonIn.get(i).getAsJsonObject().keySet().size() == 2) {
                        createMinExpensesCriteriajson(searchJson, jsonIn, i, criteria);
                    } else if ((jsonIn.get(i).getAsJsonObject().has("badCustomers")) && jsonIn.get(i).getAsJsonObject().keySet().size() == 1) {
                        createBadCustomersCriteriajson(searchJson, jsonIn, i, criteria);
                    } else {
                        return new Error("Error", "Unknown criteria name");
                    }
                }
            } else {
                return new Error("Error", "Wrong search parameter");
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return searchJson;
    }

    private static void createBadCustomersCriteriajson(@NotNull final SearchJson searchJson, @NotNull final JsonArray jsonIn,
                                                       final int i, @NotNull final Criteria criteria) {
        final int quantity = (jsonIn.get(i).getAsJsonObject().get("badCustomers").getAsInt());

        for (Object[] j : database.getBadCustomers(quantity)) {
            criteria.addJsonToResultList(new LastName((String) j[0], (String) j[1]));
        }
        searchJson.addJsonToResultList(criteria);
    }

    private static void createMinExpensesCriteriajson(@NotNull final SearchJson searchJson, @NotNull final JsonArray jsonIn,
                                                      final int i, @NotNull final Criteria criteria) {
        final Long minExpenses = (jsonIn.get(i).getAsJsonObject().get("minExpenses").getAsLong());
        final Long maxExpenses = (jsonIn.get(i).getAsJsonObject().get("maxExpenses").getAsLong());

        for (Object[] j : database.getCustomersByInterval(minExpenses, maxExpenses)) {
            criteria.addJsonToResultList(new LastName((String) j[0], (String) j[1]));
        }
        searchJson.addJsonToResultList(criteria);
    }

    private static void createProductNameCriteriajson(@NotNull final SearchJson searchJson, @NotNull final JsonArray jsonIn,
                                                      final int i, @NotNull final Criteria criteria) {
        final String productName = (jsonIn.get(i).getAsJsonObject().get("productName").getAsString());
        final Long quantity = (jsonIn.get(i).getAsJsonObject().get("minTimes").getAsLong());

        for (Object[] j : database.getCustomersByProduct(productName, quantity)) {
            criteria.addJsonToResultList(new LastName((String) j[0], (String) j[1]));
        }
        searchJson.addJsonToResultList(criteria);
    }

    private static void createLastNameCriteriajson(@NotNull final SearchJson searchJson, @NotNull final JsonArray jsonIn,
                                                   final int i, @NotNull final Criteria criteria) {
        final String lastName = jsonIn.get(i).getAsJsonObject().get("lastName").getAsString();
        for (Customer j : database.getCustomersListByFirstName(lastName)) {
            criteria.addJsonToResultList(new LastName(j.lastName, j.firstName));
        }
        searchJson.addJsonToResultList(criteria);
    }
}
