package com.supermap.command;

public record CommandResult(int exitCode, String stdout, String stderr) {

    public boolean success() {
        return exitCode == 0;
    }

}