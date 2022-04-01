package yefancy.tpa.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import yefancy.tpa.resutl.Result;
import yefancy.tpa.service.TpaServiceImpl;
import yefancy.tpa.utils.HttpContextUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TpaInterceptor implements HandlerInterceptor {

    /**
     * @Author: 三分恶
     * @Date: 2021/1/18
     * @Description: 用户登录拦截器
     **/

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler)
            throws IOException {

        String token = request.getHeader("Access-Token");

        response.setHeader("Access-Control-Allow-Origin", HttpContextUtil.getOrigin());
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, PATCH, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "X-Requested-With,content-type,Access-Token");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        if ("/uploadExcel".equals(request.getServletPath())) {
            return true;
        }
        else if (StringUtils.isEmpty(token)) {
            setReturn(response, 401, "please upload your tabular");
            return false;
        } else if (!TpaServiceImpl.INSTANCE.checkActive(token)) {
            setReturn(response, 401, "token overtime");
            return false;
        }

        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler,
                                Exception ex) {

    }

    //返回json格式错误信息
    private static void setReturn(HttpServletResponse response, Integer code,
                                  String msg) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        //UTF-8编码
        httpResponse.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        Result result = new Result(code, msg, "");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(result);
        httpResponse.getWriter().print(json);
    }

}
