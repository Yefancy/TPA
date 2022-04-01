package main.yefancy.lfz.datacube.DPVcube;

import main.yefancy.lfz.datacube.Linkube.ICuboidLink;
import main.yefancy.lfz.datacube.Linkube.ILode;
import main.yefancy.lfz.datacube.api.tree.INode;

import java.util.Iterator;
import java.util.List;

public class ServerCube<T extends ICuboidLink> extends ACube<T> {

    public ServerCube(Class<? extends T> clazz) {
        super(clazz);
    }

    public void update(List<ICuboidLink> upload) {
        uploadDFS(root, upload.iterator(), false);
    }

    private void uploadDFS(ILode root, Iterator<ICuboidLink> iterator, boolean shared) {
        //if (!root.isContentShared()) {
            var content = root.getContent();
            if (content instanceof ICuboidLink) {
                var cuboid = iterator.next();
                if (!root.isContentShared() && !shared) {
                    ((ICuboidLink) content).merge(cuboid);
                }
            } else {
                uploadDFS((ILode) content, iterator, shared || root.isContentShared());
            }
        //}
        if(root.getChildren() != null) {
            for (INode child : root.getChildren()) {
                //if (!root.isChildShared(child.getLabel())) {
                    uploadDFS((ILode) child, iterator, shared || root.isChildShared(child.getLabel()));
                //}
            }
        }
    }

}
