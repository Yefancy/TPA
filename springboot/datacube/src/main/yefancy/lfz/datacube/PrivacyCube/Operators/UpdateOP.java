package main.yefancy.lfz.datacube.PrivacyCube.Operators;

import main.yefancy.lfz.datacube.PrivacyCube.Excel;

import java.util.List;
import java.util.Map;

public class UpdateOP {
    public Map<Integer, Map<String, Object>> modify;
    public List<Map<String, Object>> add;
    public List<Integer> remove;

    public void execute(Excel excel) {
        if (modify != null) {
            modify.forEach((index, data)->excel.modified.put(index, data));
        }
        if (add != null) {
            excel.desserts.addAll(add);
        }
        if (remove != null) {
            excel.removed.addAll(remove);
        }
    }

    @Override
    public String toString() {
        String op = "OP:";
        if (modify != null) {
            op += " modify";
        }
        if (add != null) {
            op += " add";
        }
        if (remove != null) {
            op += " remove";
        }
        return op;
    }
}
