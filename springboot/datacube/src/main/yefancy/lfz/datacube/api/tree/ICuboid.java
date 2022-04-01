package main.yefancy.lfz.datacube.api.tree;

import main.yefancy.lfz.datacube.api.IDataPoint;

public interface ICuboid extends IContent {
    void insert(IDataPoint dataPoint);
    void merge(ICuboid cuboid);
    int result();
    ICuboid addNoise(double noise);
}
