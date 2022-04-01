package test.GaussianTest;

 import main.yefancy.lfz.datacube.HashedCubes.HashedCubes;
 import main.yefancy.lfz.datacube.HashedCubes.HashedCuboid;
 import main.yefancy.lfz.datacube.Linkube.Linkube;
 import main.yefancy.lfz.datacube.Linkube.SumCountBinLink;
import main.yefancy.lfz.datacube.Nanocube.Nanocube;
 import main.yefancy.lfz.datacube.SmartCube.SmartCube;
 import main.yefancy.lfz.datacube.api.ICube;
 import main.yefancy.lfz.datacube.api.tree.ICuboid;
 import test.TestUtils.TestHelper;

 import javax.imageio.ImageIO;
 import java.awt.image.BufferedImage;
 import java.io.File;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;
 import java.util.Random;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;

public class GaussianTest {
	public static Random random = new Random(1);
	public static long startTime;
	public static void main(String[] args)
			throws IOException, InterruptedException {

		startTime = System.currentTimeMillis();
		int count = 500; // 200
		int queryTimes = 1000;
		int d = 7;
		int c= 5;
		for(int v = 7; v <= 10; v+=1) {
			d=v;
//			c=v;
//			count = v;
//			queryTimes = v;

			int[][] linkubeConfig = new int[d][1];
			int[] config = new int[d];
			int[] smartcubeConfig = new int[d];
			Arrays.fill(config, c);
			Arrays.fill(linkubeConfig, new int[]{c});
			Arrays.fill(smartcubeConfig, c);

//			int[][] linkubeConfig = new int[d][1];
//			int[] config = new int[d];
//			int[] smartcubeConfig = new int[d];
//			Arrays.fill(config, 3);
//			Arrays.fill(linkubeConfig, new int[]{3});
//			Arrays.fill(smartcubeConfig, 3);



//			ICube<?> cube = new Nanocube<>(SumCountBinLink.class);
			ICube<?> cube = new SmartCube<>(SumCountBinLink.class, smartcubeConfig);
//			ICube<?> cube = new Linkube<>(SumCountBinLink.class, linkubeConfig);
//			ICube<?> cube = new HashedCubes<>(HashedCuboid.class, smartcubeConfig);

			TestHelper th = new TestHelper(cube, 0);

			test(th, config, count, queryTimes);
			cube = null;
			th = null;
			while (TestHelper.usedMemory() / 1024 / 1024 > 50) {
				Thread.sleep(3000);
//				System.out.println("wait 4 gc: " + TestHelper.usedMemory() / 1024 / 1024);
			}
		}
	}
	
	public static void test(TestHelper testHelper, int[] config, int count, int queryTimes) {
		for (int i = 0; i < 50; i++) {
//			testHelper.buildMemTest(new GaussianDataPoint(config));
			GaussianDataPoint[] patches = new GaussianDataPoint[count];
			for (int i1 = 0; i1 < patches.length; i1++) {
				patches[i1] = new GaussianDataPoint(config);
			}
			testHelper.streamTimeTest(patches, queryTimes, ()->{
				List<List<Long>> query = new ArrayList<>();
				for (int value : config) {
					List<Long> chain = new ArrayList<>();
					int chainLength = random.nextInt(value);
					for (int k = 0; k < chainLength; k++) {
						chain.add((long) random.nextInt(4) % 5);
					}
					query.add(chain);

//					List<Long> chain = new ArrayList<>();
//					if(random.nextBoolean()) {
//						chain.add((long) random.nextInt(4) % 5);
//					}
//					query.add(chain);
				}
				return query;
			});
		}
	}
}
