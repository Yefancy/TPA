package main.yefancy.lfz.datacube.PrivacyCube;

import main.yefancy.lfz.datacube.api.IDataPoint;
import main.yefancy.lfz.datacube.api.utils.tuples.Tuple2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableDP implements IDataPoint {
    public long[] cate;
    public double[] num;
    public List<Tuple2<String, List<Object>>> map;


    public TableDP(long[] cate, double[] num, List<Tuple2<String, List<Object>>> map) {
        this.cate = cate;
        this.num = num;
        this.map = map;
    }

    @Override
    public List<List<Long>> getLabels() {
        List<List<Long>> result = new ArrayList<>();
        for (long l : cate) {
            result.add(Collections.singletonList(l));
        }
        for (double aDouble : num) {
            result.add(Collections.singletonList(0L));
        }
        return result;
    }
}
