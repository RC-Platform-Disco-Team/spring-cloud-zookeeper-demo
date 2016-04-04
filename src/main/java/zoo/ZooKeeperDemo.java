package zoo;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableAutoConfiguration
@EnableDiscoveryClient
@RestController
@EnableFeignClients
public class ZooKeeperDemo {

    @Autowired
    private CuratorFramework curator;
    private static final String PATH = "/global/lock";
    private final InterProcessMutex lock = new InterProcessMutex(curator, PATH);
    @Autowired
    private Client appClient;

    @FeignClient(name = "testZookeeperApp", url = "${feign.url}")
    private interface Client {

        @RequestMapping(path = "/id", method = RequestMethod.GET)
        Integer id();
    }

    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    String getId() throws Exception {
        try {
            if (!lock.acquire(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("could not acquire the lock");
            }
            return appClient.id().toString();
        } finally {
            lock.release();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ZooKeeperDemo.class, args);
    }

}