package main.yefancy.lfz.datacube.Linkube;

import main.yefancy.lfz.datacube.api.tree.IContent;

public interface ILink<T extends ILink<T>> extends IContent {

    default T getNode() {
        return (T) this;
    }
    boolean isRootNode();
    ILink<T> getNextNode();
    void setNextNode(ILink<T> nextContent);
    default void insertNextNode(ILink<T> nextContent){
        if (nextContent == null)
            return;
        if (nextContent.getNextNode() == null)
            nextContent.setNextNode(this.getNextNode());
        setNextNode(nextContent);
    }
    @Override
    ILink<T> shallowCopy();
}
