package main.yefancy.lfz.datacube.HashedCubes;

import main.yefancy.lfz.datacube.api.ICube;
import main.yefancy.lfz.datacube.api.IDataPoint;
import main.yefancy.lfz.datacube.api.IQueryEngine;
import main.yefancy.lfz.datacube.api.tree.ICuboid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HashedCubes<T extends ICuboid> implements ICube<T> {
    public IDataPoint[] dataPoints;
    public PivotNode root;
    protected final Class<? extends T> clazz;
    int[] config;

    public HashedCubes(Class<? extends T> clazz, int[] config){
        this.clazz = clazz;
        this.config = config;
    }

    @Override
    public void insert(IDataPoint dataPoint) {

    }

    @Override
    public void insert(IDataPoint[] newArray) {
        if (this.dataPoints != null) {
            IDataPoint[] result = Arrays.copyOf(this.dataPoints, dataPoints.length + newArray.length);
            System.arraycopy(newArray, 0, result, dataPoints.length, newArray.length);
            root = dfsSortList(0, 0,0, result.length, result, new PivotNode(-1,0,result.length));
            dataPoints = result;
        } else {
            root = dfsSortList(0, 0,0, newArray.length, newArray, new PivotNode(-1,0, newArray.length));
            dataPoints = newArray;
        }
    }

    private PivotNode dfsSortList(int dimension, int chain, int start, int end, IDataPoint[] dataPoints, PivotNode root) {
        Arrays.sort(dataPoints, start, end, (a, b)-> (int) (a.getLabel(dimension, chain) - b.getLabel(dimension, chain)));
        long now = dataPoints[start].getLabel(dimension, chain);
        int s = start;
        for (int i = start + 1; i < end; i++) {
            long next = dataPoints[i].getLabel(dimension, chain);
            if (next != now) {
                drillDown(dimension, chain, i, s, dataPoints, root);
                now = next;
                s = i;
            }
        }
        drillDown(dimension, chain, end, s, dataPoints, root);
        return root;
    }

    private void drillDown(int dimension, int chain, int end, int start, IDataPoint[] dataPoints, PivotNode root) {
        if (config[dimension] > chain + 1) {
            dfsSortList(dimension, chain + 1, start, end, dataPoints, root.addChild(dataPoints[start].getLabel(dimension, chain), start, end));
        } else if(config.length > dimension + 1){
            dfsSortList(dimension + 1, 0, start, end, dataPoints, root.addChild(dataPoints[start].getLabel(dimension, chain), start, end));
        }
    }

    @Override
    public IQueryEngine getQueryEngine() {
        return null;
    }

    @Override
    public T query(List<List<Long>> query) {
        return queryDFS(getCuboidInstance(), root, 0, query);
    }

    private T queryDFS(T result, PivotNode root, int dim, List<List<Long>> query) {
        List<Long> chain = query.get(dim);
        PivotNode child = root;
        for (long chainLabel : chain) {
            child = child.getChild(chainLabel);
            if (child == null) {
               return result;
            }
        }
        List<PivotNode> drills = drill(config[dim], chain.size() + 1, new ArrayList<>(), child);
        for (PivotNode drill : drills) {
            List<PivotNode> children = drill.getChildren();
            if (children == null) {
                for (int i = drill.start; i < drill.end; i++) {
                    result.insert(dataPoints[i]);
                }
            } else {
                for (PivotNode childChild : children) {
                    queryDFS(result, childChild, dim + 1, query);
                }
            }
        }
        return result;
    }

    private List<PivotNode> drill(int chainDim, int chainSize, List<PivotNode> result, PivotNode root) {
        if (chainDim <= chainSize) {
            result.add(root);
            return result;
        }
        for (PivotNode child : root.getChildren()) {
            drill(chainDim, chainSize + 1, result, child);
        }
        return result;
    }

    @Override
    public T getCuboidInstance() {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new IllegalArgumentException("error class");
        }
    }

}
