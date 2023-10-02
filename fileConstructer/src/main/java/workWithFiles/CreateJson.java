package workWithFiles;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import database.Database;
import database.Models.Customer;
import jsonObjects.*;
import jsonObjects.Error;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CreateJson {
    private static final Database database = new Database();

    public static void createOutputJson(String parameter, String input, String output) {
        if (parameter.equals("search")) {
            searchWriter(input, output);
        } else if (parameter.equals("stat")) {
            statWriter(input, output);
        } else {
            wrongParameterConstructor(output);
        }
    }

    private static void wrongParameterConstructor(String output) {
        try (FileWriter writer = new FileWriter(output)) {
            Error error = new Error("Error", "Unknown parameter");
            Gson gson = new Gson();
            writer.write(gson.toJson(error));
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private static void statWriter(String input, String output) {
        try (FileWriter writer = new FileWriter(output)) {
            StatJson statJson = statConstructor(input);

            Gson gson = new Gson();
            writer.write(gson.toJson(statJson));
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private static StatJson statConstructor(String input) {
        return null;
    }

    private static void searchWriter(String input, String output) {
        try (FileWriter writer = new FileWriter(output)) {
            jsonObjects.JsonObject searchJson = searchConstructor(input);

            Gson gson = new Gson();
            writer.write(gson.toJson(searchJson));
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private static jsonObjects.JsonObject searchConstructor(String input) {

        SearchJson searchJson = null;
        try (FileReader reader = new FileReader(input)) {
            Gson gson = new Gson();
            JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
            if (jsonObject.has("criterias")) {
                searchJson = new SearchJson("search");
                JsonArray jsonIn = jsonObject.getAsJsonArray("criterias");
                System.out.println(jsonIn);
                for (int i = 0; i< jsonIn.size(); i++) {
                    if (jsonIn.get(i).getAsJsonObject().has("lastName")) {
                        String lastName = jsonIn.get(i).getAsJsonObject().get("lastName").getAsString();
                        Criteria criteria = new Criteria(jsonIn.get(i));
                        for( Customer j : database.getCustomersListByFirstName(lastName)){
                            criteria.results.add(new LastName(j.lastName, j.firstName));
                        }
                        searchJson.addJsonToResultList(criteria);
                    } else if ((jsonIn.get(i).getAsJsonObject().has("productName")) && (jsonIn.get(i).getAsJsonObject().has("minTimes"))) {
                        String productName = (jsonIn.get(i).getAsJsonObject().get("productName").getAsString());
                        Long quantity = (jsonIn.get(i).getAsJsonObject().get("minTimes").getAsLong());
                        Criteria criteria = new Criteria(jsonIn.get(i));
                        for( Object[] j : database.getCustomersByProduct(productName, quantity)){
                            criteria.results.add(new LastName((String) j[0], (String) j[1]));
                        }
                        searchJson.addJsonToResultList(criteria);
                    } else if ((jsonIn.get(i).getAsJsonObject().has("minExpenses")) && (jsonIn.get(i).getAsJsonObject().has("maxExpenses"))) {
                        Long minExpenses = (jsonIn.get(i).getAsJsonObject().get("minExpenses").getAsLong());
                        Long maxExpenses = (jsonIn.get(i).getAsJsonObject().get("maxExpenses").getAsLong());

                        Criteria criteria = new Criteria(jsonIn.get(i));
                        for( Object[] j : database.getCustomersByInterval(minExpenses, maxExpenses)){
                            criteria.results.add(new LastName((String) j[0], (String) j[1]));
                        }
                        searchJson.addJsonToResultList(criteria);
                    } else if ((jsonIn.get(i).getAsJsonObject().has("badCustomers")) ){
                        int quantity = (jsonIn.get(i).getAsJsonObject().get("badCustomers").getAsInt());

                        Criteria criteria = new Criteria(jsonIn.get(i));
                        for( Object[] j : database.getBadCustomers(quantity)){
                            criteria.results.add(new LastName((String) j[0], (String) j[1]));
                        }
                        searchJson.addJsonToResultList(criteria);

                    } else {
                        Criteria criteria = new Criteria(jsonIn.get(i), "Unknown criteria name");
                        searchJson.addJsonToResultList(criteria);
                    }
                }
            } else {
                return new Error("Error","Wrong parameter");
            }

        } catch (IOException ex) {
            System.out.println(ex);
        }
        return searchJson;
    }
}
