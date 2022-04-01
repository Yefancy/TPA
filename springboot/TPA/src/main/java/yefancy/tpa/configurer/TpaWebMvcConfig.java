package yefancy.tpa.configurer;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import yefancy.tpa.interceptor.TpaInterceptor;

@Configuration
public class TpaWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加拦截器
        registry.addInterceptor(new TpaInterceptor()).addPathPatterns("/**")
                //放行路径，可以添加多个
                .excludePathPatterns("/");
    }
}
