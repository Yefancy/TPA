package test.Tweets;

import main.yefancy.lfz.datacube.api.IDataPoint;

import java.util.ArrayList;
import java.util.List;

public class TweetDataPoint implements IDataPoint {
    private final long lat;
    private final long lon;
    private final long type;
    private final List<List<Long>> labels;

    public TweetDataPoint(long lat, long lon, long type) {
        this.lat = lat;
        this.lon = lon;
        this.type = type;
        labels = new ArrayList<>();
        List<Long> typeList = new ArrayList<Long>();
        typeList.add(type);
        labels.add(getGeo(24));
        labels.add(typeList);
    }

    public TweetDataPoint(long lat, long lon, long type, long year, long month, long day) {
        this.lat = lat;
        this.lon = lon;
        this.type = type;
        labels = new ArrayList<>();
        List<Long> typeList = new ArrayList<>();
        typeList.add(type);
        List<Long> dateList = new ArrayList<>();
        dateList.add(year);
        dateList.add(month);
        dateList.add(day);
        labels.add(getGeo(24));
        labels.add(typeList);
        labels.add(dateList);
    }
    public long getX() {return lat;}
    public long getY() {return lon;}
    public long getType() {return type;}

    public List<Long> getGeo(int depth) {
        List<Long> geo = new ArrayList<Long>();
        long maxLat = 1800000000L;
        long minLat = 0;
        long maxLon = 3600000000L;
        long minLon = 0;
        for (int i = 0; i < depth; i++) {
            long middleLat = minLat + (maxLat+1-minLat)/2;
            long latB = lat > middleLat ? 0:2;
            if(lat > middleLat)
                minLat = middleLat;
            else
                maxLat = middleLat;

            long middleLon = minLon + (maxLon+1-minLon)/2;
            long lonB = lon > middleLon ? 1:0;
            if(lon > middleLon)
                minLon = middleLon;
            else
                maxLon = middleLon;
            geo.add(latB+lonB);
        }
        return geo;
    }

    @Override
    public List<List<Long>> getLabels() {
        return this.labels;
    }

    @Override
    public long getLabel(int dimension, int chainDim) {
        return getLabels().get(dimension).get(chainDim);
    }

    @Override
    public int getDimension() {
        return labels.size();
    }

}
