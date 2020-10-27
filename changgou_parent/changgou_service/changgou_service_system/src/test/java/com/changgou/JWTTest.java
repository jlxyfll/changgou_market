package com.changgou;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

public class JWTTest {


    /*    *//**
     * 生成JWT字符串，也称为TOKEN值
     * 应用的场景：一般用于用户登录的时候系统给当前登录成功的用户生成这个TOKEN值
     *//*
    @Test
    public void createJwt(){
        String secret = "itheima";

        String jwt = Jwts.builder().signWith(SignatureAlgorithm.HS256, secret) //设置头部的算法和签名的密钥
                .setId(UUID.randomUUID().toString()) //设置JWT的唯一ID标识
                .setSubject("黑马程序员") //设置JWT的主题
                .setIssuedAt(new Date()) //设置JWT的系统签发时间
                .compact();
        System.out.println("JWT值：" + jwt);
    }

    *//**
     * JWT超时时间
     *//*
    @Test
    public void createJwtExpire(){
        String secret = "itheima";

        String jwt = Jwts.builder().signWith(SignatureAlgorithm.HS256, secret) //设置头部的算法和签名的密钥
                .setId(UUID.randomUUID().toString()) //设置JWT的唯一ID标识
                .setSubject("黑马程序员") //设置JWT的主题
                .setIssuedAt(new Date()) //设置JWT的系统签发时间
                .setExpiration(new Date(System.currentTimeMillis()+3600000)) //设置为有效期为1个小时
                .compact();
        System.out.println("JWT值：" + jwt);
    }

    @Test
    public void createJwtCustom(){
        String secret = "itheima";
        String jwt = Jwts.builder().signWith(SignatureAlgorithm.HS256, secret) //设置头部的算法和签名的密钥
                .setId(UUID.randomUUID().toString()) //设置JWT的唯一ID标识
                .setSubject("黑马程序员") //设置JWT的主题
                .setIssuedAt(new Date()) //设置JWT的系统签发时间
                .setExpiration(new Date(System.currentTimeMillis()+3600000))
                .claim("username", "zhangsan") //设置自定义用户属性，用户名
                .claim("age", 22) //设置自定义用户属性，用户年龄
                .compact();
        System.out.println("JWT值：" + jwt);
    }


    *//**
     * 解析JWT字符串
     * 应用的场景：用户登录成功后访问其他页面或资源，携带JWT字符串，服务端进行解析验证
     */
    @Test
    public void parseJwt(){
//        String secret = "itheima";
//        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIwZjM0NmEyMC0xMWU2LTQ4MzgtODYxOC1hMTM3YjQyZDg5YzciLCJzdWIiOiLpu5HpqaznqIvluo_lkZgiLCJpYXQiOjE1Nzc5NDc0ODcsImV4cCI6MTU3Nzk1MTA4NywidXNlcm5hbWUiOiJ6aGFuZ3NhbiIsImFnZSI6MjJ9.1rO-aDvG8wzg_YEnTOTXlkzwdIrspN7iSbQZ4lS9v3Q";
        String secret = "JIANGJIANG";
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJkMmE2MjBkMC01ZDM4LTQxZDAtYWFjYy1lZDJiZTkxYWVhODMiLCJzdWIiOiJUQU5HIiwiaWF0IjoxNjAzNzI4NjU0fQ.We7DeCQV67zTH0q2Y4lshq2Ki90Uf2dXuJVdXfq34h0";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt);
        JwsHeader header = claimsJws.getHeader();
        Claims body = claimsJws.getBody();
        String signature = claimsJws.getSignature();
        System.out.println("解析JWT的头：" + header);
        System.out.println("解析JWT的载荷："+body);
        System.out.println("解析JWT的签名：" + signature);
    }

    /**
     * 生成JWT字符串，也称为TOKEN值
     * 应用的场景：一般用于用户登录的时候系统给当前登录成功的用户生成这个TOKEN值
     */
    @Test
    public void createJWT() {
        String secret = "JIANGJIANG";
        JwtBuilder jwt = Jwts.builder();
        String jwtInfo = jwt.signWith(SignatureAlgorithm.HS256, secret)
                .setId(UUID.randomUUID().toString())
                .setSubject("TANG")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+3600000))
                .compact();
        System.out.println("jwt信息：" + jwtInfo);

    }

    /**
     * 生成JWT字符串，也称为TOKEN值
     * 应用的场景：一般用于用户登录的时候系统给当前登录成功的用户生成这个TOKEN值
     */
    @Test
    public void createJWTCustom() {
        String secret = "JIANGJIANG";
        JwtBuilder jwt = Jwts.builder();
        String jwtInfo = jwt.signWith(SignatureAlgorithm.HS256, secret)
                .setId(UUID.randomUUID().toString())
                .setSubject("TANG")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+3600000))
                .claim("username","zhangsan")
                .claim("age","22")
                .compact();
        System.out.println("jwt信息：" + jwtInfo);

    }

    /**
     * 解析JWT字符串
     * 应用的场景：用户登录成功后访问其他页面或资源，携带JWT字符串，服务端进行解析验证
     * 头信息：{alg=HS256}
     * 载荷：{jti=d2a620d0-5d38-41d0-aacc-ed2be91aea83, sub=TANG, iat=1603728654}
     * 签名：We7DeCQV67zTH0q2Y4lshq2Ki90Uf2dXuJVdXfq34h0
     * 解析JWT的头：{alg=HS256}
     * 解析JWT的载荷：{jti=d2a620d0-5d38-41d0-aacc-ed2be91aea83, sub=TANG, iat=1603728654}
     * 解析JWT的签名：We7DeCQV67zTH0q2Y4lshq2Ki90Uf2dXuJVdXfq34h0
     */
    @Test
    public void parseJWT() {
        String secret = "JIANGJIANG";
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJmN2JmMDY3OS0zODcyLTQxODYtYjkxNi1mYTUxNGVhNDNhYzAiLCJzdWIiOiJUQU5HIiwiaWF0IjoxNjAzNzcwNjc0LCJleHAiOjE2MDM3NzQyNzQsInVzZXJuYW1lIjoiemhhbmdzYW4iLCJhZ2UiOiIyMiJ9.NxbY3fFHtPCxpreJGUC8QTLRYSxwEQE4I0UOEzJH4ok";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt);
        Header header = claimsJws.getHeader();
        Claims body = claimsJws.getBody();
        String signature = claimsJws.getSignature();
        System.out.println("头信息：" + header);
        System.out.println("载荷：" + body);
        System.out.println("签名：" + signature);
    }
}
