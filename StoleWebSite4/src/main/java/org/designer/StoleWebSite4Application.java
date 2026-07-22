package org.designer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StoleWebSite4Application {

    private static final Logger log = LoggerFactory.getLogger(StoleWebSite4Application.class);

    public static void main(String[] args) {
        SpringApplication.run(StoleWebSite4Application.class, args);
        log.info("攻击https://593937.kkxk52.shop/play/450936-1-1/");
        log.info("http://localhost:8081。");
    }

}
