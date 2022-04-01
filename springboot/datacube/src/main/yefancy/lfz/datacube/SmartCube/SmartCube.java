package main.yefancy.lfz.datacube.SmartCube;

import main.yefancy.lfz.datacube.Nanocube.Nanocube;
import main.yefancy.lfz.datacube.api.IDataPoint;
import main.yefancy.lfz.datacube.api.tree.IContent;
import main.yefancy.lfz.datacube.api.tree.ICuboid;
import main.yefancy.lfz.datacube.api.tree.INode;
import main.yefancy.lfz.datacube.api.utils.tuples.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class SmartCube<T extends ICuboid> extends Nanocube<T> {
    public List<List<Integer>> cuboids = new ArrayList<>();

    public SmartCube(Class<? extends T> clazz, int[] config) {
        super(clazz);
        this.root = new SMNode(-1, 0, 0);
        cuboids.add(Arrays.stream(config).boxed().collect(Collectors.toList()));
    }

    @Override
    protected IContent createNewContent(int dimension, boolean isSum) {
        if (isSum) {
            return getCuboidInstance();
        } else {
            return new SMNode(-1, dimension, 0);
        }
    }

    @Override
    protected void add(INode root, IDataPoint dataPoint, int dimension, Set<IContent> updatedNodes) {
        List<Long> labels = dataPoint.getLabels().get(dimension);
        List<INode> dimensionPathNodes = trailProperDimensionPath(root, labels, dimension);
        INode child = null;
        // start with finest level ...
        int size = dimensionPathNodes.size() - 1;
        for (int i = size; i >= 0; i--) {
            INode pathNode = dimensionPathNodes.get(i);
            if (i == size || pathNode.getContent() != null) {
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
    }

    public void updateCuboid(List<List<Long>> query) {
        List<Integer> cuboid = query.stream().map(List::size).collect(Collectors.toList());
        if (!cuboids.contains(cuboid)) {
            addCuboid(cuboid);
        }
    }

    @Override
    public T query(List<List<Long>> query) {
        T result = getCuboidInstance();
        queryDFS(root, result, query, 0);
        updateCuboid(query);
        return result;
    }

    protected T queryDFS(INode rootNode, T result, List<List<Long>> query, int dimension) {
        if (dimension >= query.size())
            return result;
        List<Long> chain = query.get(dimension);
        INode node = rootNode;
        for (long aInteger : chain) {
            node = node.getChild(aInteger);
            if (node == null)
                return result;
        }
        return checkContentNull(result, node, dimension, query);
    }

    private T checkContentNull(T result, INode root, int dimension, List<List<Long>> query) {
        IContent content = root.getContent();
        if (content == null) {
            if(root.getChildren() == null) {
//                System.out.println("????");
                return result;
            }
            for (INode child : root.getChildren()) {
                checkContentNull(result, child, dimension, query);
            }
            return result;
        } else {
            if (dimension == query.size() - 1)
                result.merge((ICuboid) content);
            else
                return queryDFS((INode)content, result, query, dimension + 1);
        }
        return result;
    }

    public void addCuboid(List<Integer> schema) {
        addCuboid(root, schema, cuboids.get(0));
        cuboids.add(schema);
    }

    private void addCuboid(INode root, List<Integer> new_schema, List<Integer> basic_schema) {
        List<IContent> nodes = new ArrayList<>();
        List<List<IContent>> candidates = new ArrayList<>();
        nodes.add(root);
        candidates.add(new ArrayList<>());
        candidates.get(0).add(root);

        for(int i=0;i<new_schema.size()-1;i++) {
            var back = addCuboidChildren(nodes,candidates,0,new_schema.get(i));
            List<IContent> next_nodes = back.x;
            List<List<IContent>> next_candi = back.y;
            nodes.clear();
            candidates.clear();
            for(int k=0;k<next_nodes.size();k++) {
                SMNode node = (SMNode) next_nodes.get(k);
                List<IContent> candi = next_candi.get(k);
                Stack<IContent> children = new Stack<>();
                for(int j=0;j<candi.size();j++) {
                    int depth = basic_schema.get(i)-new_schema.get(i);
                    getChildrenRec(candi.get(j), depth, children);
                }
                if(node.getContent() == null) {
                    if(children.size() == 1)
                        node.setSharedContentWithNode((SMNode) children.get(0));
                    else
                        node.setContent(false, new SMNode(-1, node.decodeDim() + 1, 0, 0));
//                        node.setContent(false, createNewContent(node.decodeDim(), false));
                }
                incEdgeRef(node, node.getContent());
                nodes.add(node.getContent());
                List<IContent> candidate = new ArrayList<>();
                for (IContent child : children)
                    candidate.add(((SMNode) child).getContent());
                candidates.add(candidate);
            }
        }
        for(int i=0;i<nodes.size();i++) {
            incRef((SMNode)nodes.get(i));
            updateContent((SMNode)nodes.get(i),candidates.get(i));
        }
    }

    private Tuple2<List<IContent>, List<List<IContent>>> addCuboidChildren(List<IContent> nodes, List<List<IContent>> candidates, int depth, int new_layer) {
        Tuple2<List<IContent>, List<List<IContent>>> back = new Tuple2<>(null, null);
        if(depth == new_layer) {
            for(IContent node: nodes)
                incRef((SMNode)node);
            back.x = nodes;
            back.y = candidates;
            return back;
        }
        List<IContent> nnodes = new ArrayList<>();
        List<List<IContent>> ncandi = new ArrayList<>();
        for(int i = 0;i<nodes.size();i++) {
            SMNode node = (SMNode) nodes.get(i);
            incRef(node);
            List<IContent> children = new ArrayList<>();
            for(IContent content:candidates.get(i))
                children.addAll(((SMNode)content).getChildren());
            HashMap<Long,List<IContent>> nchildren = new HashMap<>();
            for(IContent child : children) {
                if(!nchildren.containsKey(((SMNode)child).getLabel()))
                    nchildren.put(((SMNode)child).getLabel(), new ArrayList<>());
                nchildren.get(((SMNode)child).getLabel()).add(child);
            }
            for(Map.Entry<Long, List<IContent>> entry : nchildren.entrySet()) {
                long key = entry.getKey();
                children = entry.getValue();
                SMNode child = (SMNode) node.getChild(key);
                if(child==null)
                    if(children.size()==1)
                        child = (SMNode) node.addChildNode((INode) children.get(0), true);
                    else
                        child = (SMNode) node.addChildNode(new SMNode(key, node.decodeDim(), node.decodeLevel() +1, 0), false);
                incEdgeRef(node, node.getChild(key));
                nnodes.add(child);
                ncandi.add(children);
            }
        }
        return addCuboidChildren(nnodes, ncandi, depth+1, new_layer);
    }

    public void deleteCuboid(List<Integer> cuboid) {
        deleteDFS(root, 0, cuboid, 0);
    }

    private void deleteDFS(IContent rootContent, int dimension, List<Integer> cuboid, int depth) {
        SMNode root = (SMNode)rootContent;
        if(depth == cuboid.get(dimension) && dimension <  cuboid.size()-1) {
            deleteDFS(root.getContent(), dimension+1, cuboid, 0);
            if(((SMNode)root.getContent()).ASN == 0)
                root.setContent(false, null);
        } else {
            for(int i=0;i<root.getChildren().size();i++) {
                SMNode child = (SMNode) root.getChildren().get(i);
                deleteDFS(child, dimension, cuboid, depth+1);
                if(child.ASN == 0) {
                    root.getChildren().remove(child);
                    i--;
                }
            }
        }
        root.ASN--;
    }

    private void getChildrenRec(IContent content, int depth, Stack<IContent> children) {
        if(depth == 0) {
            children.add(content);
            return;
        }
        if (content != null && ((SMNode)content).getChildren() != null) {
            for(INode child: ((SMNode)content).getChildren())
                getChildrenRec(child, depth-1,children);
        }
    }

    private void incEdgeRef(SMNode node, IContent content) {
        //TODO
        //node.ASN++;
        //((SMNode)content).ASN--;
    }

    private void incRef(SMNode node) {
        node.ASN++;
    }

    private void updateContent(SMNode node, List<IContent> candidate){
        ICuboid content = getCuboidInstance();
        for(IContent last_node: candidate) {
            ICuboid cuboid = (ICuboid)((SMNode)last_node).getContent();
            content.merge(cuboid);
        }
        node.setContent(false, content);
    }

    public long calculateLatency(List<Integer> schema) {
//        long latency = 1;
//        for(int d = 0;d<schema.size();d++) {
//            int level = schema.get(d);
//            for(int l = 0;l<level-1;l++) {
//                int count = nodeCount.getOrDefault(d+"-"+l, 1);
//                int branch = nodeBranch.get(d+"-"+l);
//                latency *= branch/count;
//            }
//        }
//        return latency;
        return 0;
    }
}
