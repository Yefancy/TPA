package main.yefancy.lfz.datacube.PrivacyCube;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebNode {
    public List<WebNode> children = new ArrayList<>();
    public int count;
    public int branch;
    public Set<Integer> labels = new HashSet<>();
    public TableCuboid content = new TableCuboid();

    public void updateData() {
        this.count = content.d == null? 0 : content.d.size();
        this.branch = 0;
        for (WebNode child : children) {
            this.branch += child.branch;
        }
    }
}
