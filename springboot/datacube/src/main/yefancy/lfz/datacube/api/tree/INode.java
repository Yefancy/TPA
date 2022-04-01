package main.yefancy.lfz.datacube.api.tree;

import java.util.List;

public interface INode extends IContent{
    List<INode> getChildren();
    IContent getContent();
    long getLabel();
    INode addChildNode(INode childNode, boolean shared);
    INode newProperChild(long label);
    boolean isChildShared(long label);
    boolean isChildIndexShared(int childIndex);
    int getChildIndex(INode node);
    INode getChild(long label);
    INode getIndexChild(int index);
    void replaceChild(INode newChild);
    void setSharedContentWithNode(INode node);
    void setContent(boolean shared, IContent content);
    boolean isContentShared();
    int getChildrenSize();
    int decodeDim();
    int decodeLevel();
}
