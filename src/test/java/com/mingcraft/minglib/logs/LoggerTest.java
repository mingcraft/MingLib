package com.mingcraft.minglib.logs;

import org.junit.jupiter.api.Test;

class LoggerTest {

    @Test
    public void log() {

        String key = "testLogger";
        String path = "test/test.log";

        Logger logger = Logger.getLogger(key, path);
        logger.log(Logger.LogType.MESSAGE, "테스트 로그입니다.");

    }

}