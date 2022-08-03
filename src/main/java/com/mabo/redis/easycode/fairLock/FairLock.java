package com.mabo.redis.easycode.fairLock;
/**
 * @Description : 在当前修饰的方法前后执行其他的方法
 * @Author : mabo
*/
import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FairLock {
    /**
     * 锁名称
     */
    String value() ;
    /**
     * 等待超时：默认秒
     */
    int waitTime() default 5;

    /**
     * 过期时长：默认秒
     */
    int leaseTime() default 10;

    /**
     * 时长单位：默认秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
