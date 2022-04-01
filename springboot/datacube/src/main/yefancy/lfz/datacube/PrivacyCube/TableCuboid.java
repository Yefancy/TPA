package main.yefancy.lfz.datacube.PrivacyCube;

import main.yefancy.lfz.datacube.api.tree.IContent;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TableCuboid implements IContent{
    public List<TabularDataPoint> d = null;
    public int k = -1;
    public int[] l = new int[0];
    public double[] t = new double[0];

    public void update_k_Anonymous() {
        k = d == null? 0: d.size();
    }

    public void update_l_Diversity(List<Integer> sensitives) {
        l = new int[sensitives.size()];
        for (int i = 0; i < sensitives.size(); i++) {
            Set<Integer> sens = new HashSet<>();
            if (d != null) {
                for (TabularDataPoint dp : d) {
                    sens.addAll(dp.getLabels().get(sensitives.get(i)).get(0));
                }
            }
            l[i] = sens.size();
        }
    }

    public void update_t_closeness(List<Integer> sensitives, List<double[]> globals, DistanceMeasure emd) {
        t = new double[sensitives.size()];
        for (int i = 0; i < sensitives.size(); i++) {
            t[i] = emd.compute(globals.get(i), getDistribute(sensitives.get(i), globals.get(i).length));
        }
    }

    public double[] getDistribute(int sensitive, int size) {
        double[] distribute = new double[size];
        if (d != null) {
            for (TabularDataPoint dp : d) {
                for (Integer integer : dp.getLabels().get(sensitive).get(0)) {
                    distribute[integer] += 1;
                }
            }
        }
        return distribute;
    }

    public void insert(TabularDataPoint dataPoint) {
        if (d == null)
            d = new ArrayList<>();
        d.add(dataPoint);
    }

    public void merge(TableCuboid cuboid) {
        if (cuboid != null && cuboid.d != null) {
            if (d == null)
                d = new ArrayList<>();
            cuboid.d.forEach(dp->{
                if (!d.contains(dp))
                    d.add(dp);
            });
        }
    }

    public boolean inValid(){
        if (d == null) return true;
        d.removeIf(t->t.i==-1);
        return d.size() == 0;
    }

    public int result() {
        return d == null? 0 : d.size();
    }

    public TableCuboid addNoise(double noise) {
        return null;
    }

    public IContent shallowCopy() {
        TableCuboid copy = new TableCuboid();
        if (d != null) {
            copy.d = new ArrayList<>();
            copy.d.addAll(d);
        }
        return copy;
    }
}
