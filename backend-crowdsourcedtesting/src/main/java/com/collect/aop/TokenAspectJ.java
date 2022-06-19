package com.collect.aop;

import com.collect.annotation.CheckLoginStatus;
import com.collect.annotation.UserId;
import com.collect.po.enums.UserIdentity;
import com.collect.util.TokenUtil;
import com.collect.vo.ResponseVO;
import com.collect.vo.http.UserHttpStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Component
@Aspect
public class TokenAspectJ {

    public static ThreadLocal<Integer> USER_ID = null;

    /**
     * 切点
     */
    @Pointcut(value = "@annotation(com.collect.annotation.CheckLoginStatus)")
    private void verifyLoginPointCut() {
    }

    /**
     * 检查登录状况
     * @param point
     * @return
     * @throws Throwable
     */
    @Around(value = "verifyLoginPointCut()")
    public Object verifyLogin(ProceedingJoinPoint point) throws Throwable {
        CheckLoginStatus checkLoginStatus = ((MethodSignature) point.getSignature()).getMethod().getAnnotation(CheckLoginStatus.class);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 根据请求头中的Authorization获得token，若不存在或验证错误则返回错误
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseVO.fail(UserHttpStatus.USER_TOKEN_ERROR);
        }
        String token = authHeader.substring(7);
        try {
            // 如果验证成功则则获取token中的id和identity
            final Claims claims = TokenUtil.parseJWT(token);
            String id = "userId", identity = "identity";
            Integer userId = (Integer) claims.get(id);
            UserIdentity userIdentity = UserIdentity.parseFrom(String.valueOf(claims.get(identity)));
            if (!checkLoginStatus.identity().equals(UserIdentity.ALL)) {
                // 如果identity不匹配则返回错误
                if (!userIdentity.equals(checkLoginStatus.identity())) {
                    return ResponseVO.fail(UserHttpStatus.USER_PRIVILEGE_ERROR);
                }
            }
            if (checkLoginStatus.insert().equals(CheckLoginStatus.InsertMode.ENTITY)) {
                //将userId插入到方法的加有@userId注解的参数中，或类中有@userId注解的属性中
                Method method = ((MethodSignature) point.getSignature()).getMethod();
                Annotation[][] paramAnnotations = method.getParameterAnnotations();
                Class<?>[] classes = method.getParameterTypes();
                for (int i = 0; i < point.getArgs().length; i++) {
                    if (classes[i].equals(Integer.class)) {
                        if (paramAnnotations[i] != null && paramAnnotations[i].length != 0) {
                            for (Annotation annotation : paramAnnotations[i]) {
                                if (annotation.annotationType().equals(UserId.class)) {
                                    point.getArgs()[i] = userId;
                                }
                            }
                        }
                    } else {
                        Field[] fields = point.getArgs()[i].getClass().getDeclaredFields();
                        for (Field f : fields) {
                            if (f.getAnnotation(UserId.class) != null && f.getType().equals(Integer.class)) {
                                f.setAccessible(true);
                                f.set(point.getArgs()[i], userId);
                            }
                        }
                    }
                }
            } else if (checkLoginStatus.insert().equals(CheckLoginStatus.InsertMode.THREAD)) {
                USER_ID = new ThreadLocal<>();
                USER_ID.set(userId);
            }
            // 更新token
            token = TokenUtil.createJWT(claims);
            HttpServletResponse httpServletResponse = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            httpServletResponse.setHeader("token", token);
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "token");
        } catch (final SignatureException | MalformedJwtException | ExpiredJwtException e) {
            return ResponseVO.fail(UserHttpStatus.USER_TOKEN_ERROR);
        }
        return point.proceed(point.getArgs());
    }

}
