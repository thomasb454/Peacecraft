package com.peacecraftec.module.cmd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    public String[] aliases();

    public String permission();

    public String usage() default "";

    public String desc();

    public int min() default 0;

    public int max() default -1;

    public boolean player() default true;

    public boolean console() default true;

    public boolean commandblock() default true;

}
