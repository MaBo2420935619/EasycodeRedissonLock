# EasycodeRedissonLock
一个注解替换synchronized关键字：分布式场景下实现方法加锁


# 一、前言

> 单机部署场景下，当我们需要锁住少量代码块或者方法，通常会使用synchronized关键字进行加锁，如下所示

```java
public synchronized void test(){
}

public  void test2(){
    Object o = new Object();
    synchronized (o){
        
    }
}
```


> 但是，在分布式场景下，由于jvm之间无法通信，一个服务是无法感知另外一个服务对当前代码快加锁。
>
> 该demo实现了分布式场景下，使用一个注解，对方法进行加锁。单机也可以使用。

# 二 、实现原理
## 1.原理
**利用Aspect的Around方法，对需要加锁的方法进行动态代理
在方法执行前获取Redisson锁对象，获取成功，方法执行。执行成功后释放锁
获取规则根据注解的相关参数进行设置**。

**用Redisson进行加锁**
## 2.注解参数描述
- value锁名称
- waitTime等待超时：默认5秒,当waitTime<0，表示一直等待获取锁
- leaseTime过期时长：默认10秒
- timeUnit时长单位：默认单位秒


# 三、公平锁注解（@FairLock）
@FairLock实现了对接口fairLock加锁，知道该接口当前请求结束后，才可以再次被请求

```java
@FairLock("fairLock")
    @RequestMapping("fairLock")
    public Date fairLock() throws InterruptedException {
        Thread.sleep(3000);
        return new Date();
    }
```



# 四、读写锁注解（@ReadWriteLock）

@ReadWriteLock(value = "readWrite",lockType = ReadWriteLockType.WRITELOCK)
如下图所示，用@ReadWriteLock对方法进行加读写锁，锁名称为readWrite，类型为读锁

![在这里插入图片描述](https://img-blog.csdnimg.cn/5c38193a16ac42c78f27b0055eb5c4c2.jpeg)

```java
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
```


# 五、下载地址
github下载地址:  [github](https://github.com/MaBo2420935619/EasycodeRedissonLock)
https://github.com/MaBo2420935619/EasycodeRedissonLock
