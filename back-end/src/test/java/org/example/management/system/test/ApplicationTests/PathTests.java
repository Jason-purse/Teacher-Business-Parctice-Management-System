package org.example.management.system.test.ApplicationTests;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathTests {

    @Test
    public void test() {
        Path path = Paths.get("../a", "b");
        System.out.println(path.toFile().getPath());

        System.out.println(path.toString());
    }
}
