package yefancy.tpa.service;

import main.yefancy.lfz.datacube.PrivacyCube.Excel;
import main.yefancy.lfz.datacube.PrivacyCube.Operators.MergeCOP;
import main.yefancy.lfz.datacube.PrivacyCube.Operators.MergeOP;
import main.yefancy.lfz.datacube.PrivacyCube.Operators.UpdateOP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yefancy.tpa.resutl.Result;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TpaServiceImpl {
    public static TpaServiceImpl INSTANCE = new TpaServiceImpl();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Map<String, TpaSession> activeExcel;

    private TpaServiceImpl() {
        activeExcel = new HashMap<>();
    }

    public Result uploadExcel(String token, Excel excel){
//        for (int i = 0; i < 50; i++) {
//            long st = System.currentTimeMillis();
//            if(activeExcel.containsKey(token)) {
//                activeExcel.get(token).dispose();
//                activeExcel.remove(token);
//            }
//
//            TpaSession newSession = new TpaSession(excel);
//            activeExcel.put(newSession.token, newSession);
//            Map<String, Object> result = new LinkedHashMap<>();
//            result.put("token", newSession.token);
//            result.put("mapSchema", newSession.cube.schemaMap);
//            System.out.printf("%d\t%d%n", i, System.currentTimeMillis() - st);
//        }
        long st = System.currentTimeMillis();
        if(activeExcel.containsKey(token)) {
            activeExcel.get(token).dispose();
            activeExcel.remove(token);
        }

        TpaSession newSession = new TpaSession(excel);
        activeExcel.put(newSession.token, newSession);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", newSession.token);
        result.put("mapSchema", newSession.cube.schemaMap);
        logger.info("[uploadExcel]{%s}[%d]: %s".formatted(token, System.currentTimeMillis() - st, newSession.token));
        return new Result(200, "uploadExcel", result);
    }

    public Result updateTableau(String token, UpdateOP op) {
        long st = System.currentTimeMillis();
        if(checkActive(token)) {
            TpaSession session = activeExcel.get(token);
            op.execute(session.excel);
            session.reloadCube();
            session.updateRoot();
            logger.info("[updateTableau]{%s}[%d][]: %s".formatted(token, System.currentTimeMillis() - st, op));
            return new Result(200, "updateSchema", session.root);
        } else {
            logger.info("[updateTableau]{%s}[%d][]: failed - no session".formatted(token, System.currentTimeMillis() - st));
            return new Result(400, "updateSchema",null);
        }
    }

    public Result updateSchema(String token, List<Integer> schema, List<Integer> sensitive) {
        long st = System.currentTimeMillis();
        if(checkActive(token)) {
            TpaSession session = activeExcel.get(token);
            session.schema = schema;
            session.sensitive = sensitive;
            session.updateRoot();
            logger.info("[updateSchema]{%s}[%d][]: %s %s".formatted(token, System.currentTimeMillis() - st, schema, sensitive));
            return new Result(200, "updateSchema", session.root);
        } else {
            logger.info("[updateSchema]{%s}[%d][]: failed - no session".formatted(token, System.currentTimeMillis() - st));
            return new Result(400, "updateSchema",null);
        }
    }

    public Result mergeGroup(String token, MergeOP op){
        long st = System.currentTimeMillis();
        if(checkActive(token)) {
            TpaSession session = activeExcel.get(token);
            activeExcel.get(token).operators.add(op);
            if(!op.execute(session.root, session.schema)) {
                logger.info("[mergeGroup]{%s}[%d][]: failed - execute op".formatted(token, System.currentTimeMillis() - st));
                return new Result(401, "mergeGroup", null);
            }
            logger.info("[mergeGroup]{%s}[%d][]: %s %d %s %s".formatted(token, System.currentTimeMillis() - st, op.filters, op.dim, op.label1, op.label2));
            return new Result(200, "mergeGroup", session.root);
        } else {
            logger.info("[mergeGroup]{%s}[%d][]: failed - no session".formatted(token, System.currentTimeMillis() - st));
            return new Result(400, "mergeGroup",null);
        }
    }

    public Result mergeCubeGroup(String token, MergeCOP op){
        long st = System.currentTimeMillis();
        if(checkActive(token)) {
            TpaSession session = activeExcel.get(token);
            if(!op.execute(session.cube)) {
                logger.info("[mergeCubeGroup]{%s}[%d][]: failed - execute op".formatted(token, System.currentTimeMillis() - st));
                return new Result(401, "mergeCubeGroup", null);
            }
            session.updateRoot();
            logger.info("[mergeCubeGroup]{%s}[%d][]: %s %d %s %s".formatted(token, System.currentTimeMillis() - st, op.filters, op.dim, op.label1, op.label2));
            return new Result(200, "mergeCubeGroup", session.root);
        } else {
            logger.info("[mergeCubeGroup]{%s}[%d][]: failed - no session".formatted(token, System.currentTimeMillis() - st));
            return new Result(400, "mergeCubeGroup",null);
        }
    }

    public boolean checkActive(String token) {
        return activeExcel.containsKey(token);
    }
}
