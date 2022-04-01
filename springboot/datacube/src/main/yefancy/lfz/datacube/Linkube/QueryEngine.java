package main.yefancy.lfz.datacube.Linkube;

import main.yefancy.lfz.datacube.api.IQueryEngine;
import main.yefancy.lfz.datacube.api.tree.ICuboid;
import main.yefancy.lfz.datacube.api.tree.INode;
import main.yefancy.lfz.datacube.api.utils.tuples.Tuple2;
import main.yefancy.lfz.datacube.api.utils.tuples.Tuple3;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class QueryEngine implements IQueryEngine {
    private final ILode root;
    private final Class<? extends ICuboidLink> clazz;
    private long spatial,temporal,categorical;

    public QueryEngine(ILode root, Class<? extends ICuboidLink> clazz) {
        this.root = root;
        this.clazz = clazz;
    }

    public long getLastSpatialTime() {
        if (categorical == 0)
            return spatial - temporal;
        return spatial - categorical;
    }

    public long getLastCategoricalTime() {
        if (categorical == 0)
            return 0;
        return categorical - temporal;
    }

    public long getLastTemporalTime() {
        return temporal;
    }

    @Override
    public List<Tuple3<Double, Double, Integer>> queryMap(double ltLat, double ltLon, double rbLat, double rbLon, int zoom, List<List<Long>> cate,
                                  Date from, Date to) {
        List<Tuple3<Double, Double, Integer>> nodes = new ArrayList<>();
        Stack<ILode> geo = new Stack<>();
        geo.push(root);
        spatial = 0;
        temporal = 0;
        categorical = 0;
        long st = System.nanoTime();
        queryGeoDFS(nodes, geo,(long) ((ltLat + 89) * 10000000), (long) ((ltLon + 179) * 10000000),
                (long) ((rbLat + 89) * 10000000), (long) ((rbLon + 179) * 10000000), zoom, cate, from, to);
        spatial += System.nanoTime() - st;
        return nodes;
    }

    private void queryGeoDFS(List<Tuple3<Double, Double, Integer>> list, Stack<ILode> geo,long ltLat, long ltLon, long rbLat, long rbLon, int zoom, List<List<Long>> cate,
                             Date from, Date to) {
        if (geo.size() - 1 == zoom) {
            ICuboidLink cuboid = null;
            try {
                cuboid = clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            var content = geo.peek().getContent();
            for (int count = 0; count < geo.peek().getCount(); count++) {
                long st = System.nanoTime();
                if (cate.size() == 0) {
                    queryMinTimeDFS((ILode) content, cuboid, from, to);
                    temporal += System.nanoTime() - st;
                }
                else {
                    queryCateDFS((ILode) content, cuboid, cate, 0, from, to);
                    categorical += System.nanoTime() - st;
                }
                content = content.getNextNode();
            }
            int count = cuboid.result();
            if (count == 0)
                return;
            list.add(getGeo(geo, count));
        } else {
            for (INode node : geo.peek().getChildren()) {
                geo.push((ILode)node);
                if (checkGeoAvailable(geo, ltLat, ltLon, rbLat, rbLon) != 1) {
                    queryGeoDFS(list, geo, ltLat, ltLon, rbLat, rbLon, zoom, cate, from, to);
                }
                geo.pop();
            }
        }
    }

    private void queryCateDFS(ILode rootNode, ICuboidLink result, List<List<Long>> query, int dimension, Date from, Date to) {
        if (dimension >= query.size()) {
            return;
        }
        List<Long> chain = query.get(dimension);
        ILode node = rootNode;
        for (Long aLong : chain) {
            node = (ILode) node.getChild(aLong); //== null?node.getChild(0):node.getChild(chain.get(i));
            if (node == null) {
                return;
            }
        }
        var content = node.getContent();
        if (dimension == query.size() - 1) {
            for (int count = 0; count < node.getCount(); count++) {
                long st = System.nanoTime();
                queryMinTimeDFS((ILode) content, result, from, to);
                temporal += System.nanoTime() - st;
                content = content.getNextNode();
            }
        }
        else
            for (int count = 0; count < node.getCount(); count++) {
                queryCateDFS((ILode) content, result, query, dimension + 1, from, to);
                content = content.getNextNode();
            }
    }

    private void queryMinTimeDFS(ILode rootNode, ICuboidLink result, Date from, Date to) {
        for (INode _node : rootNode.getChildren()) {
            ILode node = (ILode)_node;
            if(node.decodeLevel() == 1) {
                int year = (int) node.getLabel();
                if (year > from.getYear() && year < to.getYear()) {
                    var content = node.getContent();
                    for (int count = 0; count < node.getCount(); count++) {
                        result.merge((ICuboid) content);
                        content = content.getNextNode();
                    }
                }
                else if (year == from.getYear() || year == to.getYear())
                    queryMinTimeDFS(node, result, from, to);
            }
            else if( node.decodeLevel() == 2) {
                int month = (int) node.getLabel();
                if (month > from.getMonth() && month < to.getMonth()){
                    var content = node.getContent();
                    for (int count = 0; count < node.getCount(); count++) {
                        result.merge((ICuboid) content);
                        content = content.getNextNode();
                    }
                }
                else if (month == from.getMonth() || month == to.getMonth())
                    queryMinTimeDFS(node, result, from, to);
            }
            else if( node.decodeLevel() == 3) {
                int date = (int) node.getLabel();
                if (date >= from.getDate() && date <= to.getDate()) {
                    result.merge((ICuboid) node.getContent());
                }

            }
        }
    }


    private int checkGeoAvailable(Stack<ILode> path, long ltLat, long ltLon, long rbLat, long rbLon) {
        long maxLat = 1800000000L;
        long minLat = 0;
        long maxLon = 3600000000L;
        long minLon = 0;
        for (int i = 1; i < path.size(); i++) {
            switch ((int) (path.get(i).getLabel())) {
                case 0 -> {
                    minLat = minLat + (maxLat + 1 - minLat) / 2;
                    maxLon = minLon + (maxLon + 1 - minLon) / 2;
                }
                case 1 -> {
                    minLat = minLat + (maxLat + 1 - minLat) / 2;
                    minLon = minLon + (maxLon + 1 - minLon) / 2;
                }
                case 2 -> {
                    maxLat = minLat + (maxLat + 1 - minLat) / 2;
                    maxLon = minLon + (maxLon + 1 - minLon) / 2;
                }
                case 3 -> {
                    maxLat = minLat + (maxLat + 1 - minLat) / 2;
                    minLon = minLon + (maxLon + 1 - minLon) / 2;
                }
            }
        }
        // 0-完全范围内 1-范围外 2-涉及范围内
        if (minLon > rbLon || maxLat < rbLat)
            return 1;
        else if (maxLon < ltLon || minLat > ltLat)
            return 1;
        else if (maxLon <= rbLon && maxLat <= ltLat && minLon >= ltLon && minLat >= rbLat)
            return 0;
        return 2;
    }

    public static Tuple3<Double, Double, Integer> getGeo(Stack<ILode> path, int count) {
        long maxLat = 1800000000L;
        long minLat = 0;
        long maxLon = 3600000000L;
        long minLon = 0;
        for (int i = 1; i < path.size(); i++) {
            switch ((int) (path.get(i).getLabel())) {
                case 0 -> {
                    minLat = minLat + (maxLat + 1 - minLat) / 2;
                    maxLon = minLon + (maxLon + 1 - minLon) / 2;
                }
                case 1 -> {
                    minLat = minLat + (maxLat + 1 - minLat) / 2;
                    minLon = minLon + (maxLon + 1 - minLon) / 2;
                }
                case 2 -> {
                    maxLat = minLat + (maxLat + 1 - minLat) / 2;
                    maxLon = minLon + (maxLon + 1 - minLon) / 2;
                }
                case 3 -> {
                    maxLat = minLat + (maxLat + 1 - minLat) / 2;
                    minLon = minLon + (maxLon + 1 - minLon) / 2;
                }
            }
        }
        return new Tuple3<>((maxLon + minLon) * 1.0d / 20000000 - 179, (maxLat + minLat) * 1.0d / 20000000 - 89, count);
    }

    @Override
    public List<Tuple2<Integer, Integer>> queryTime(double ltLat, double ltLon, double rbLat, double rbLon, int zoom, List<List<Long>> cate,
                                   int timeLevel) {
        List<Tuple2<Integer, Integer>> nodes = new ArrayList<Tuple2<Integer, Integer>>();
        Stack<ILode> geo = new Stack<>();
        geo.push(root);
        spatial = 0;
        temporal = 0;
        categorical = 0;
        long st = System.nanoTime();
        queryMinGeoDFS(nodes, geo,(long) ((ltLat + 89) * 10000000), (long) ((ltLon + 179) * 10000000),
                (long) ((rbLat + 89) * 10000000), (long) ((rbLon + 179) * 10000000), zoom, cate, timeLevel);
        spatial += System.nanoTime() - st;
        return nodes;
    }

    public void queryMinGeoDFS(List<Tuple2<Integer, Integer>> list, Stack<ILode> geo,long ltLat, long ltLon, long rbLat, long rbLon, int zoom, List<List<Long>> cate, int timeLevel) {
        if (geo.size() - 1 == zoom) {
            var content = geo.peek().getContent();
            for (int count = 0; count < geo.peek().getCount(); count++) {
                if(cate.size() == 0) {
                    Stack<ILode> path =new Stack<>();
                    path.push((ILode) geo.peek().getContent());
                    queryTimeDFS(path, list, timeLevel);
                }
                else {
                    long st = System.nanoTime();
                    queryCateDFS((ILode) geo.peek().getContent(), list, cate, 0, timeLevel);
                    categorical += System.nanoTime() - st;
                }
                content = content.getNextNode();
            }
        } else {
            for (var _node : geo.peek().getChildren()) {
                ILode node = (ILode) _node;
                geo.push(node);
                if (checkGeoAvailable(geo, ltLat, ltLon, rbLat, rbLon) == 2)
                    queryMinGeoDFS(list, geo, ltLat, ltLon, rbLat, rbLon, zoom, cate, timeLevel);
                else if (checkGeoAvailable(geo, ltLat, ltLon, rbLat, rbLon) == 0) {
                    var content = geo.peek().getContent();
                    for (int count = 0; count < geo.peek().getCount(); count++) {
                        if(cate.size() == 0) {
                            Stack<ILode> path =new Stack<>();
                            path.push((ILode) geo.peek().getContent());
                            long st = System.nanoTime();
                            queryTimeDFS(path, list, timeLevel);
                            temporal += System.nanoTime() - st;
                        }
                        else {
                            long st = System.nanoTime();
                            queryCateDFS((ILode) geo.peek().getContent(), list, cate, 0, timeLevel);
                            categorical += System.nanoTime() - st;
                        }
                        content = content.getNextNode();
                    }

                }
                geo.pop();
            }
        }
    }

    private List<Tuple2<Integer, Integer>> queryCateDFS(ILode rootNode, List<Tuple2<Integer, Integer>> result, List<List<Long>> query, int dimension, int level) {
        if (dimension >= query.size())
            return result;
        List<Long> chain = query.get(dimension);
        ILode node = rootNode;
        for (Long aLong : chain) {
            node = (ILode) node.getChild(aLong); //== null?node.getChild(0):node.getChild(chain.get(i));
            if (node == null)
                return result;
        }
        var content = node.getContent();
        if (dimension == query.size() - 1) {
            for (int count = 0; count < node.getCount(); count++) {
                Stack<ILode> path = new Stack<>();
                path.add((ILode) content);
                long st = System.nanoTime();
                queryTimeDFS(path, result, level);
                temporal += System.nanoTime() - st;
                content = content.getNextNode();
            }
        }
        else
            for (int count = 0; count < node.getCount(); count++) {
                queryCateDFS((ILode) content, result, query, dimension + 1, level);
                content = content.getNextNode();
            }
        return result;
    }

    private void queryTimeDFS(Stack<ILode> path, List<Tuple2<Integer, Integer>> result, int level) {
        for (INode _node : path.peek().getChildren()) {
            ILode node= (ILode)_node;
            path.push(node);
            if (path.size() - 1 == level) {
                var content = node.getContent();
                ICuboidLink cuboid = null;
                try {
                    cuboid = clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (int count = 0; count < node.getCount(); count++) {
                    assert cuboid != null;
                    cuboid.merge((ICuboid) content);
                    content = content.getNextNode();
                }
                assert cuboid != null;
                Tuple2<Integer, Integer> point = getTime(path, cuboid.result());
                for (int i = result.size() - 1; i >= 0; i--) {
                    Tuple2<Integer, Integer> tmp = result.get(i);
                    if (tmp.x.equals(point.x)) {
                        tmp.y += point.y;
                        break;
                    }
                    if (tmp.x < point.x) {
                        result.add(i + 1, point);
                        break;
                    }
                }
                if (result.size() == 0)
                    result.add(point);
                else if (result.get(0).x > point.x)
                    result.add(0, point);
            }
            else
                queryTimeDFS(path, result, level);
            path.pop();
        }
    }

    private Tuple2<Integer, Integer> getTime(Stack<ILode> path, int count) {
        int off = 10000;
        int time = 0;
        for (int i = 1; i < path.size(); i++) {
            time += path.get(i).getLabel() * off;
            off /= 100;
        }
        return new Tuple2<>(time, count);
    }
}
