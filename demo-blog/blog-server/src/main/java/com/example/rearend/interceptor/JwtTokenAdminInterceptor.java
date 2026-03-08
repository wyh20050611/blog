package com.example.rearend.interceptor;

import com.example.rearend.context.BaseContext;
import com.example.rearend.properties.JwtProperties;
import com.example.rearend.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 34932
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;


    /**
     * jwt令牌校验
     *
     * @param request:是指经过spring封装的请求对象,包含请求地址,头,参数,body(流)等信息
     * @param response:是指经过spring封装的响应对象,包含输入流,响应body类型等信息
     * @param handler:是指controller的@Controller注解下的"完整"方法名,是包含exception等字段信息的
     * @return boolean
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //判断当前拦截到的是不是Controller的方法
        if (!(handler instanceof HandlerMethod)) {
            //如果不是Controller的方法，则放行
            return true;
        }
        //1、获取请求头
        String token = request.getHeader("Authorization");

        //2、校验令牌
        try {
            log.info("令牌校验：{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            Long empId = Long.valueOf(claims.get("empId").toString());
            log.info("当前员工id：{}", empId);
            //放入员工id
            BaseContext.setCurrentId(empId);
            //3、通过，放行
            return true;
        } catch (Exception e) {
            //失败，放入401状态码
            response.setStatus(401);
            return false;
        }
    }
}
