package test.Linkube;

import main.yefancy.lfz.datacube.Linkube.Linkube;
import main.yefancy.lfz.datacube.Linkube.SumCountBinLink;
import test.ExampleDataPoint;

import java.util.*;

public class LinkubeTest {
    public static void main(String[] args) {
        int time =20120302;
        Date date = new Date(time / 10000, (time % 10000) / 100, time % 100);
        System.out.println(date.getMonth());
        List<ExampleDataPoint> data = prepareDataPoints2();
        checkRight(data);
        int times = 2000000;
        while(times > 0) {
            times--;
            System.out.print((2000000-times)+":");
            checkRight(randomNode());
            //data = randomSort(data);
        }

        System.out.println("end");
    }

    private static List<ExampleDataPoint> prepareDataPoints() {
        List<ExampleDataPoint> list = new ArrayList<>();
        list.add(new ExampleDataPoint(1, 2, 1, ExampleDataPoint.DeviceType.ANDROID, 0));
        list.add(new ExampleDataPoint(1, 2, 1, ExampleDataPoint.DeviceType.IPHONE, 0));
        list.add(new ExampleDataPoint(2, 1, 1, ExampleDataPoint.DeviceType.IPHONE, 0));
        list.add(new ExampleDataPoint(2, 2, 1, ExampleDataPoint.DeviceType.ANDROID, 0));
        list.add(new ExampleDataPoint(3, 1, 1, ExampleDataPoint.DeviceType.IPHONE, 0));
        return list;
    }

    private static List<ExampleDataPoint> prepareDataPoints2() {
        List<ExampleDataPoint> list = new ArrayList<>();
        list.add(new ExampleDataPoint(0, 1, 3, ExampleDataPoint.DeviceType.IPHONE, 0));
        list.add(new ExampleDataPoint(0, 3, 1, ExampleDataPoint.DeviceType.ANDROID, 0));
        list.add(new ExampleDataPoint(0, 3, 2, ExampleDataPoint.DeviceType.IPHONE, 0));
        list.add(new ExampleDataPoint(1, 3, 1, ExampleDataPoint.DeviceType.ANDROID, 0));
        list.add(new ExampleDataPoint(3, 2, 3, ExampleDataPoint.DeviceType.ANDROID, 0));
        list.add(new ExampleDataPoint(1, 1, 1, ExampleDataPoint.DeviceType.IPHONE, 0));
        list.add(new ExampleDataPoint(3, 2, 1, ExampleDataPoint.DeviceType.ANDROID, 0));
        list.add(new ExampleDataPoint(2, 1, 0, ExampleDataPoint.DeviceType.ANDROID, 0));
        list.add(new ExampleDataPoint(3, 2, 1, ExampleDataPoint.DeviceType.ANDROID, 0));
        list.add(new ExampleDataPoint(1, 0, 1, ExampleDataPoint.DeviceType.IPHONE, 0));
        list.add(new ExampleDataPoint(3, 2, 1, ExampleDataPoint.DeviceType.ANDROID, 0));
        list.add(new ExampleDataPoint(1, 0, 3, ExampleDataPoint.DeviceType.IPHONE, 0));
        list.add(new ExampleDataPoint(0, 1, 3, ExampleDataPoint.DeviceType.ANDROID, 0));
        list.add(new ExampleDataPoint(3, 3, 1, ExampleDataPoint.DeviceType.ANDROID, 0));

        return list;
    }

    private static List<ExampleDataPoint> randomNode(){
        List<ExampleDataPoint> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < random.nextInt(22) + 10; i++) {
            if (random.nextInt(3) == 1)
                list.add(new ExampleDataPoint(random.nextInt(4), random.nextInt(4), random.nextInt(4), ExampleDataPoint.DeviceType.IPHONE, 0));
            else
                list.add(new ExampleDataPoint(random.nextInt(4), random.nextInt(4), random.nextInt(4), ExampleDataPoint.DeviceType.ANDROID, 0));
        }
        return list;
    }

    private static List<ExampleDataPoint> randomSort(List<ExampleDataPoint> origin) {
        List<ExampleDataPoint> list = new ArrayList<>();
        Random random = new Random();
        while (origin.size() > 0)
            list.add(origin.remove(random.nextInt(origin.size())));
        return list;
    }

    private static void checkRight(List<ExampleDataPoint> data) {
        int[][] split = new int[][] {{3},{1}};
        Linkube<SumCountBinLink> linkube = new Linkube<>(SumCountBinLink.class, split);
        for (ExampleDataPoint dataPoint : data) {
            linkube.insert(dataPoint);
        }
        List<List<Long>> query = new ArrayList<>();
        query.add(new ArrayList<>());
        query.add(new ArrayList<>());
        long result1 = linkube.query(query).count;
        if (data.size() == result1 && data.size() == result1)
            System.out.println("Right! "+data.size() + "==" + result1);
        else
            System.out.println("Error " + data.size() + "!=" + result1 + " or " + result1);
    }
}
