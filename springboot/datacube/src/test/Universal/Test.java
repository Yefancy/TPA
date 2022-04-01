//package test.Universal;
//
//import main.yefancy.lfz.datacube.PrivacyCube.PrivacyCube;
//import main.yefancy.lfz.datacube.PrivacyCube.TableDP;
//import main.yefancy.lfz.datacube.PrivacyCube.WebNode;
//import main.yefancy.lfz.datacube.api.utils.tuples.Tuple2;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//public class Test {
//    public static PrivacyCube cube = new PrivacyCube();
//    public static void main(String[] args) throws IOException {
//        main();
//    }
//
//    public static void main() throws IOException {
//        FileReader fr = new FileReader("F:\\Datasets\\kaggle\\insurance.csv");
//        BufferedReader bf = new BufferedReader(fr);
//        String str = bf.readLine();
//        String[] title = str.split(",");
//        str = bf.readLine();
//        String[] firstLine = str.split(",");
//        var map = cube.schemaMap;
//        List<Integer> cate = new ArrayList<>();
//        List<Integer> num = new ArrayList<>();
//        for (int i = 0; i < firstLine.length; i++) {
//            if(isNumeric(firstLine[i])) {
//                num.add(i);
//            } else {
//                cate.add(i);
//                map.add(new Tuple2<>(title[i], new ArrayList<>()));
//            }
//        }
//        for (Integer integer : num) {
//            map.add(new Tuple2<>(title[integer], Collections.singletonList("All")));
//        }
//        while (str != null) {
//            String[] data = str.split(",");
//            long[] _cate = new long[cate.size()];
//            double[] _num = new double[num.size()];
//            for (int i = 0; i < cate.size(); i++) {
//                var d = data[cate.get(i)];
//                if(!map.get(i).y.contains(d)) {
//                    map.get(i).y.add(d);
//                }
//                _cate[i] = map.get(i).y.indexOf(d);
//            }
//            for (int i = 0; i < num.size(); i++) {
//                _num[i] = Double.parseDouble(data[num.get(i)]);
//            }
//            TableDP dp = new TableDP(_cate, _num, map);
//            cube.insert(dp);
//            str = bf.readLine();
//        }
//        bf.close();
//        fr.close();
//    }
//
//    public static boolean isNumeric(final String str) {
//        // null or empty
//        if (str == null || str.length() == 0) {
//            return false;
//        }
//        try {
//            Integer.parseInt(str);
//            return true;
//        } catch (NumberFormatException e) {
//            try {
//                Double.parseDouble(str);
//                return true;
//            } catch (NumberFormatException ex) {
//                try {
//                    Float.parseFloat(str);
//                    return true;
//                } catch (NumberFormatException exx) {
//                    return false;
//                }
//            }
//        }
//    }
//}