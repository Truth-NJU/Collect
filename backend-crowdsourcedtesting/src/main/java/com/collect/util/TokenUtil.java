package com.collect.util;

import com.collect.config.TokenConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;

@Component
public class TokenUtil {

    @Autowired
    TokenConfig tokenConfig;

    public static String staticKey; //密钥

    public static long staticTTl;   //过期时间

    @PostConstruct
    public void init() {
        staticKey = tokenConfig.getSecretKey();
        staticTTl = tokenConfig.getTtl();
    }

    /**
     * 生成JWT
     * issuedAt=签发时间
     * expiration=有效时间
     */
    public static String createJWT(Map<String, Object> claims) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder().setIssuedAt(now)
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, generalKey(staticKey));
        if (staticTTl > 0) {
            builder.setExpiration(new Date(nowMillis + staticTTl));
        }
        return builder.compact();
    }

    /**
     * 解析JWT
     */
    public static Claims parseJWT(String token) {
        SecretKey secretKey = generalKey(staticKey);
        return Jwts.parser()  //得到DefaultJwtParser
                .setSigningKey(secretKey)   //设置签名的秘钥
                .parseClaimsJws(token)  //设置需要解析的jwt
                .getBody();
    }

    /**
     * 由字符串生成加密key
     *
     * @return SecretKey
     */
    private static SecretKey generalKey(String originKey) {
        byte[] encodedKey = Base64.decodeBase64(originKey);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }
}
