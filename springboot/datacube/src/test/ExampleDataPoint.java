package test;

import main.yefancy.lfz.datacube.api.IDataPoint;

import java.util.ArrayList;
import java.util.List;

public class ExampleDataPoint implements IDataPoint {
    public enum DeviceType {
        ANDROID,
        IPHONE
    }

    private final int geoX;
    private final int geoY;
    private final int geoZ;
    private final DeviceType deviceType;
    private final long time;

    public ExampleDataPoint(int geoX, int geoY, int geoZ,DeviceType deviceType, long time) {
        this.geoX = geoX;
        this.geoY = geoY;
        this.geoZ = geoZ;
        this.deviceType = deviceType;
        this.time = time;
    }

    public int getGeoX() {
        return geoX;
    }

    public int getGeoY() {
        return geoY;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("geoX=").append(geoX);
        sb.append(", geoY=").append(geoY);
        sb.append(", deviceType=").append(deviceType);
        sb.append(", time=").append(time);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public List<List<Long>> getLabels() {
        // TODO Auto-generated method stub
        List<Long> geo = new ArrayList<Long>();
        List<Long> device = new ArrayList<Long>();
        geo.add((long) geoX);
        geo.add((long) geoY);
        geo.add((long) geoZ);
        device.add((long)deviceType.ordinal());
        List<List<Long>> labels = new ArrayList<List<Long>>();
        labels.add(geo);
        labels.add(device);
        return labels;
    }

    @Override
    public long getLabel(int dimension, int chainDim) {
        // TODO Auto-generated method stub
        getLabels().get(dimension).get(chainDim);
        return 0;
    }

    @Override
    public int getDimension() {
        // TODO Auto-generated method stub
        return 2;
    }
}
