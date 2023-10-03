package jsonObjects;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Criteria implements JsonObjects {
    private JsonElement criteria;

    private List<JsonObjects> results = new ArrayList<>();

    public Criteria(@NotNull final  JsonElement criteria) {
        this.criteria = criteria;
    }

    public void addJsonToResultList(JsonObjects jsonObject) {
        this.results.add(jsonObject);
    }
}
