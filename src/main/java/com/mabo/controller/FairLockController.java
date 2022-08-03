package com.mabo.controller;

import com.mabo.redis.easycode.fairLock.FairLock;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RestController
public class FairLockController {
    @FairLock("fairLock")
    @RequestMapping("fairLock")
    public Date fairLock() throws InterruptedException {
        Thread.sleep(3000);
        return new Date();
    }

    /**
     * value锁名称
     * waitTime等待超时：默认5秒,当waitTime<0，表示一直等待获取锁
     * leaseTime过期时长：默认10秒
     * timeUnit时长单位：默认秒
     */
    @FairLock(value = "fairLock2",waitTime = 10,leaseTime = 5,timeUnit = TimeUnit.SECONDS)
    @RequestMapping("fairLock2")
    public Date fairLock2() throws InterruptedException {
        Thread.sleep(3000);
        return new Date();
    }
}
