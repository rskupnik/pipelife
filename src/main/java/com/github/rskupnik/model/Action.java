package com.github.rskupnik.model;

public final class Action {

    public enum ActionScope {
        BUILT_IN, PROVIDED;
    }

    private String id;
    private ActionScope scope;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ActionScope getScope() {
        return scope;
    }

    public void setScope(ActionScope scope) {
        this.scope = scope;
    }
}
