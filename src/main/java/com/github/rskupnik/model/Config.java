package com.github.rskupnik.model;

import java.util.List;

public final class Config {

    private List<ConfigEntry> config;
    private List<Action> actions;
    private List<Handler> handlers;

    public List<ConfigEntry> getConfig() {
        return config;
    }

    public void setConfig(List<ConfigEntry> config) {
        this.config = config;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public List<Handler> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<Handler> handlers) {
        this.handlers = handlers;
    }
}
