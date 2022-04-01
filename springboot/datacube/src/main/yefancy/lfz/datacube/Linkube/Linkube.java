package main.yefancy.lfz.datacube.Linkube;

import main.yefancy.lfz.datacube.api.ICube;
import main.yefancy.lfz.datacube.api.IDataPoint;
import main.yefancy.lfz.datacube.api.IQueryEngine;
import main.yefancy.lfz.datacube.api.tree.IContent;
import main.yefancy.lfz.datacube.api.tree.ICuboid;
import main.yefancy.lfz.datacube.api.tree.INode;
import main.yefancy.lfz.datacube.api.tree.ISerialized;

import java.util.*;

public class Linkube<T extends ICuboidLink> implements ICube<T>, ISerialized {
    protected final ILode root;
    protected final Class<? extends T> clazz;
    protected final List<Set<Integer>> aggSplit;
    protected final IQueryEngine queryEngine;

    public Linkube(Class<? extends T> clazz, List<Set<Integer>> aggSplit) {
        root = new Lode(0, 0, 0);
        this.clazz = clazz;
        this.aggSplit = aggSplit;
        queryEngine = new QueryEngine(root, clazz);
    }

    public Linkube(Class<? extends T> clazz, int[][] aggSplit) {
        root = new Lode(0, 0, 0);
        this.clazz = clazz;
        this.aggSplit = new ArrayList<>();
        for (int[] ints : aggSplit) {
            Set<Integer> set = new HashSet<>();
            for (int anInt : ints) {
                set.add(anInt);
            }
            this.aggSplit.add(set);
        }
        queryEngine = new QueryEngine(root, clazz);
    }

    public List<Set<Integer>> getAggSplit() {
        return aggSplit;
    }

    @Override
    public void insert(IDataPoint dataPoint) {
        add(root, dataPoint, 0, new HashSet<>());
    }

    @Override
    public IQueryEngine getQueryEngine() {
        return queryEngine;
    }

    protected boolean needToAggregate(Lode node) {
        // return true;
        return aggSplit.get(node.decodeDim()).contains(node.decodeLevel());
    }

    private void add(ILode root, IDataPoint dataPoint, int dimension, Set<IContent> updatedNodes) {
        List<Long> chain = dataPoint.getLabels().get(dimension);
        List<ILode> nodePathStack = trailProperPath(root, chain);

        boolean isCuboid = dimension == dataPoint.getDimension() - 1;
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
                    ICuboidLink cuboid = (ICuboidLink) pathNode.getContent();
                    cuboid.insert(dataPoint);
                } else {
                    add((ILode) pathNode.getContent(), dataPoint, dimension + 1, updatedNodes);
                }
                updatedNodes.add(pathNode.getContent());
            }
        }
    }

    protected Stack<ILode> buildStack(List<ILode> nodePathStack, int i) {
        Stack<ILode> stack = new Stack<>();
        for (int j = 0; j <= i; j++) {
            stack.push(nodePathStack.get(j));
        }
        return stack;
    }

    protected void remakeChain(Stack<ILode> nodePathStack, ILink content) {
        if (nodePathStack.size() < 2) {
            return;
        }
        ILode child = nodePathStack.pop();
        ILode node = nodePathStack.pop();
        int pop_size = 1;
        while (!needToAggregate((Lode) node)) {
            int index = node.getChildIndex(child);
            if (index < 1) {
                if (nodePathStack.isEmpty()) {
                    return;
                }
                child = node;
                node = nodePathStack.pop();
                pop_size++;
            } else {
                if (node.isChildIndexShared(index - 1)) {
                    ILode origin_node = node;
                    ILode copy = (ILode) node.getIndexChild(index - 1).shallowCopy();
                    node.replaceChild(copy);
                    nodePathStack.push(node);
                    nodePathStack.push(copy);
                    node = copy;
                    for (int k = 1; k < pop_size; k++) {
                        copy = node.getRightMostChild().shallowCopy();
                        node.replaceChild(copy);
                        nodePathStack.push(copy);
                        node = copy;
                    }
                    var copyContent = node.getContent().shallowCopy();
                    copyContent.insertNextNode(content);
                    node.setContent(false, copyContent);
                    remakeChain((Stack<ILode>) nodePathStack.clone(), copyContent);
                    nodePathStack.pop();
                    while (true) {
                        ILode lastNode = nodePathStack.pop();
                        lastNode.setSharedContentWithNode(lastNode.getLeftMostChild());
                        if (lastNode == origin_node) {
                            break;
                        }
                    }
                } else {
                    node = (ILode) node.getIndexChild(index - 1);
                    var lastContent = node.getContent();
                    for (int k = 1; k < node.getCount(); k++) {
                        lastContent = lastContent.getNextNode();
                    }
                    lastContent.insertNextNode(content);
                }
                break;
            }
        }
    }

    protected List<ILode> trailProperPath(ILode root, List<Long> chain) {
        List<ILode> stack = new ArrayList<>();
        stack.add(root);
        ILode node = root;
        for (Long label : chain) {
            ILode child = (ILode) node.getChild(label);
            if (child == null) {
                child = (ILode) node.newProperChild(label);
            } else if (node.isChildShared(label)) {
                ILode copy = child.shallowCopy();
                node.replaceChild(copy);
                child = copy;
            }
            stack.add(child);
            node = child;
        }
        return stack;
    }

    @Override
    public T query(List<List<Long>> query) {
        T result = getCuboidInstance();
        return queryDFS(root, result, query, 0);
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

    private T queryDFS(ILode rootNode, T result, List<List<Long>> query, int dimension) {
        if (dimension >= query.size()) {
            return result;
        }
        List<Long> chain = query.get(dimension);
        ILode node = rootNode;
        for (Long aLong : chain) {
            node = (ILode) node.getChild(aLong); //== null?node.getChild(0):node.getChild(chain.get(i));
            if (node == null) {
                return result;
            }
        }
        if (node == null) {
            for (Long deo : query.get(0)) {
                System.out.print(deo + "->");
            }
        }
        assert node != null;
        var content = node.getContent();
        if (dimension == query.size() - 1) {
            //queryCount += node.getCount();
            for (int count = 0; count < node.getCount(); count++) {
                result.merge((ICuboid) content);
                content = content.getNextNode();
            }
        } else {
            for (int count = 0; count < node.getCount(); count++) {
                queryDFS((ILode) content, result, query, dimension + 1);
                content = content.getNextNode();
            }
        }
        return result;
    }

    @Override
    public List<Long> serializedBasicTree() {
        List<Long> result = new ArrayList<>();
        serDFS(root, result);
        return result;
    }

    private void serDFS(INode root, List<Long> result) {
        result.add(root.getLabel());
        int size = root.getChildrenSize();
        if (size > 0) {
            result.add((long) root.getChildrenSize());
            for (INode child : root.getChildren()) {
                serDFS(child, result);
            }
        } else if (root.getContent() instanceof INode nextDim) {
            result.add(0L);
            serDFS(nextDim, result);
        } else {
            result.add(-1L);
        }
    }

    @Override
    public void deSerializedBasicTree(List<Long> data) {
        Iterator<Long> iterator = data.iterator();
        iterator.next();
        deSerDFS(root, iterator, 0, 0);
    }

    private void deSerDFS(INode root, Iterator<Long> iterator, int dim, int level) {
        long size = iterator.next();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                long label = iterator.next();
                INode child = root.getChild(label);
                if (child == null) {
                    child = new Lode(label, dim, level + 1);
                    root.addChildNode(child, false);
                }
                deSerDFS(child, iterator, dim, level + 1);
            }
        } else if (size == 0) {
            var content = root.getContent();
            if (content == null) {
                long label = iterator.next();
                content = new Lode(label, dim + 1, 0);
                root.setContent(false, content);
                deSerDFS((Lode)content, iterator, dim + 1, 0);
            }
        }
    }
}
