package com.leep.security.hedge.injection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Injection {
    boolean checkAllFields() default true;
    String[] exclude() default {};
    boolean checkSql() default true;
    boolean checkOs() default false;
    int maxChar() default -1;
    UserValidation standard() default UserValidation.NONE;
}
