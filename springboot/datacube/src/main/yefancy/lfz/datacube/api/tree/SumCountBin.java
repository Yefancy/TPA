package main.yefancy.lfz.datacube.api.tree;

import main.yefancy.lfz.datacube.api.IDataPoint;

public class SumCountBin implements ICuboid {
    public int count;

    public SumCountBin(int count) {
        this.count = count;
    }

    public SumCountBin() {
        this(0);
    }

    @Override
    public IContent shallowCopy() {
        return new SumCountBin(count);
    }

    @Override
    public void insert(IDataPoint dataPoint) {
        count++;
    }

    @Override
    public void merge(ICuboid cuboid) {
        if (cuboid instanceof SumCountBin _cuboid)
            this.count += _cuboid.count;
    }

    @Override
    public int result() {
        return count;
    }

    @Override
    public SumCountBin addNoise(double noise) {
        count += noise;
        return this;
    }
}
