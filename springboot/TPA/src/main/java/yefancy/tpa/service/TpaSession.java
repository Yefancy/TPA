package yefancy.tpa.service;

import main.yefancy.lfz.datacube.PrivacyCube.Excel;
import main.yefancy.lfz.datacube.PrivacyCube.Operators.IOperator;
import main.yefancy.lfz.datacube.PrivacyCube.PrivacyCube;
import main.yefancy.lfz.datacube.PrivacyCube.TabularDataPoint;
import main.yefancy.lfz.datacube.PrivacyCube.WebNode;
import org.apache.commons.math3.ml.distance.EarthMoversDistance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TpaSession {
    public String token;
    public Excel excel;
    public PrivacyCube cube;
    public List<Integer> schema;
    public List<Integer> sensitive;
    public WebNode root;
    public List<IOperator> operators;

    public TpaSession(Excel excel) {
        token = UUID.randomUUID().toString();
        this.excel = excel;
        reloadCube();
    }

    public void reloadCube() {
        if (cube == null ) {
            cube = new PrivacyCube();
        } else {
            cube = new PrivacyCube(cube.schemaMap);
        }

        for (int i = 0; i < excel.desserts.size(); i++) {
            if (excel.removed.contains(i)) continue;
            Map<String, Object> data = excel.modified.get(i);
            cube.insert(new TabularDataPoint(excel.headers, data == null ? excel.desserts.get(i) : data, cube.schemaMap, i));
        }
        operators = new ArrayList<>();
//        System.out.println("loadCube");
    }

    public void updateRoot() {
        if (this.schema != null && this.sensitive != null) {
            this.root = this.cube.getWebTree(this.schema, this.sensitive, new EarthMoversDistance());
            this.operators.forEach(op->op.execute(this.root, this.schema));
        }
    }

    public void dispose(){
    }
}
