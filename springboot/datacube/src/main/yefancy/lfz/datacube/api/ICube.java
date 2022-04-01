package main.yefancy.lfz.datacube.api;

import main.yefancy.lfz.datacube.api.tree.ICuboid;

import java.util.List;

public interface ICube<T extends ICuboid> {
    void insert(IDataPoint dataPoint);
    default void insert(IDataPoint[] dataPoint) {
        for (IDataPoint iDataPoint : dataPoint) {
            insert(iDataPoint);
        }
    }
    IQueryEngine getQueryEngine();
    T query(List<List<Long>> query);
    T getCuboidInstance();
}
