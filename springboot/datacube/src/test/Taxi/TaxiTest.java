package test.Taxi;

import com.google.gson.Gson;
import main.yefancy.lfz.datacube.HashedCubes.HashedCubes;
import main.yefancy.lfz.datacube.HashedCubes.HashedCuboid;
import main.yefancy.lfz.datacube.Linkube.Linkube;
import main.yefancy.lfz.datacube.Linkube.SumCountBinLink;
import main.yefancy.lfz.datacube.Nanocube.Nanocube;
import main.yefancy.lfz.datacube.SmartCube.SmartCube;
import main.yefancy.lfz.datacube.api.ICube;
import test.GaussianTest.GaussianDataPoint;
import test.TestUtils.TestHelper;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TaxiTest {
    public static Random random = new Random(1);
    public static long startTime;
    public static void main(String[] args) throws FileNotFoundException {
        int[][] linkubeConfig = new int[2][1];
        int[] smartcubeConfig = new int[2];
        linkubeConfig[0][0] = 24;
        linkubeConfig[1][0] = 4;
        smartcubeConfig[0] = 24;
        smartcubeConfig[1] = 4;
//        ICube<?> cube = new Nanocube<>(SumCountBinLink.class);
//			ICube<?> cube = new SmartCube<>(SumCountBinLink.class, smartcubeConfig);
//			ICube<?> cube = new Linkube<>(SumCountBinLink.class, linkubeConfig);
			ICube<?> cube = new HashedCubes<>(HashedCuboid.class, smartcubeConfig);
//

        TestHelper testHelper = new TestHelper(cube, 0);
        for (int i = 0; i <= 16840; i++) {
            FileReader reader = new FileReader(String.format("src\\test\\Taxi\\patches2\\p%d.json", i));
            TaxiDataPoint[] dataPoints = new Gson().fromJson(reader, TaxiDataPoint[].class);
            testHelper.streamTimeTest(dataPoints, 0, TaxiTest::queryGenerator);
        }
    }

    private static List<List<Long>> queryGenerator() {
        return new ArrayList<>();
    }
}
