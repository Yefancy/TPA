package main.yefancy.lfz.datacube.PrivacyCube;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Excel {
    public List<header> headers;
    public List<Map<String, Object>> desserts;
    public Map<Integer, Map<String, Object>> modified = new HashMap<>();
    public Set<Integer> removed = new HashSet<>();

    public static class header {
        public String name;
        public Type type;
    }

    public enum Type {
        NUMBER,
        CATEGORICAL,
        TEMPORAL,
        SPATIAL,
    }
}



