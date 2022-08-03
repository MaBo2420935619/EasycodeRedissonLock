package com.mabo.redis.easycode.readWriteLock;

import com.mabo.redis.easycode.utils.TimeUtils;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
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
public class ReadWriteLockAspect {
    @Autowired
    RedissonClient redissonClient;

    /**
     * @Description : 使用Around可以修改方法的参数，返回值，
     * 甚至不执行原来的方法,但是原来的方法不执行会导致before和after注解的内容不执行
     * 通过around给原方法赋给参数
     */
    @Around("@annotation(readWriteLock)")
    public Object addEventListener(ProceedingJoinPoint joinPoint, ReadWriteLock readWriteLock) throws Throwable {
        int waitTime = readWriteLock.waitTime();
        int leaseTime = readWriteLock.leaseTime();

        RReadWriteLock readWrite = redissonClient.getReadWriteLock(readWriteLock.value());
        RLock rLock = readWrite.readLock();
        RLock wLock = readWrite.writeLock();
        ReadWriteLockType readWriteLockType = readWriteLock.lockType();
        //读之前加读锁，读锁的作用就是等待该lockkey释放写锁以后再读
        Object  proceed = null;
        try {
            //根据waitTime和leaseTime判断加锁的种类
            if (leaseTime>0){
                if (readWriteLockType.equals(ReadWriteLockType.READLOCK)){
                    rLock.tryLock(waitTime,leaseTime, readWriteLock.timeUnit());
                    log.info(Thread.currentThread().getName()+"获取到了READLOCK: "+readWriteLock.value()+"  时间: "+ TimeUtils.formatTime(new Date()));
                }
                else {
                    wLock.tryLock(waitTime,leaseTime, readWriteLock.timeUnit());
                    log.info(Thread.currentThread().getName()+"获取到了WRITELOCK: "+readWriteLock.value()+"  时间: "+ TimeUtils.formatTime(new Date()));
                }
            }else if (waitTime<0){
                if (readWriteLockType.equals(ReadWriteLockType.READLOCK)){
                    rLock.lock(leaseTime, readWriteLock.timeUnit());
                }
                else {
                    wLock.lock(leaseTime, readWriteLock.timeUnit());
                }

            }

            Object[] args = joinPoint.getArgs();
            proceed = joinPoint.proceed(args);
        } finally {
            if (readWriteLockType.equals(ReadWriteLockType.READLOCK)){
                rLock.unlock();
                log.info(Thread.currentThread().getName()+"释放了READLOCK: "+readWriteLock.value()+"  时间: "+ TimeUtils.formatTime(new Date()));
            }
            else {
                wLock.unlock();
                log.info(Thread.currentThread().getName()+"释放了WRITELOCK: "+readWriteLock.value()+"  时间: "+ TimeUtils.formatTime(new Date()));
            }
        }
        return proceed;
    }

}
