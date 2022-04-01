package main.yefancy.lfz.datacube.DPVcube;

import main.yefancy.lfz.datacube.Linkube.ICuboidLink;
import main.yefancy.lfz.datacube.Linkube.ILode;
import main.yefancy.lfz.datacube.api.tree.INode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClientCube<T extends ICuboidLink> extends ACube<T> {
    private final Random random;
    private final int[] privacy;
    public ClientCube(Class<? extends T> clazz, int[] privacy, long seed) {
        super(clazz);
        this.random = new Random(seed);
        this.privacy = privacy;
    }

    protected double getSensitive(double sensitive, ILode lode) {
        int count = dfsCount(lode);
        if (count >= privacy[lode.decodeDim()]) {
            return sensitive;
        }
        // TODO
        return Math.max(1, sensitive) * count;
    }

    private int dfsCount(INode lode) {
        var children = lode.getChildren();
        if(children == null)
            return 1;
        int result = 0;
        for(var child : children) {
            result += dfsCount(child);
        }
        return result;
    }

//    private int checkPrivacyAvailable(INode lode, int privacy, int least) {
//        var children = lode.getChildren();
//        if(children == null)
//            return 1;
//        if(least + children.size() >= privacy) return privacy;
//        var result = 0;
//        for (int i = 0; i < children.size(); i++) {
//            result += checkPrivacyAvailable(children.get(i), privacy, least + result + children.size() - 1 - i);
//            if(result >= privacy)
//                return privacy;
//        }
//        return result;
//    }

    protected double getNoise(double mu, double lambda) {
        double randomDouble = random.nextDouble() - 0.5;
        return mu - lambda * Math.signum(randomDouble) * Math.log(1 - 2 * Math.abs(randomDouble));
    }

    public List<ICuboidLink> getUpload() {
        List<ICuboidLink> result = new ArrayList<>();
        uploadDFS(root, result, 0);
        return result;
    }

    private void uploadDFS(ILode root, List<ICuboidLink> result, double sensitive) {
        //if (!root.isContentShared()) {
            var content = root.getContent();
            assert content != null;
            if (content instanceof ICuboidLink) {
                result.add((ICuboidLink) ((ICuboidLink)content).shallowCopy().getNode().addNoise(getNoise(0, sensitive)));
            } else {
                uploadDFS((ILode) content, result, getSensitive(sensitive, (ILode) content));
            }
        //}
        if(root.getChildren() != null) {
            for(INode child: root.getChildren()){
                //if (!root.isChildShared(child.getLabel())) {
                    uploadDFS((ILode) child, result, getSensitive(sensitive, (ILode) child));
                //}
            }
        }
    }

}
