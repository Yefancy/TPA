package test.DPVcube;

import main.yefancy.lfz.datacube.DPVcube.ACube;
import main.yefancy.lfz.datacube.DPVcube.ClientCube;
import main.yefancy.lfz.datacube.DPVcube.ServerCube;
import main.yefancy.lfz.datacube.Linkube.Linkube;
import main.yefancy.lfz.datacube.Linkube.SumCountBinLink;
import test.AutoDataPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class DPVcubeTest {
    private static int[][][][] testData = {
            {
                    {{0, 0}, {0}},
                    {{0, 0}, {1}},
                    {{0, 1}, {0}},
                    {{0, 2}, {0}},
                    {{0, 2}, {1}},
                    {{1, 0}, {2}},
                    {{1, 1}, {0}},
                    {{1, 1}, {2}},
            },
            {
                    {{0, 0}, {2}},
                    {{0, 1}, {0}},
                    {{1, 2}, {0}},
            },
            {
                    {{0, 0}, {0}},
                    {{0, 0}, {1}},
                    {{1, 0}, {0}},
            },
    };

    public static void main(String[] args) {
        List<AutoDataPoint> data1 = prepareDataPoints(testData[0]);
        List<AutoDataPoint> data2 = prepareDataPoints(testData[1]);
        List<AutoDataPoint> data3 = prepareDataPoints(testData[2]);
        var cube1 = checkRight(data1, new int[] {3, 3},1);
        var cube2 = checkRight(data2, new int[] {3, 3},2);
        var cube3 = checkRight(data3, new int[] {2, 2},3);
        var server = new ServerCube<>(SumCountBinLink.class);
        //////
        var bone1 = cube1.serializedBasicTree();
        var bone2 = cube2.serializedBasicTree();
        var bone3 = cube3.serializedBasicTree();
        server.deSerializedBasicTree(bone1);
        server.deSerializedBasicTree(bone2);
        server.deSerializedBasicTree(bone3);
        //////
        var boneS = server.serializedBasicTree();
        cube1.deSerializedBasicTree(boneS);
        cube2.deSerializedBasicTree(boneS);
        cube3.deSerializedBasicTree(boneS);
        var boneCheck1 = cube1.serializedBasicTree();
        var boneCheck2 = cube2.serializedBasicTree();
        var boneCheck3 = cube3.serializedBasicTree();
        //////
        var cuboid1 = cube1.getUpload();
        var cuboid2 = cube2.getUpload();
        var cuboid3 = cube3.getUpload();
        server.update(cuboid1);
        server.update(cuboid2);
        server.update(cuboid3);


//        checkRight(data);
//        int times = 2000000;
//        while (times > 0) {
//            times--;
//            System.out.print((2000000 - times) + ":");
//            checkRight(randomNode());
//            //data = randomSort(data);
//        }
    }

    private static List<AutoDataPoint> prepareDataPoints(int[][][] data) {
        List<AutoDataPoint> list = new ArrayList<>();
        for (int[][] d : data) {
            list.add(new AutoDataPoint(d));
        }
        return list;
    }

    private static List<AutoDataPoint> randomNode() {
        List<AutoDataPoint> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < random.nextInt(22) + 10; i++) {
            //TODO
        }
        return list;
    }

    private static List<AutoDataPoint> randomSort(List<AutoDataPoint> origin) {
        List<AutoDataPoint> list = new ArrayList<>();
        Random random = new Random();
        while (origin.size() > 0) {
            list.add(origin.remove(random.nextInt(origin.size())));
        }
        return list;
    }

    private static ClientCube<SumCountBinLink> checkRight(List<AutoDataPoint> data, int[] privacy, long seed) {
        ClientCube<SumCountBinLink> linkube = new ClientCube<>(SumCountBinLink.class, privacy, seed);
        for (AutoDataPoint dataPoint : data) {
            linkube.insert(dataPoint);
        }
        List<List<Long>> query = new ArrayList<>();
        query.add(new ArrayList<>());
        query.add(new ArrayList<>());
        long result1 = linkube.query(query).count;
        if (data.size() == result1 && data.size() == result1) {
            System.out.println("Right! " + data.size() + "==" + result1);
        } else {
            System.out.println("Error " + data.size() + "!=" + result1 + " or " + result1);
        }
        return linkube;
    }
}
