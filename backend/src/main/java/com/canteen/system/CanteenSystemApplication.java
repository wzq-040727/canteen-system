package com.canteen.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.canteen.system.mapper")
public class CanteenSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(CanteenSystemApplication.class, args);
    }
}
