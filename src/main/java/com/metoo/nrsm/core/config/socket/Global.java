package com.metoo.nrsm.core.config.socket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-03-28 10:43
 */
@Component
public class Global {

    public static String env;

    @Value("${spring.profiles.active}")
    public void setEnv(String env) {
        Global.env = env;
    }

    public Global() {}

    private static Global global = new Global();

    public static Global getInstance() {
        return global;
    }
}
