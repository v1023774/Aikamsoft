package jsonObjects;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SearchJson implements JsonObject {
    public String type;
    public String message;

    public List<Criteria> results = new ArrayList<>();

    public SearchJson(@NotNull final String type, @NotNull final String message) {
        this.type = type;
        this.message = message;
    }

    public SearchJson(@NotNull final String type) {
        this.type = type;
    }

    public void addJsonToResultList(Criteria criteria) {
        this.results.add(criteria);
    }
}
