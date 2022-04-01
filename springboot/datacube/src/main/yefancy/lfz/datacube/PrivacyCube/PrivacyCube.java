package main.yefancy.lfz.datacube.PrivacyCube;

import main.yefancy.lfz.datacube.api.IQueryEngine;
import main.yefancy.lfz.datacube.api.tree.IContent;
import main.yefancy.lfz.datacube.api.utils.tuples.Tuple2;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class PrivacyCube{
    public final PNode root;
    public final List<Tuple2<String, List<Object>>> schemaMap;

    public PrivacyCube() {
        this(new ArrayList<>());
    }

    public PrivacyCube(List<Tuple2<String, List<Object>>> schemaMap) {
        root = new PNode(Set.of(0));
        this.schemaMap = schemaMap;
    }

    public WebNode getWebTree(List<Integer> schema, List<Integer> sensitives, DistanceMeasure distanceMeasure) {
        Stack<WebNode> path = new Stack<>();
        WebNode child = new WebNode();
        child.labels.add(-1);
        path.push(child);
        List<List<Set<Integer>>> queries = new ArrayList<>();
        for (int i = 0; i < schemaMap.size(); i++) {
            queries.add(new ArrayList<>());
        }
        List<double[]> globals = new ArrayList<>();
        var content = query(queries);
        for (Integer sensitive : sensitives) {
            globals.add(content.getDistribute(sensitive, schemaMap.get(sensitive).y.size()));
        }
        return dfsWebTree(schema, path, sensitives, globals, distanceMeasure);
    }

    private WebNode dfsWebTree(List<Integer> schema, Stack<WebNode> path, List<Integer> sensitives, List<double[]> globals, DistanceMeasure distanceMeasure) {
        WebNode root = path.peek();
        List<List<Set<Integer>>> queries = new ArrayList<>();
        for (int i = 0; i < schemaMap.size(); i++) {
            boolean flag = true;
            for (int j = 0; j < schema.size(); j++) {
                if (schema.get(j) == i && path.size() - 1 > j) {
                    List<Set<Integer>> query = new ArrayList<>();
                    query.add(path.get(j + 1).labels);
                    queries.add(query);
                    flag = false;
                }
            }
            if (flag) {
                queries.add(new ArrayList<>());
            }
        }
        root.content = query(queries);
        if (root.content == null || root.content.inValid()) {
            return null;
        }

        root.content.update_k_Anonymous();
        root.content.update_l_Diversity(sensitives);
        root.content.update_t_closeness(sensitives, globals, distanceMeasure);


        root.count = root.content.d.size();
        if (schema.size() == path.size() - 1) {
            root.branch = 1;
            return root;
        }
//        int size = schemaMap.get(schema.get(path.size() - 1)).y.size();
//        for (int i = 0; i < size; i++) {
//            WebNode child = new WebNode();
//            child.labels.add((Integer) i);
//            path.push(child);
//            child = dfsWebTree(schema, path, sensitives, globals, distanceMeasure);
//            path.pop();
//            if (child != null) {
//                root.children.add(child);
//            }
//        }

        PNode node = this.root;
        for (int i = 0; i < schema.get(path.size() - 1); i++) {
            node = (PNode) node.content;
        }
        for (PNode childNode : node.getChildren()) {
            WebNode child = new WebNode();
            child.labels = childNode.getLabel();
            path.push(child);
            child = dfsWebTree(schema, path, sensitives, globals, distanceMeasure);
            path.pop();
            if (child != null) {
                root.children.add(child);
            }
        }

        root.children.forEach(child->{
            root.branch += child.branch;
        });
        return root;
    }

    public int getCount(PNode root) {
        IContent content = root.getContent();
        while (content instanceof PNode node) {
            content = node.getContent();
        }
        return ((TableCuboid)content).d.size();
    }

    public void insert(TabularDataPoint dataPoint) {
        add(root, dataPoint, 0, new HashSet<>());
    }

    public IQueryEngine getQueryEngine() {
        return null;
    }

    public TableCuboid query(List<List<Set<Integer>>> query) {
        return queryDFS(root, query, 0);
    }

    private TableCuboid queryDFS(PNode rootNode, List<List<Set<Integer>>> query, int dimension) {
        if (dimension >= query.size())
            return null;
        List<Set<Integer>> chain = query.get(dimension);
        PNode node = rootNode;
        for (Set<Integer> aInteger : chain) {
            node = node.getChild(aInteger);
            if (node == null)
                return null;
        }
        IContent content = node.getContent();
        if (dimension == query.size() - 1)
            return (TableCuboid)content;
        else
            return queryDFS((PNode)content, query, dimension + 1);
    }

    public TableCuboid getCuboidInstance() {
        return new TableCuboid();
    }

    private void add(PNode root, TabularDataPoint dataPoint, int dimension, Set<IContent> updatedNodes){
        List<Set<Integer>> labels = dataPoint.getLabels().get(dimension);
        List<PNode> dimensionPathNodes = trailProperDimensionPath(root, labels, dimension);
        PNode child = null;
        // start with finest level ...
        for (int i = dimensionPathNodes.size() - 1; i >= 0; i--) {
            PNode pathNode = dimensionPathNodes.get(i);
            boolean isSum = dimension == dataPoint.getDimension() - 1;
            boolean update = processDimensionPathNode(pathNode, child, dimension, updatedNodes, isSum);
            if (update) {
                if (isSum) {
                    if (pathNode.getContent() instanceof TableCuboid cuboid)
                        cuboid.insert(dataPoint);
                    else
                        throw new IllegalArgumentException("error cuboid");
                } else {
                    if (pathNode.getContent() instanceof PNode content)
                        add(content, dataPoint, dimension + 1, updatedNodes);
                    else
                        throw new IllegalArgumentException("error content");
                }
                updatedNodes.add(pathNode.getContent());
            }
            child = pathNode;
        }
    }

    private boolean processDimensionPathNode(PNode node, PNode child, int dimension, Set<IContent> updatedNodes, boolean isSum) {
        if (node.getChildrenSize() == 1) {
            node.setSharedContentWithNode(child);
        } else if (node.getContent() == null) {
            node.setContent(false, createNewContent(dimension + 1, isSum));
            return true;
        } else if (node.isContentShared() && !updatedNodes.contains(node.getContent())) {
            var shallowCopy = node.getContent().shallowCopy();
            node.setContent(false, shallowCopy);
            return true;
        } else return !node.isContentShared();
        return false;
    }

    private IContent createNewContent(int dimension, boolean isSum) {
        if (isSum) {
            return getCuboidInstance();
        } else {
            return new PNode(Set.of(0));
        }
    }

    private List<PNode> trailProperDimensionPath(PNode root, List<Set<Integer>> labels, int dimension) {
        List<PNode> stack = new ArrayList<>();
        stack.add(root);
        PNode node = root;
        for (int i =0;i<labels.size();i++) {
            PNode child = getOrCreateProperChildNode(node, labels.get(i), dimension, i);
            stack.add(child);
            node = child;
        }
        return stack;
    }

    private PNode getOrCreateProperChildNode(PNode node, Set<Integer> label, int dimension, int level) {
        PNode child = node.getChild(label);
        if (child == null) {
            return node.newProperChild(label);
        } else if (node.isChildShared(label)) {
            PNode copy = (PNode) child.shallowCopy();
            node.replaceChild(copy);
            return copy;

        } else {
            return child;
        }
    }
}
