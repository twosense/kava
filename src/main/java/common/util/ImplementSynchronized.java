package common.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* Denotes a method that should be implemented as "synchronized" */
@Retention(RetentionPolicy.SOURCE)
public @interface ImplementSynchronized { }
