package com.mabo.controller;

import com.mabo.redis.easycode.readWriteLock.ReadWriteLock;
import com.mabo.redis.easycode.readWriteLock.ReadWriteLockType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.text.SimpleDateFormat;
import java.util.Date;
@RestController
public class ReadWriteLockController {
    private static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @ReadWriteLock(value = "readWrite",lockType = ReadWriteLockType.READLOCK)
    @RequestMapping("read")
    public String read() throws InterruptedException {
        System.out.println("开始执行read");
        Thread.sleep(100000);
        System.out.println("read执行结束");
        return sdf.format(new Date());
    }

    /**
     * value锁名称
     * waitTime等待超时：默认5秒,当waitTime<0，表示一直等待获取锁
     * leaseTime过期时长：默认10秒
     * timeUnit时长单位：默认秒
     */
    @ReadWriteLock(value = "readWrite",lockType = ReadWriteLockType.WRITELOCK)
    @RequestMapping("write")
    public String write() throws InterruptedException {
        Thread.sleep(3000);
        return sdf.format(new Date());
    }
}
