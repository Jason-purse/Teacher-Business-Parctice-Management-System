package org.example.management.system.test.ApplicationTests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.system.SystemProperties;

public class SystemPropertiesTests {
    @Test
    public void test() {
        System.getProperties().forEach((key,value) -> {
            System.out.println("key " + key + " value " + value);
        });
    }
}
