package com.mabo.redis.easycode.fairLock;

import com.mabo.redis.easycode.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * @Description : 定义自定义注解的切面
 * @Author : mabo
 */
@Slf4j
@Component
@Aspect
public class FairLockAspect {
    @Autowired
    RedissonClient redissonClient;

    /**
     * @Description : 使用Around可以修改方法的参数，返回值，
     * 甚至不执行原来的方法,但是原来的方法不执行会导致before和after注解的内容不执行
     * 通过around给原方法赋给参数
     */
    @Around("@annotation(fairLock)")
    public Object addEventListener(ProceedingJoinPoint joinPoint, FairLock fairLock) throws Throwable {
        int waitTime = fairLock.waitTime();
        int leaseTime = fairLock.leaseTime();

        RLock fair = redissonClient.getFairLock(fairLock.value());
        //读之前加读锁，读锁的作用就是等待该lockkey释放写锁以后再读
        Object  proceed = null;
        try {
            //根据waitTime和leaseTime判断加锁的种类
            if (leaseTime>0){
                fair.tryLock(waitTime,leaseTime,fairLock.timeUnit());
                log.info(Thread.currentThread().getName()+"获取了fairLock: "+fairLock.value()+"  时间: "+ TimeUtils.formatTime(new Date()));
            }else if (waitTime<0){
                fair.lock(leaseTime,fairLock.timeUnit());
                log.info(Thread.currentThread().getName()+"获取了fairLock: "+fairLock.value()+"  时间: "+ TimeUtils.formatTime(new Date()));
            }
            Object[] args = joinPoint.getArgs();
            proceed = joinPoint.proceed(args);
        } finally {
            fair.unlock();
            log.info(Thread.currentThread().getName()+"释放了fairLock: "+fairLock.value()+"  时间: "+ TimeUtils.formatTime(new Date()));
        }
        return proceed;
    }

}
