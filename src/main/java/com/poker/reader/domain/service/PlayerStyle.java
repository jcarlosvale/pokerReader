package com.poker.reader.domain.service;

public enum PlayerStyle{
    NONE("bg-transparent"),
    LIMPER("bg-warning"),
    TIGHT("bg-success"),
    FREE_BLIND("bg-info"),
    CBET_FOLD("bg-info"),
    SUPER_TIGHT("bg-primary"),
    AGGRESSIVE("bg-danger"),
    LOOSE("bg-danger"),
    SUPER_LOOSE("bg-danger");

    private String css;
    PlayerStyle(String css) {
        this.css = css;
    }

    public String getCss() {
        return css;
    }
}
