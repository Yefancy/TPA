package test.Taxi;

import main.yefancy.lfz.datacube.api.IDataPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaxiDataPoint implements IDataPoint {
    public final List<List<Long>> labels;

    public TaxiDataPoint(long timestamp, float lat, float lon) {
        labels = new ArrayList<>();
        List<Long> geo = new ArrayList<>();
        float maxLat = 90;
        float minLat = -90;
        float maxLon = 180;
        float minLon = -180;
        for (int i = 0; i < 24; i++) {
            float middleLat = minLat + (maxLat - minLat) / 2;
            long latB = lat > middleLat ? 0 : 2;
            if(lat > middleLat)
                minLat = middleLat;
            else
                maxLat = middleLat;

            float middleLon = minLon + (maxLon - minLon) / 2;
            long lonB = lon > middleLon ? 1 : 0;
            if(lon > middleLon)
                minLon = middleLon;
            else
                maxLon = middleLon;
            geo.add(latB+lonB);
        }
        labels.add(geo);
        List<Long> time = new ArrayList<>();
        Date t = new Date(timestamp);
        time.add((long) t.getDate());
        time.add((long) t.getHours());
        time.add((long) t.getMinutes());
        time.add((long) t.getSeconds());
        labels.add(time);
    }

    @Override
    public List<List<Long>> getLabels() {
        return labels;
    }
}
