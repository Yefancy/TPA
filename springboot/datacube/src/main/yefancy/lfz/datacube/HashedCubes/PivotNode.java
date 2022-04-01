package main.yefancy.lfz.datacube.HashedCubes;

import java.util.ArrayList;
import java.util.List;

public class PivotNode {
    public int start;
    public int end;
    private List<PivotNode> children;
    private final long label;

    public PivotNode(long label, int start, int end) {
        this.label = label;
        this.start = start;
        this.end = end;
    }

    public long getLabel() {
        return label;
    }

    public List<PivotNode> getChildren(){
        return children;
    }

    public PivotNode addChild(long label) {
        return this.addChild(label, start, end);
    }

    public PivotNode addChild(long label, int start, int end) {
        if (children == null) {
            children = new ArrayList<>();
        }
        PivotNode child = new PivotNode(label, start, end);
        children.add(child);
        return child;
    }

    public PivotNode getChild(long label) {
        if (children == null) {
            return null;
        }
        for (PivotNode child : children) {
            if (child.label == label) {
                return child;
            }
        }
        return null;
    }
}
