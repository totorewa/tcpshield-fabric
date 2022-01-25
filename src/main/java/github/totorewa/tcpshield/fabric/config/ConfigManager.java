package github.totorewa.tcpshield.fabric.config;

import net.tcpshield.tcpshield.provider.ConfigProvider;
import net.tcpshield.tcpshield.util.exception.config.ConfigLoadException;
import net.tcpshield.tcpshield.util.exception.config.ConfigReloadException;
import net.tcpshield.tcpshield.util.exception.config.ConfigResetException;
import net.tcpshield.tcpshield.util.exception.phase.ConfigException;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ConfigManager extends ConfigProvider {
    private static final String FILENAME = "tcpshield.yml";
    private Map<String, Object> loaded;

    public ConfigManager(final Path configDir) {
        this.configFile = configDir.resolve(FILENAME).toFile();
        this.dataFolder = this.configFile.getParentFile();
    }

    @Override
    protected void checkNodes(String... nodes) throws ConfigException {
        if (loaded == null) {
            throw new ConfigException("Could not parse the config file.");
        }
        for (String node : nodes) {
            if (!loaded.containsKey(node))
                throw new ConfigException("The node \"" + node + "\" does not exist in the config.");
        }
    }

    @Override
    protected void reset() throws ConfigResetException {
        this.loaded = null;
        if (this.configFile.exists()) {
            this.configFile.delete();
        }

        try (InputStream in = ConfigManager.class.getResourceAsStream("/" + FILENAME)) {
            Files.copy(in, this.configFile.toPath());
        } catch (IOException e) {
            throw new ConfigResetException(e);
        }
    }

    @Override
    protected void load() throws ConfigLoadException {
        Yaml yaml = new Yaml();
        try {
            try (InputStream in = new FileInputStream(this.configFile)) {
                this.loaded = yaml.load(in);
            }

            this.checkNodes("only-allow-proxy-connections", "timestamp-validation", "debug-mode");

            this.onlyProxy = (boolean) this.loaded.getOrDefault("only-allow-proxy-connections", true);
            this.doDebug = (boolean) this.loaded.getOrDefault("debug-mode", false);
            this.timestampValidationMode = (String) this.loaded.getOrDefault("timestamp-validation", "htpdate");
        } catch (Exception e) {
            throw new ConfigLoadException(e);
        }
    }

    @Override
    public void reload() throws ConfigReloadException {
        this.reload(false);
    }

    private void reload(boolean fileReset) throws ConfigReloadException {
        try {
            if (!this.dataFolder.exists()) {
                this.dataFolder.mkdir();
            }

            if (!this.configFile.exists()) {
                this.reset();
            }

            try {
                this.load();
            } catch (ConfigLoadException e) {
                if (fileReset) {
                    throw new ConfigResetException(e);
                }
                this.reset();
                this.reload(true);
            }
        } catch (Exception e) {
            throw new ConfigReloadException(e);
        }
    }
}
