package com.leep.security.hedge.injection;

import org.springframework.boot.logging.LogLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SQLInjectionControl {
    UserValidation maxChar() default UserValidation.UUID;
    int customMaxChar() default -1;
    LogLevel logLevel() default LogLevel.INFO;

    enum UserValidation {
        UUID(36);

        private final int maxChar;

        UserValidation(int maxChar) {
            this.maxChar = maxChar;
        }

        public int getMaxChar() {
            return this.maxChar;
        }
    }
}