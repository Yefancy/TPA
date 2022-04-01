package main.yefancy.lfz.datacube.PrivacyCube.Operators;

import main.yefancy.lfz.datacube.PrivacyCube.TableCuboid;
import main.yefancy.lfz.datacube.PrivacyCube.WebNode;
import main.yefancy.lfz.datacube.api.utils.tuples.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

public class MergeOP implements IOperator{
    public Map<Integer, Set<Integer>> filters;
    public int dim;
    public Set<Integer> label1;
    public Set<Integer> label2;

    private WebNode mergeNode(List<WebNode> toMerged, int dim, List<Integer> schema){
        WebNode back = new WebNode();
        if (schema.size() <= dim) {
            TableCuboid content = new TableCuboid();
            for (WebNode webNode : toMerged) {
                back.labels.addAll(webNode.labels);
                content.merge(webNode.content);
                webNode.branch = 0;
            }
            back.content = content;
            back.updateData();
            back.branch = 1;
        } else {
            int childDim = schema.get(dim);
            Map<Set<Integer>, List<Tuple2<WebNode, WebNode>>> matched = new HashMap<>();
            Set<WebNode> updateNode = new HashSet<>();
            toMerged.forEach(nodes-> nodes.children.forEach(child->{
                if (filters.containsKey(childDim)) {
                    if(child.labels.equals(filters.get(childDim))) {
                        if (!matched.containsKey(child.labels)) {
                            matched.put(child.labels, new ArrayList<>());
                        }
                        matched.get(child.labels).add(new Tuple2<>(nodes, child));
                    }
                } else {
                    if (!matched.containsKey(child.labels)) {
                        matched.put(child.labels, new ArrayList<>());
                    }
                    matched.get(child.labels).add(new Tuple2<>(nodes, child));
                }
            }));
            matched.forEach((key,listNode) ->{
                if (listNode.size() > 1){
                    var backChild = mergeNode(listNode.stream().map(node->node.y).collect(Collectors.toList()), dim + 1, schema);
                    if(backChild != null) {
                        back.children.add(backChild);
                        listNode.forEach(parent -> {
                            if (parent.y.branch == 0)
                                parent.x.children.remove(parent.y);
                            updateNode.add(parent.x);
                        });
                    }
                } else {
                    var pair = listNode.get(0);
                    back.children.add(pair.y);
                    pair.x.children.remove(pair.y);
                    updateNode.add(pair.x);
                }
            });
            updateNode.forEach(webNode -> {
                back.labels.addAll(webNode.labels);
                TableCuboid content = new TableCuboid();
                webNode.children.forEach(child->content.merge(child.content));
                webNode.content = content;
                webNode.updateData();
            });

            TableCuboid content = new TableCuboid();
            back.children.forEach(child->content.merge(child.content));
            back.content = content;
            back.updateData();
        }
        return back.branch == 0 ? null:back;
    }

    @Override
    public boolean execute(WebNode root, List<Integer> schema) {
        if (!schema.contains(dim)) return false;
        if (!schema.containsAll(filters.keySet())) return false;
        Stack<Tuple2<WebNode, Integer>> stack = new Stack<>();
        stack.push(new Tuple2<>(root, 0));
        while(!stack.isEmpty()) {
            var pair = stack.pop();
            if (schema.get(pair.y) == dim) {
                List<WebNode> toMerged = new ArrayList<>();
                WebNode parent = pair.x;
                for (int i = 0; i < parent.children.size(); i++) {
                    WebNode child = parent.children.get(i);
                    if(label1.equals(child.labels) || label2.equals(child.labels)) {
                        toMerged.add(child);
                        if (toMerged.size() == 2) {
                           break;
                        }
                    }
                }
                if (toMerged.size() > 1) {
                    WebNode backChild = mergeNode(toMerged, pair.y + 1, schema);
                    if(backChild != null) {
                        parent.children.add(backChild);
                        parent.children.removeIf(child->child.branch == 0);
                        TableCuboid content = new TableCuboid();
                        parent.children.forEach(child->content.merge(child.content));
                        parent.content = content;
                        parent.updateData();
                    }
                }

            } else {
                var labels = filters.get(schema.get(pair.y));
                if (labels == null){
                    pair.x.children.forEach(child->stack.push(new Tuple2<>(child, pair.y + 1)));
                } else {
                    pair.x.children.forEach(child->{
                        if (child.labels.equals(labels)) {
                            stack.push(new Tuple2<>(child, pair.y + 1));
                        }
                    });
                }
            }
        }
        return true;
    }
}
