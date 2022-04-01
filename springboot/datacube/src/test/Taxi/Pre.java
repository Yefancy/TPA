package test.Taxi;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Pre {
    public static void main(String[] args) throws IOException, ParseException {
        long startTime =
                new Date(2008 - 1900, Calendar.FEBRUARY, 2, 0, 0, 0).getTime();
        long endTime =
                new Date(2008 - 1900, Calendar.FEBRUARY, 10, 0, 0, 0).getTime();
        long dur = 30 * 1000;

        List<List<TaxiDataPoint>> patches = new ArrayList<>();
        for (int i = 0; i < (endTime - startTime) / dur; i++) {
            patches.add(new ArrayList<>());
        }

        System.out.println((endTime - startTime) / dur);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (int i = 1; i <= 10357; i++) {
            FileReader reader = new FileReader(
                    String.format("F:\\Datasets\\taxi_log_2008_by_id\\%d.txt",
                            i));
            BufferedReader in = new BufferedReader(reader);
            String str;
            while ((str = in.readLine()) != null) {
                String[] line = str.split(",");
                long timestamp = sdf.parse(line[1]).getTime();
                patches.get((int) ((timestamp - startTime) / dur))
                        .add(new TaxiDataPoint(timestamp,
                                Float.parseFloat(line[3]),
                                Float.parseFloat(line[2])));
            }
            in.close();
            reader.close();
            System.out.println(i);
        }
        Gson gson = new Gson();
        int i = 0;
        int count = 0;
        for (List<TaxiDataPoint> patch : patches) {
            count += patch.size();
            if (patch.size() > 0) {
                FileWriter writer = new FileWriter(String.format("src\\test\\Taxi\\patches2\\p%d.json", i));
                writer.write(gson.toJson(patch));
                writer.close();
                i++;
                System.out.println(i);
            }
        }
        System.out.println(count);
//        for (int i = patches.size() - 1; i >= 0; i--) {
//            FileWriter writer = new FileWriter(String.format("src\\test\\Taxi\\patches\\p%d.json", i));
//            writer.write(gson.toJson(patches.get(i)));
//            writer.close();
//        }
        System.out.println("end");
    }
}
