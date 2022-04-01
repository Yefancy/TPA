package main.yefancy.lfz.datacube.PrivacyCube;

import main.yefancy.lfz.datacube.api.tree.IContent;
import main.yefancy.lfz.datacube.api.tree.SharedLinkMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PNode implements IContent {
    private static final int CONTENT_SHARED_BIT_INDEX = 0;
    public static Integer nodeCount = 0;

    public Set<Integer> label;

    protected List<PNode> _children = null;
    protected IContent content = null;

    private final Map<Integer, Boolean> sharedMap = new SharedLinkMap();

    protected PNode(Set<Integer> label) {
        nodeCount++;
        this.label = label;
    }

    protected void finalize() throws Throwable {
        nodeCount--;
        super.finalize();
    }

    public Set<Integer> getLabel(){
        return label;
    }

    public PNode newProperChild(Set<Integer> user_label) {
        PNode childNode = new PNode(user_label);
        addChildNode(childNode, false);
        return childNode;
    }

    public void addChildNode(PNode childNode, boolean shared) {
        if (_children == null)
            _children = new ArrayList<>();
        _children.add(childNode);
        setLinkShared(_children.size(), shared);
    }

    public int getChildrenSize() {
        var children = getChildren();
        return children == null? 0 : children.size();
    }

    public boolean isChildShared(Set<Integer> user_label) {
        int childIndex = getChildIndex(user_label);
        return isLinkShared(childIndex + 1);
    }

    public boolean isChildIndexShared(int childIndex) {
        return isLinkShared(childIndex + 1);
    }

    public int getChildIndex(PNode node) {
        var children = getChildren();
        if (children == null) return -1;
        for (int i = 0; i < children.size(); i++)
            if (node == children.get(i))
                return i;
        return -1;
    }

    public PNode getChild(Set<Integer> user_label) {
        var children = getChildren();
        if (children == null) return null;
        for (PNode child : children) {
            if (child != null && child.getLabel().equals(user_label)) {
                return child;
            }
        }
        return null;
    }

    public PNode getIndexChild(int index) {
        var children = getChildren();
        if (children == null) return null;
        if (index >= children.size())
            return null;
        return children.get(index);
    }

    public void replaceChild(PNode newChild) {
        var children = getChildren();
        if (children == null) return;
        int childIndex = getChildIndex(newChild.getLabel());
        children.set(childIndex, newChild);
        setLinkShared(childIndex + 1, false);
    }

    public void setSharedContentWithNode(PNode node) {
        IContent nodeContent = node.getContent();
        setContent(true, nodeContent);
    }

    public void setContent(boolean shared, IContent content) {
        this.content = content;
        setLinkShared(CONTENT_SHARED_BIT_INDEX, shared);
    }

    public boolean isContentShared() {
        if (content == null) {
            throw new IllegalStateException("There is no content");
        }
        return isLinkShared(CONTENT_SHARED_BIT_INDEX);
    }

    public IContent getContent() {
        return content;
    }

    public List<PNode> getChildren(){
        return _children;
    }

    public PNode shallowCopy() {
        var children = getChildren();
        PNode copy = new PNode(label);
        copy.setSharedContentWithNode(this);
        if (children != null) {
            for (PNode child : children) {
                copy.addChildNode(child, true);
            }
        }
        return copy;
    }

    private int getChildIndex(Set<Integer> user_label) {
        var children = getChildren();
        if (children == null) return -1;
        for (int i = 0; i < children.size(); i++) {
            PNode node = children.get(i);
            if (node != null && node.getLabel().equals(user_label)) {
                return i;
            }
        }
        return -1;
    }

    protected boolean isLinkShared(int linkIndex) {
        return sharedMap.getOrDefault(linkIndex, false);
    }

    protected void setLinkShared(int linkIndex, boolean shared) {
        sharedMap.put(linkIndex, shared);
    }
}
