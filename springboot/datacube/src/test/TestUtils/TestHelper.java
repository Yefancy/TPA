package test.TestUtils;

import main.yefancy.lfz.datacube.SmartCube.SmartCube;
import main.yefancy.lfz.datacube.api.ICube;
import main.yefancy.lfz.datacube.api.IDataPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TestHelper {
	public ICube<?> cube;
	public int dataCount;
	public int wCount;
	public static long startTime;
	public Consumer<TestHelper> onWCountInsert;
	public static Random random = new Random(1);

	public TestHelper(ICube<?> cube) {
		this.cube = cube;
		dataCount = 0;
		wCount = 100000;
		resetStartTime();
	}
	
	public TestHelper(ICube<?> cube, int wCount) {
		this.cube = cube;
		dataCount = 0;
		this.wCount = wCount;
		resetStartTime();
	}

	public void resetStartTime() {
		startTime = System.nanoTime();
	}

	public void insertData(IDataPoint data) {
		dataCount++;
		cube.insert(data);
		if (dataCount % wCount == 0)
			if(onWCountInsert != null) onWCountInsert.accept(this);
	}
	
	public void buildTimeTest(IDataPoint data) {
		insertData(data);
		if (dataCount % wCount == 0)
			System.out.println(dataCount / 10000 + "W\t" + usedTime() / 1000);
	}

	public void buildMemTest(IDataPoint data) {
		insertData(data);
		if (dataCount % wCount == 0)
			System.out.println(dataCount / 10000 + "W\t" + usedMemory() / 1024 / 1024);
	}
	
	public void streamTimeTest(IDataPoint[] dataPatches, int queryTimes, Supplier<List<List<Long>>> querySupplier) {
//		insertData(dataPatches);
		cube.insert(dataPatches);
		dataCount += dataPatches.length;

		wCount++;
		//time build
		long buildTime = usedTime();
		//time query
		long updateTime = 0;
		for(int i = 0;i < queryTimes; i++) {
			List<List<Long>> query = querySupplier.get();
			cube.query(query);
			if (cube instanceof SmartCube && random.nextInt(1000) > 995) {
				long st = System.nanoTime();
				((SmartCube<?>) cube).updateCuboid(query);
				updateTime += System.nanoTime() - st;
			}
		}
		long queryTime = usedTime() - buildTime - updateTime;
		System.out.printf("P%d\t%d\t%d\t%d\t%d%n", wCount, buildTime/1000000, queryTime/1000000, updateTime/1000000, (buildTime + updateTime + queryTime)/1000000);
		resetStartTime();

	}

	public long queryTest(int dimension, int times) {
		long startTime = System.currentTimeMillis();
		Random random = new Random();
		List<List<Long>> query = new ArrayList<List<Long>>();
		List<Long> geo = new ArrayList<Long>();
		query.add(geo);
		for(int d = 1; d < dimension; d++)
			query.add(new ArrayList<Long>());
		for (int i = 0; i < times; i++) {
			geo.clear();
			queryDFS(cube, query, random.nextInt(25));
		}
		return System.currentTimeMillis() - startTime;
	}

	public void queryTest() {
		List<List<Long>> query = new ArrayList<List<Long>>();
		List<Long> type = new ArrayList<Long>();
		List<Long> geo = new ArrayList<Long>();
		query.add(geo);
		query.add(type);
		for (int d = 0; d <= 25; d++) {
			ts result = queryDFS(cube, query, d);
			System.out.print(result.max + "\t" + result.min + "\t" + result.mean + "\t");
		}
		System.out.println();
	}

	public ts queryDFS(ICube<?> cube, List<List<Long>> query, int size) {
//		List<Long> geo = query.get(0);
//		if (geo.size() == size) {
//			long st = System.nanoTime();
//			cube.query(query);
//			long et = System.nanoTime();
//			return new ts(et - st);
//		} else {
//			geo.add(0L);
//			ts tmpts = queryDFS(cube, query, size);
//			for (int i = 1; i < 4; i++) {
//				geo.remove(geo.size() - 1);
//				geo.add((long) i);
//				tmpts.combineTs(queryDFS(cube, query, size));
//			}
//			return tmpts;
//		}
		List<Long> geo = query.get(0);
		if (geo.size() == size) {
			cube.query(query);
			return null;
		} else {
			geo.add((long) new Random().nextInt(4));
			return queryDFS(cube, query, size);
		}
	}

	public static long usedMemory() {
		Runtime.getRuntime().gc();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
	}

	public long usedTime() {
		return (System.nanoTime() - startTime);
	}

	public static class ts {
		public long max;
		public long min;
		public long mean;

		public ts(long time) {
			max = time;
			min = time;
			mean = time;
		}

		public ts combineTs(ts other) {
			max = Math.max(max, other.max);
			min = Math.min(min, other.min);
			mean = (mean + other.mean) / 2;
			return this;
		}
	}
}


