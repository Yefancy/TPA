package main.yefancy.lfz.datacube.PrivacyCube;

import main.yefancy.lfz.datacube.api.utils.tuples.Tuple2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TabularDataPoint {
    public transient final Map<String, Object> data;
    public transient final List<List<Set<Integer>>> labels;
    public int i;

    private TabularDataPoint(Map<String, Object> data, List<List<Set<Integer>>> labels, int i) {
        this.data = data;
        this.labels = labels;
        this.i = i;
    }


    public TabularDataPoint(List<Excel.header> headers, Map<String, Object> data, List<Tuple2<String, List<Object>>> schemaMap, int index) {
        this.i = index;
        this.data = data;
        this.labels = new ArrayList<>();
        for (int i = 0; i < headers.size(); i++) {
            Excel.header header = headers.get(i);
            List<Set<Integer>> chain = new ArrayList<>();
            if(schemaMap.size() <= i) {
                schemaMap.add(new Tuple2<>(header.name, new ArrayList<>()));
            }
            List<Object> schema = schemaMap.get(i).y;
            Object item = data.get(header.name);
            if(header.type == Excel.Type.CATEGORICAL) {
                if (item != null) {
                    if (item instanceof List) {
                        Set<Integer> labels = new HashSet<>();
                        ((List<?>) item).forEach(_item->{
                            int label = schema.indexOf(_item.toString());
                            if(label == -1) {
                                schema.add(item.toString());
                                label = schema.size() - 1;
                            }
                            labels.add(label);
                        });
                        chain.add(labels);
                    } else {
                        int label = schema.indexOf(item.toString());
                        if(label == -1) {
                            schema.add(item.toString());
                            label = schema.size() - 1;
                        }
                        chain.add(Set.of(label));
                    }
                }
            } else if(header.type == Excel.Type.NUMBER) {
                if (item instanceof Number) {
                    int label = 0;
                    float value = ((Number) item).floatValue();
                    if (schema.size() < 1) {
                        schema.add(new NumberStock(value));
                    } else {
                        ((NumberStock) schema.get(0)).update(value);
                    }
                    chain.add(Set.of(label));
                } else if (item instanceof String) {
                    int label = -1;
                    String[] values = ((String) item).split("-");
                    if (values.length == 2) {
                        try {
                            float left = Float.parseFloat(values[0]);
                            float right = Float.parseFloat(values[1]);
                            NumberStock stock = new NumberStock(Math.min(left, right), Math.max(left, right));
                            label = schema.indexOf(stock);
                            if (label < 1) {
                                schema.add(stock);
                                label = schema.size() - 1;
                            }
                        } catch (Exception ignored) {}
                    }
                    chain.add(Set.of(label));
                }
            }
            labels.add(chain);
        }
    }

    public List<List<Set<Integer>>> getLabels() {
        return labels;
    }

    public TabularDataPoint copy() {
        Map<String, Object> data = new LinkedHashMap<>(this.data);
        return new TabularDataPoint(data, List.copyOf(labels), i);
    }

    public Set<Integer> getLabel(int dimension, int chainDim) {
        return getLabels().get(dimension).get(chainDim);
    }

    public int getDimension() {
        return getLabels().size();
    }

    public static class NumberStock{
        public float min;
        public float max;

        public NumberStock(float value){
            this.min = value;
            this.max = value;
        }

        public NumberStock(float min, float max){
            this.min = min;
            this.max = max;
        }

        public void update(float value) {
            this.min = Math.min(value, this.min);
            this.max = Math.max(value, this.max);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof NumberStock numberStock) {
                return numberStock.max == this.max && numberStock.min == this.min;
            }
            return false;
        }
    }
}
