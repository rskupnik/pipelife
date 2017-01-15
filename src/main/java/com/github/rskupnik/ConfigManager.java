package com.github.rskupnik;

import com.github.rskupnik.model.ConfigEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ConfigManager {

    private static ConfigManager INSTANCE;

    public static ConfigManager getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ConfigManager();

        return INSTANCE;
    }

    private Map<String, String> config = new HashMap<>();

    public void init(List<ConfigEntry> configEntries) {
        config = configEntries.stream()
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
    }

    public Optional<String> getConfigEntry(String key) {
        String output = config.get(key);
        return output != null ? Optional.of(output) : Optional.empty();
    }
}
