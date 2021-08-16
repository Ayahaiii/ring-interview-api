package com.monetware.ringinterview.system.authorize;

import java.lang.annotation.*;

/**
 * @author Simo
 * @date 2020-02-20
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MonetwareAuthorize {

    String role() default "";

}
