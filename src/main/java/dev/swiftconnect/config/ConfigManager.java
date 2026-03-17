package dev.swiftconnect.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    private final Path configPath;
    private SwiftConfig config;

    public ConfigManager(Path dataDirectory) {
        this.configPath = dataDirectory.resolve("config.yml");
    }

    public SwiftConfig load() throws IOException {
        if (!Files.exists(configPath)) {
            return null;
        }
        try (Reader reader = Files.newBufferedReader(configPath)) {
            LoaderOptions loaderOptions = new LoaderOptions();
            Constructor constructor = new Constructor(SwiftConfig.class, loaderOptions);
            Yaml yaml = new Yaml(constructor);
            config = yaml.load(reader);
            return config;
        }
    }

    public void save(SwiftConfig config) throws IOException {
        this.config = config;
        Files.createDirectories(configPath.getParent());

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setIndent(2);
        dumperOptions.setIndicatorIndent(0);
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);

        Representer representer = new Representer(dumperOptions);
        representer.getPropertyUtils().setSkipMissingProperties(true);

        Yaml yaml = new Yaml(representer, dumperOptions);

        try (Writer writer = Files.newBufferedWriter(configPath)) {
            yaml.dump(config, writer);
        }
    }

    public SwiftConfig getConfig() {
        return config;
    }

    public Path getConfigPath() {
        return configPath;
    }
}
