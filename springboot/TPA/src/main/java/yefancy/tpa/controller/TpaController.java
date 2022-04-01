package yefancy.tpa.controller;

import main.yefancy.lfz.datacube.PrivacyCube.Excel;
import main.yefancy.lfz.datacube.PrivacyCube.Operators.MergeCOP;
import main.yefancy.lfz.datacube.PrivacyCube.Operators.MergeOP;
import main.yefancy.lfz.datacube.PrivacyCube.Operators.UpdateOP;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import yefancy.tpa.resutl.Result;
import yefancy.tpa.service.TpaServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class TpaController {

    @PostMapping("uploadExcel")
    public Result uploadExcel(HttpServletRequest request, @RequestBody Excel excel) {
        return TpaServiceImpl.INSTANCE.uploadExcel(getToken(request), excel);
    }

    @PostMapping("updateTableau")
    public Result updateTableau(HttpServletRequest request, @RequestBody UpdateOP op) {
        return TpaServiceImpl.INSTANCE.updateTableau(getToken(request), op);
    }

    @PostMapping("updateSchema")
    public Result getBasicTree(HttpServletRequest request,@RequestBody Map<String, List<Integer>> config) {
        return TpaServiceImpl.INSTANCE.updateSchema(getToken(request), config.get("schema"), config.get("sensitives"));
    }

    @PostMapping("mergeOP")
    public Result mergeGroup(HttpServletRequest request,@RequestBody MergeOP op) {
        return TpaServiceImpl.INSTANCE.mergeGroup(getToken(request), op);
    }

    @PostMapping("MergeCOP")
    public Result mergeGroup(HttpServletRequest request,@RequestBody MergeCOP op) {
        return TpaServiceImpl.INSTANCE.mergeCubeGroup(getToken(request), op);
    }

    private static String getToken(HttpServletRequest request) {
        String token = request.getHeader("Access-Token");
        return token == null ? "" : token;
    }
}
