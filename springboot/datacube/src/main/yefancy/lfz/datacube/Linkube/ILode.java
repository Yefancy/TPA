package main.yefancy.lfz.datacube.Linkube;

import main.yefancy.lfz.datacube.api.tree.INode;


public interface ILode extends INode, ILink<ILode> {
    int getCount();
    void setCount(int count);
    void updateCount();
    int getChildrenCount();
    ILode getLeftMostChild();
    ILode getRightMostChild();
    ILink<?> getContent();
    @Override
    ILode getNextNode();
    @Override
    ILode shallowCopy();
}
