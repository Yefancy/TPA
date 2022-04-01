package main.yefancy.lfz.datacube.DPVcube;

import main.yefancy.lfz.datacube.Linkube.*;
import main.yefancy.lfz.datacube.api.tree.IContent;

import java.util.*;

public class ACube<T extends ICuboidLink> extends Linkube<T> {

    public ACube(Class<? extends T> clazz) {
        super(clazz, new ArrayList<>());
    }

    @Override
    protected boolean needToAggregate(Lode node) {
        return true;
        // return aggSplit.get(node.decodeDim()).contains(node.decodeLevel());
    }

    @Override
    public void deSerializedBasicTree(List<Long> data) {
        Iterator<Long> iterator = data.iterator();
        iterator.next();
        List<List<Long>> path = new ArrayList<>();
        path.add(new ArrayList<>());
        //path.get(0).add(root.getLabel());
        deSerDFS(root, path, iterator, 0, 0, false);
    }

    private void deSerDFS(ILode root, List<List<Long>> paths, Iterator<Long> iterator, int dim, int level, boolean needAdd) {
        long size = iterator.next();
        if (size > 0) {
            List<Long> path = paths.get(dim);
            for (int i = 0; i < size; i++) {
                long label = iterator.next();
                ILode child = (ILode) root.getChild(label);
                if (child == null) {
                    child = new Lode(label, dim, level + 1);
                    root.addChildNode(child, false);
                    needAdd = true;
                }
                path.add(child.getLabel());
                deSerDFS(child, paths, iterator, dim, level + 1, needAdd);
                path.remove(path.size()-1);
            }
        } else if (size == 0) {
            var content = root.getContent();
            long label = iterator.next();
            if (content == null) {
                content = new Lode(label, dim + 1, 0);
                root.setContent(false, content);
                needAdd = true;
            }
                List<Long> newList = new ArrayList<>();
                //newList.add(((ILode)content).getLabel());
                paths.add(newList);
                deSerDFS((ILode)content, paths, iterator, dim + 1, 0, needAdd);
                paths.remove(paths.size() - 1);
        } else if(needAdd) {
            add(this.root, paths, 0, new HashSet<>());
        }
    }

    private void add(ILode root, List<List<Long>> paths, int dimension, Set<IContent> updatedNodes) {
        List<ILode> nodePathStack = trailProperPath(root, paths.get(dimension));
        boolean isCuboid = dimension == paths.size() - 1;
        for (int i = nodePathStack.size() - 1; i >= 0; i--) {
            ILode pathNode = nodePathStack.get(i);
            ////////////////////////////////////////////////
            boolean update = false;
            if (needToAggregate((Lode) pathNode)) {
                if (pathNode.getChildrenSize() == 1 && pathNode.getChildrenCount() == 1) {
                    pathNode.setSharedContentWithNode(pathNode.getLeftMostChild());
                    remakeChain(buildStack(nodePathStack, i), pathNode.getContent());
                } else if (pathNode.getContent() == null) {
                    ILink<?> tmpRN;
                    if (isCuboid) {
                        tmpRN = getCuboidInstance();
                    } else {
                        tmpRN = new Lode(0, dimension + 1, 0);
                    }
                    pathNode.setContent(false, tmpRN);
                    remakeChain(buildStack(nodePathStack, i), tmpRN);
                    update = true;
                } else if (pathNode.isContentShared() && !updatedNodes.contains(pathNode.getContent())) {
                    ILink origin = pathNode.getContent();
                    if (pathNode.getChildrenSize() > 0 && !needToAggregate((Lode) pathNode)) {
                        origin = pathNode.getLeftMostChild().getContent();
                    }
                    var copy = origin.shallowCopy();
                    if (pathNode.getChildrenSize() > 0) {
                        for (int j = pathNode.getLeftMostChild().getCount(); j > 1; j--) {
                            origin = origin.getNextNode();
                        }
                        copy.setNextNode(origin.getNextNode());
                        origin.setNextNode(null);
                    }
                    pathNode.setContent(false, copy);
                    remakeChain(buildStack(nodePathStack, i), copy);
                    update = true;
                } else if (!pathNode.isContentShared()) {
                    update = true;
                }
            } else if (pathNode.getChildrenSize() >= 1) {
                pathNode.updateCount();
                pathNode.setSharedContentWithNode(pathNode.getLeftMostChild());
            }
            //////////////////////////////////////////////////
            if (update) {
                if (isCuboid) {
                    //ICuboidLink cuboid = (ICuboidLink) pathNode.getContent();
                    //TODO
                    //cuboid.insert(null);
                } else {
                    add((ILode)pathNode.getContent() ,paths, dimension + 1, updatedNodes);
                }
                updatedNodes.add(pathNode.getContent());
            }
        }
    }
}
