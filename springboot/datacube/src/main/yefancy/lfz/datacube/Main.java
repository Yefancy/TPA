package main.yefancy.lfz.datacube;

import main.yefancy.lfz.datacube.api.IDataPoint;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        FileWriter fw = new FileWriter("F:\\Datasets\\twitter\\1.json");
        FileReader fr = new FileReader("F:\\Datasets\\twitter\\tweets.json");
        BufferedWriter bfw = new BufferedWriter(fw);
        BufferedReader bf = new BufferedReader(fr);
        String str;
        while ((str = bf.readLine()) != null) {
            bfw.write(str+"\n");
        }
        bf.close();
        fr.close();
        fr = new FileReader("F:\\Datasets\\twitter\\tweets.json");
        bf = new BufferedReader(fr);
        while ((str = bf.readLine()) != null) {
            bfw.write(str+"\n");
        }
        bf.close();
        fr.close();
        fw.close();
    }

}
