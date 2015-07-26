package io.peet.hubsub.config;

import org.apache.commons.cli.Options;

public class ConfigProvider {

    protected Options options;

    protected ConfigProvider() {
    }

    public static ConfigProvider parse(String[] args) {
        return new ConfigProvider();
    }
}
