package main.yefancy.lfz.datacube.api.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Node implements INode {
    private static final int CONTENT_SHARED_BIT_INDEX = 0;
    public static long nodeCount = 0;

    public long label;

    protected List<INode> _children = null;
    protected IContent content = null;

    private Map<Integer, Boolean> sharedMap = new SharedLinkMap();

    public Node(long user_label, int dim, int level) {
        this(user_label << 16 | dim << 8 | level);
        if (dim == 255) {
            System.out.println("???");
        }
        if (dim > 0XFF || level > 0XFF)
            throw new IllegalArgumentException("dim and level should smaller than 0XFF!");
    }

    protected Node(long label) {
        nodeCount++;
        this.label = label;
    }

    public long decodeLabel() {
        return this.label >> 16;
    }

    public int decodeDim() {
        return (int) (this.label >> 8 & 0xFF);
    }

    public int decodeLevel() {
        return (int) (this.label & 0xFF);
    }

    @Override
    protected void finalize() throws Throwable {
        nodeCount--;
        super.finalize();
    }

    @Override
    public long getLabel(){
        return decodeLabel();
    }

    @Override
    public String toString() {
        return String.format("Label=%d, Dim=%d, Level=%d", decodeLabel(), decodeDim(), decodeLevel());
    }

    @Override
    public Node newProperChild(long user_label) {
        Node childNode = new Node(user_label, this.decodeDim(), this.decodeLevel() + 1);
        addChildNode(childNode, false);
        return childNode;
    }

    @Override
    public INode addChildNode(INode childNode, boolean shared) {
        if (_children == null)
            _children = new ArrayList<>();
        _children.add(childNode);
        setLinkShared(_children.size(), shared);
        return childNode;
    }

    @Override
    public int getChildrenSize() {
        var children = getChildren();
        return children == null? 0 : children.size();
    }

    @Override
    public boolean isChildShared(long user_label) {
        int childIndex = getChildIndex(user_label);
        return isLinkShared(childIndex + 1);
    }

    @Override
    public boolean isChildIndexShared(int childIndex) {
        return isLinkShared(childIndex + 1);
    }

    @Override
    public int getChildIndex(INode node) {
        var children = getChildren();
        if (children == null) return -1;
        for (int i = 0; i < children.size(); i++)
            if (node == children.get(i))
                return i;
        return -1;
    }

    @Override
    public INode getChild(long user_label) {
        var children = getChildren();
        if (children == null) return null;
        for (INode child : children) {
            if (child != null && child.getLabel() == user_label) {
                return child;
            }
        }
        return null;
    }

    @Override
    public INode getIndexChild(int index) {
        var children = getChildren();
        if (children == null) return null;
        if (index >= children.size())
            return null;
        return children.get(index);
    }

    @Override
    public void replaceChild(INode newChild) {
        var children = getChildren();
        if (children == null) return;
        int childIndex = getChildIndex(newChild.getLabel());
        children.set(childIndex, newChild);
        setLinkShared(childIndex + 1, false);
    }

    @Override
    public void setSharedContentWithNode(INode node) {
        IContent nodeContent = node.getContent();
        setContent(true, nodeContent);
    }

    @Override
    public void setContent(boolean shared, IContent content) {
        this.content = content;
        setLinkShared(CONTENT_SHARED_BIT_INDEX, shared);
    }

    @Override
    public boolean isContentShared() {
        if (content == null) {
            throw new IllegalStateException("There is no content");
        }
        return isLinkShared(CONTENT_SHARED_BIT_INDEX);
    }

    @Override
    public IContent getContent() {
        return content;
    }

    @Override
    public List<INode> getChildren(){
        return _children == null ? new ArrayList<>() : _children;
    }

    @Override
    public INode shallowCopy() {
        var children = getChildren();
        INode copy = new Node(label);
        copy.setSharedContentWithNode(this);
        if (children != null) {
            for (INode child : children) {
                copy.addChildNode(child, true);
            }
        }
        return copy;
    }

    private int getChildIndex(long user_label) {
        var children = getChildren();
        if (children == null) return -1;
        for (int i = 0; i < children.size(); i++) {
            INode node = children.get(i);
            if (node != null && node.getLabel() == user_label) {
                return i;
            }
        }
        return -1;
    }

    protected boolean isLinkShared(int linkIndex) {
//        validateLinkIndex(linkIndex);
//        return (sharedLinkBitSet & (1L << linkIndex)) != 0;
        return sharedMap.getOrDefault(linkIndex, false);
    }

    protected void setLinkShared(int linkIndex, boolean shared) {
//        validateLinkIndex(linkIndex);
//        if (shared) {
//            sharedLinkBitSet |= (1L << linkIndex);
//        } else {
//            sharedLinkBitSet &= ~(1L << linkIndex);
//        }
        sharedMap.put(linkIndex, shared);
    }

    private void validateLinkIndex(int linkIndex) {
        if (linkIndex > 32) {
            throw new IllegalArgumentException("Link index cannot be larger than 32");
        }
    }
}
