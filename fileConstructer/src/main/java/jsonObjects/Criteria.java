package jsonObjects;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Criteria implements JsonObject{
    public JsonElement criteria;
    public String message;

    public List<JsonObject> results = new ArrayList<>();

    public Criteria(@NotNull final  JsonElement criteria) {
        this.criteria = criteria;
    }
    public Criteria(@NotNull final  JsonElement criteria, String message) {
        this.criteria = criteria;
        this.message = message;
        this.results = null;
    }

    public void addJsonToResultList(JsonObject jsonObject) {
        this.results.add(jsonObject);
    }
}
