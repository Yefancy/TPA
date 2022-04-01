package main.yefancy.lfz.datacube.api;

import java.util.List;

public interface IDataPoint {
    List<List<Long>> getLabels();
    default long getLabel(int dimension, int chainDim) {
        return getLabels().get(dimension).get(chainDim);
    }
    default int getDimension() {
        return getLabels().size();
    }
}
