package com.paddi.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.paddi.constants.HttpStatus;
import com.paddi.exception.ConditionException;

import java.util.Calendar;
import java.util.Date;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月15日 22:43:27
 */
public class TokenUtil {

    private static final String ISSUER = "Paddi-Yan";

    private static final int TOKEN_EXPIRE_TIME = 60 * 60 * 24;
    public static String generateToken(Long userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, TOKEN_EXPIRE_TIME);
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }

    public static Long verifyToken(String token) {
        Algorithm algorithm = null;
        try {
            algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        } catch(TokenExpiredException e) {
            throw new ConditionException(HttpStatus.FORBIDDEN, "授权过期");
        } catch(Exception e) {
            throw new ConditionException("非法用户Token!");
        }
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);
        return Long.valueOf(jwt.getKeyId());
    }
}
