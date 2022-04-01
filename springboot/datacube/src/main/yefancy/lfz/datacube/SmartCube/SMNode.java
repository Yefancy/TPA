package main.yefancy.lfz.datacube.SmartCube;

import main.yefancy.lfz.datacube.api.tree.INode;
import main.yefancy.lfz.datacube.api.tree.Node;

public class SMNode extends Node {
    public int ASN = 1;

    public SMNode(long user_label, int dim, int level) {
        super(user_label, dim, level);
    }

    public SMNode(long user_label, int dim, int level, int asn) {
        super(user_label, dim, level);
        this.ASN = asn;
    }

    public SMNode(long label) {
        super(label);
    }

    @Override
    public Node newProperChild(long user_label) {
        SMNode childNode = new SMNode(user_label, this.decodeDim(), this.decodeLevel() + 1);
        addChildNode(childNode, false);
        return childNode;
    }

    @Override
    public INode shallowCopy() {
        var children = getChildren();
        INode copy = new SMNode(label);
        copy.setSharedContentWithNode(this);
        if (children != null) {
            for (INode child : children) {
                copy.addChildNode(child, true);
            }
        }
        return copy;
    }
}
