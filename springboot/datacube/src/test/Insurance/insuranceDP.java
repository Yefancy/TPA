package test.Insurance;

import main.yefancy.lfz.datacube.api.IDataPoint;

import java.util.ArrayList;
import java.util.List;

public class insuranceDP implements IDataPoint {
    public final List<List<Long>> labels;

    public insuranceDP(String data) {
        labels = new ArrayList<>();
        String[] strings = data.split(",");
        for (int i = 0; i < strings.length; i++) {
            labels.add(new ArrayList<>());
        }
        for (int i = 0; i < strings.length; i++) {
            switch (i) {
                case 0 -> labels.get(4).add(0L);
                case 1 -> labels.get(1).add(strings[i].equals("male")? 0L : 1L);
                case 2 -> labels.get(5).add(0L);
                case 3 -> labels.get(3).add(Long.parseLong(strings[i]));
                case 4 -> labels.get(2).add(strings[i].equals("yes")? 0L : 1L);
                case 5 -> labels.get(0).add(strings[i].equals("southwest")? 0L : strings[i].equals("northeast")? 1L :strings[i].equals("northwest")? 2L :3L);
                case 6 -> labels.get(6).add(0L);
            }
        }
    }

    @Override
    public List<List<Long>> getLabels() {
        return this.labels;
    }
}
