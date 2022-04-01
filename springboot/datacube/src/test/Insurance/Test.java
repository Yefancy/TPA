package test.Insurance;

import main.yefancy.lfz.datacube.PrivacyCube.PrivacyCube;
import main.yefancy.lfz.datacube.PrivacyCube.WebNode;
import main.yefancy.lfz.datacube.api.utils.tuples.Tuple2;
import org.apache.commons.math3.ml.distance.EarthMoversDistance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Test {
    public static PrivacyCube cube = new PrivacyCube();
    public static void main(String[] args) throws IOException {
        main();
        List<Integer> schema = Arrays.asList(3, 2, 0);
        WebNode root = Test.cube.getWebTree(schema, new ArrayList<>(), new EarthMoversDistance());
    }

    public static void main() throws IOException {
//        FileReader fr = new FileReader("F:\\Datasets\\kaggle\\insurance.csv");
//        BufferedReader bf = new BufferedReader(fr);
//        String str = bf.readLine();
//        if (str != null) {
//            while ((str = bf.readLine()) != null) {
//                cube.insert(new insuranceDP(str));
//            }
//        }
//        bf.close();
//        fr.close();
//        var map = cube.schemaMap;
//        map.add(new Tuple2<>("region", Arrays.asList("southwest", "northeast", "northwest", "southeast")));
//        map.add(new Tuple2<>("sex", Arrays.asList("male", "female")));
//        map.add(new Tuple2<>("smoker", Arrays.asList("yes", "no")));
//        map.add(new Tuple2<>("children", Arrays.asList("0", "1", "2", "3", "4", "5")));
//        map.add(new Tuple2<>("age", Collections.singletonList("18-64")));
//        map.add(new Tuple2<>("bmi", Collections.singletonList("18-64")));
//        map.add(new Tuple2<>("charges", Collections.singletonList("1120-63770")));
    }
}
