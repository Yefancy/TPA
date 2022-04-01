package main.yefancy.lfz.datacube.Nanocube;

import main.yefancy.lfz.datacube.api.ICube;
import main.yefancy.lfz.datacube.api.IDataPoint;
import main.yefancy.lfz.datacube.api.IQueryEngine;
import main.yefancy.lfz.datacube.api.tree.IContent;
import main.yefancy.lfz.datacube.api.tree.ICuboid;
import main.yefancy.lfz.datacube.api.tree.INode;
import main.yefancy.lfz.datacube.api.tree.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Nanocube<T extends ICuboid> implements ICube<T> {
    protected INode root;
    protected final Class<? extends T> clazz;

    public Nanocube(Class<? extends T> clazz) {
        root = new Node(0, 0, 0);
        this.clazz = clazz;
    }

    @Override
    public void insert(IDataPoint dataPoint) {
        add(root, dataPoint, 0, new HashSet<>());
    }

    @Override
    public IQueryEngine getQueryEngine() {
        return null;
    }

    @Override
    public T query(List<List<Long>> query) {
        return queryDFS(root, query, 0);
    }

    @Override
    public T getCuboidInstance() {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new IllegalArgumentException("error class");
        }
    }

    protected void add(INode root, IDataPoint dataPoint, int dimension, Set<IContent> updatedNodes){
        List<Long> labels = dataPoint.getLabels().get(dimension);
        List<INode> dimensionPathNodes = trailProperDimensionPath(root, labels, dimension);
        INode child = null;
        // start with finest level ...
        for (int i = dimensionPathNodes.size() - 1; i >= 0; i--) {
            INode pathNode = dimensionPathNodes .get(i);
            boolean isSum = dimension == dataPoint.getDimension() - 1;
            boolean update = processDimensionPathNode(pathNode, child, dimension, updatedNodes, isSum);
            if (update) {
                if (isSum) {
                    if (pathNode.getContent() instanceof ICuboid cuboid)
                        cuboid.insert(dataPoint);
                    else
                        throw new IllegalArgumentException("error cuboid");
                } else {
                    if (pathNode.getContent() instanceof INode content)
                        add(content, dataPoint, dimension + 1, updatedNodes);
                    else
                        throw new IllegalArgumentException("error content");
                }
                updatedNodes.add(pathNode.getContent());
            }
            child = pathNode;
        }
    }

    protected boolean processDimensionPathNode(INode node, INode child,
                                               int dimension,
                                               Set<IContent> updatedNodes,
                                               boolean isSum) {
        if (node.getChildrenSize() == 1) {
            node.setSharedContentWithNode(child);
        } else if (node.getContent() == null) {
            node.setContent(false, createNewContent(dimension, isSum));
            return true;
        } else if (node.isContentShared() && !updatedNodes.contains(node.getContent())) {
            var shallowCopy = node.getContent().shallowCopy();
            node.setContent(false, shallowCopy);
            return true;
        } else return !node.isContentShared();
        return false;
    }

    protected IContent createNewContent(int dimension, boolean isSum) {
        if (isSum) {
            return getCuboidInstance();
        } else {
            return new Node(0, dimension, 0);
        }
    }

    protected List<INode> trailProperDimensionPath(INode root,
                                                   List<Long> labels,
                                                   int dimension) {
        List<INode> stack = new ArrayList<>();
        stack.add(root);
        INode node = root;
        for (int i =0;i<labels.size();i++) {
            INode child = getOrCreateProperChildNode(node, labels.get(i), dimension, i);
            stack.add(child);
            node = child;
        }
        return stack;
    }

    protected INode getOrCreateProperChildNode(INode node, Long label, int dimension, int level) {
        INode child = node.getChild(label);
        if (child == null) {
            return node.newProperChild(label);
        } else if (node.isChildShared(label)) {
            Node copy = (Node) child.shallowCopy();
            node.replaceChild(copy);
            return copy;

        } else {
            return child;
        }
    }

    private T queryDFS(INode rootNode, List<List<Long>> query, int dimension) {
        if (dimension >= query.size())
            return null;
        List<Long> chain = query.get(dimension);
        INode node = rootNode;
        for (long aInteger : chain) {
            node = node.getChild(aInteger);
            if (node == null)
                return null;
        }
        IContent content = node.getContent();
        if (dimension == query.size() - 1)
            return (T)content;
        else
            return queryDFS((INode)content, query, dimension + 1);
    }

}
