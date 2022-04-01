package test;

import main.yefancy.lfz.datacube.api.IDataPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AutoDataPoint implements IDataPoint {
    private final List<List<Long>> labels;

    public AutoDataPoint(int[] paths, int[] branches, Integer seed){
        Random random;
        if(seed == null)
            random = new Random();
        else
            random = new Random(seed);
        labels = new ArrayList<>();
        for (int i = 0; i < paths.length; i++) {
            var path = new ArrayList<Long>();
            for (int j = 0; j < paths[i]; j++) {
                path.add((long) random.nextInt(branches[i]));
            }
            labels.add(path);
        }
    }

    public AutoDataPoint(int[][] data){
        this.labels = new ArrayList<>();
        for (int[] labels:data){
            var path = new ArrayList<Long>();
            for(int label:labels){
                path.add((long)label);
            }
            this.labels.add(path);
        }
    }

    @Override
    public List<List<Long>> getLabels() {
        return labels;
    }
}
