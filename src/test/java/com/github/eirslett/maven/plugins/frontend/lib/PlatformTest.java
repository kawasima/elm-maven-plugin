package com.github.eirslett.maven.plugins.frontend.lib;

import org.junit.jupiter.api.Test;

class PlatformTest {
    @Test
    void test() {
        Platform p = Platform.guess();
        System.out.println(p);
    }
}
