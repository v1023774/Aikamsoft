package jsonObjects;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SearchJson implements JsonObjects {
    private String type;

    private List<Criteria> results = new ArrayList<>();

    public SearchJson(@NotNull final String type) {
        this.type = type;
    }

    public void addJsonToResultList(Criteria criteria) {
        this.results.add(criteria);
    }
}
