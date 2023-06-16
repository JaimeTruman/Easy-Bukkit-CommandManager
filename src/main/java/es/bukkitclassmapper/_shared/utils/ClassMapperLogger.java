package es.bukkitclassmapper._shared.utils;

import es.bukkitclassmapper.ClassMapperConfiguration;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class ClassMapperLogger {
    private final ClassMapperConfiguration configuration;

    public void debug(String message, Object... args) {
        if(configuration.isUseDebugLogging()){
            System.out.printf(message, args);
        }
    }

    public void info(String message, Object... args) {
        System.out.printf(message, args);
    }

    public void error(String message, Object... args) {
        System.err.printf(message, args);
    }
}
