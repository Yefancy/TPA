package test.GaussianTest;

import main.yefancy.lfz.datacube.api.IDataPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GaussianDataPoint implements IDataPoint {
	private final List<List<Long>> labels;
	
	public GaussianDataPoint(int[] config) {
		// TODO Auto-generated constructor stub
		labels = new ArrayList<List<Long>>();
		Random r = new Random();
		for (int i : config) {
			List<Long> chain = new ArrayList<Long>();
			for(int j =0;j<i;j++) {
				double value = r.nextGaussian();
				long data = 4;
				if (value< -1.5)
					data = 0;
				else if(value < -0.5)
					data = 1;
				else if(value < 0.5)
					data = 2;
				else if(value < 1.5)
					data = 3;
				chain.add(data);
			}
			labels.add(chain);
		}
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
