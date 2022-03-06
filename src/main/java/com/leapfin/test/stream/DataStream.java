package com.leapfin.test.stream;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.ThreadLocalRandom;

public class DataStream implements Stream<String> {

    public String getData() {
        int length = ThreadLocalRandom.current().nextInt(1000);

        if (length == 0) {
            return "Lpfn";
        }

        return RandomStringUtils.randomAlphanumeric(length);
    }
}
