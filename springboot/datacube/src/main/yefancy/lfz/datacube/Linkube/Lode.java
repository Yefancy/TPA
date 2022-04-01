package main.yefancy.lfz.datacube.Linkube;

import main.yefancy.lfz.datacube.api.tree.INode;
import main.yefancy.lfz.datacube.api.tree.Node;

import java.util.ArrayList;
import java.util.List;

public class Lode extends Node implements ILode {
    private static final int NEXT_NODE_BIT_INDEX = 1;
    public static int linkedListCount = 0;
    private int count = 1;

    public Lode(long user_label, int dim, int level) {
        this(user_label << 40 | ((long)dim) << 36 | ((long)level) << 28 | 1);
        if (dim > 0XF || level > 0XFF) {
            throw new IllegalArgumentException("dim and level should smaller than 0XFF!");
        }
    }

    protected Lode(long label) {
        super(label);
        if (decodeLevel() == 0) {
            _children = new ArrayList<>();
            _children.add(null);
            setLinkShared(NEXT_NODE_BIT_INDEX, true);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        // TODO Auto-generated method stub
        if (getNextNode() != null) {
            linkedListCount--;
        }
        super.finalize();
    }

    @Override
    public String toString() {
        return String.format("Label=%d, Dim=%d, Level=%d, Count=%d", decodeLabel(), decodeDim(), decodeLevel(), getCount());
    }

    @Override
    public long decodeLabel() {
        return this.label >> 40;
    }

    @Override
    public int decodeDim() {
        return (int) (this.label >> 36 & 0xF);
    }

    @Override
    public int decodeLevel() {
        return (int) (this.label >> 28 & 0xFF);
    }

    @Override
    public int getCount() {
        return count;
//        return (int) (this.label & 0xFFFFF);
    }

    @Override
    public void setCount(int count) {
        this.count = count;
//        if (count > 0xFFFFF) {
//            throw new IllegalArgumentException("count[" + count + "] should smaller than 0XFFFFF!");
//        }
//        this.label = (this.label & ~((long)0XFFFFF) | count);
    }

    @Override
    public void updateCount() {
        int count = getChildrenCount();
        setCount(Math.max(count, 1));
    }

    @Override
    public int getChildrenCount() {
        var children = getChildren();
        int total = 0;
        if (children != null) {
            for (INode child : children) {
                total += ((ILode) child).getCount();
            }
        }
        return total;
    }

    @Override
    public ILode getLeftMostChild() {
        var children = getChildren();
        if (children != null && children.size() > 0) {
            return (ILode) children.get(0);
        }
        return null;
    }

    @Override
    public ILode getRightMostChild() {
        var children = getChildren();
        if (children != null && children.size() > 0) {
            return (ILode) children.get(children.size() - 1);
        }
        return null;
    }

    @Override
    public boolean isRootNode() {
        return decodeLevel() == 0;
    }

    @Override
    public ILink<?> getContent() {
        return (ILink<?>) super.getContent();
    }

    @Override
    public ILode getNextNode() {
        if (isRootNode()) {
            return (ILode) _children.get(0);
        }
        return null;
    }

    @Override
    public void setNextNode(ILink<ILode> nextContent) {
        if (getNextNode() == null && nextContent != null) {
            linkedListCount++;
        }
        if (getNextNode() != null && nextContent == null) {
            linkedListCount--;
        }
        if (isRootNode()) {
            if (nextContent != null) {
                _children.set(0, nextContent.getNode());
            } else {
                _children.set(0, null);
            }
        } else {
            throw new IllegalArgumentException("non root node");
        }
    }

    @Override
    public ILode shallowCopy() {
        var children = getChildren();
        ILode copy = new Lode(label);
        copy.setCount(getCount());
        copy.setSharedContentWithNode(this);
        if (children != null) {
            for (INode child : children) {
                copy.addChildNode(child, true);
            }
        }
        if (isRootNode()) {
            copy.setNextNode(this.getNextNode());
        }
        return copy;
    }

    @Override
    public Lode newProperChild(long user_label) {
        Lode childNode = new Lode(user_label, this.decodeDim(), this.decodeLevel() + 1);
        addChildNode(childNode, false);
        return childNode;
    }

    @Override
    public List<INode> getChildren() {
        return !isRootNode()? _children: _children.subList(1, _children.size());
    }
}
