package com.peacecraftec.module.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Perm {

	public String desc();

	public PermissionDefault def() default PermissionDefault.OP;
	
}
