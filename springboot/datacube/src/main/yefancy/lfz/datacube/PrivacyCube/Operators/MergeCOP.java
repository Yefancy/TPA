package main.yefancy.lfz.datacube.PrivacyCube.Operators;

import main.yefancy.lfz.datacube.PrivacyCube.PNode;
import main.yefancy.lfz.datacube.PrivacyCube.PrivacyCube;
import main.yefancy.lfz.datacube.PrivacyCube.TableCuboid;
import main.yefancy.lfz.datacube.PrivacyCube.TabularDataPoint;
import main.yefancy.lfz.datacube.api.tree.IContent;
import main.yefancy.lfz.datacube.api.utils.tuples.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MergeCOP implements ICubeOperator{
    public Map<Integer, Set<Integer>> filters;
    public int dim;
    public Set<Integer> label1;
    public Set<Integer> label2;

    @Override
    public boolean execute(PrivacyCube cube) {
        List<TabularDataPoint> oldData = new ArrayList<>();
        oldData.addAll(findOldData(cube.root, label1));
        oldData.addAll(findOldData(cube.root, label2));
        Set<Integer> newLabel = Stream.concat(label1.stream(), label2.stream()).collect(Collectors.toSet());
        StringBuilder value = new StringBuilder();
        Tuple2<String, List<Object>> header = cube.schemaMap.get(dim);
        for (Integer aInteger : newLabel) {
            value.append(header.y.get(Math.toIntExact(aInteger))).append(" ");
        }
        for (TabularDataPoint nd : genNewData(newLabel, header.x, value.toString(), oldData)) {
            cube.insert(nd);
        }
        return true;
    }

    private List<TabularDataPoint> genNewData(Set<Integer> newLabel, String key, Object value, List<TabularDataPoint> oldData){
        List<TabularDataPoint> newData = new ArrayList<>();
        for (TabularDataPoint od : oldData) {
            TabularDataPoint nd = od.copy();
            od.i = -1;
            nd.labels.get(dim).set(0, newLabel);
            nd.data.put(key, value);
            newData.add(nd);
        }
        return newData;
    }

    private List<TabularDataPoint> findOldData(PNode root, Set<Integer> label) {
        int dim = 0;
        while (true) {
            if(dim == this.dim) {
                root = root.getChild(label);
            }
            if(filters.containsKey(dim)) {
                root = root.getChild(filters.get(dim));
            }
            IContent content = root.getContent();
            if(content instanceof PNode) {
                root = (PNode) content;
                dim++;
            } else {
                return ((TableCuboid)content).d;
            }
        }
    }
}
