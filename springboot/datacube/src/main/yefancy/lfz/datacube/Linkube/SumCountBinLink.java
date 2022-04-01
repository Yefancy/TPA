package main.yefancy.lfz.datacube.Linkube;

import main.yefancy.lfz.datacube.api.IDataPoint;
import main.yefancy.lfz.datacube.api.tree.ICuboid;
import main.yefancy.lfz.datacube.api.tree.SumCountBin;

public class SumCountBinLink extends SumCountBin implements ICuboidLink{
    private SumCountBinLink nextNode = null;

    public SumCountBinLink(int count) {
        super(count);
    }

    public SumCountBinLink() {
        this(0);
    }

    @Override
    public String toString() {
        return"Count:" + count;
    }

    @Override
    public boolean isRootNode() {
        return true;
    }

    @Override
    public SumCountBinLink getNextNode() {
        return nextNode;
    }

    @Override
    public void setNextNode(ILink<ICuboidLink> nextContent) {
        nextNode = (SumCountBinLink) nextContent;
    }

    @Override
    public SumCountBinLink shallowCopy() {
        return new SumCountBinLink(count);
    }

    @Override
    public void merge(ICuboid cuboid) {
        if (cuboid instanceof SumCountBinLink _cuboid)
            this.count += _cuboid.count;
    }

    @Override
    public int result() {
        return count;
    }

    @Override
    public SumCountBinLink addNoise(double noise) {
        count += Math.round(noise);
        return this;
    }
}
