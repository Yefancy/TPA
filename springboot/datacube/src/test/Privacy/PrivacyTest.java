package test.Privacy;

import java.util.Random;

public class PrivacyTest {
    static Random random = new Random();
    public static void main(String[] args){
        System.out.println("real\tmax\tmin");
        for (int i = 1000; i < 20000; i += 1000) {
            int realMerge = 0;
            double maxMerge = Double.MIN_VALUE;
            double minMerge = Double.MAX_VALUE;
            for (int j = 0; j < i; j++) {
                realMerge += random.nextInt(2);
            }
            for (int j = 0; j <20; j++) {
                double tmp = 0;
                for (int k = 0; k < i; k++) {
                    tmp += getNoise(0,1);
                }
                maxMerge = Math.max(maxMerge, tmp);
                minMerge = Math.min(minMerge, tmp);
            }
            double noise = getNoise(0, i);
            System.out.printf("%d\t%d\t%d%n",realMerge,realMerge+Math.round(maxMerge),realMerge+Math.round(minMerge));
        }
    }

    public static double getNoise(double mu, double lambda) {
        double randomDouble = random.nextDouble() - 0.5;
        return mu - lambda * Math.signum(randomDouble) * Math.log(1 - 2 * Math.abs(randomDouble));
    }
}
