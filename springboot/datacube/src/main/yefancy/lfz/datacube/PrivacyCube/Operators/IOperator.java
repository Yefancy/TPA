package main.yefancy.lfz.datacube.PrivacyCube.Operators;

import main.yefancy.lfz.datacube.PrivacyCube.WebNode;
import main.yefancy.lfz.datacube.api.tree.INode;

import java.util.List;

public interface IOperator {
    boolean execute(WebNode root, List<Integer> schema);
}
