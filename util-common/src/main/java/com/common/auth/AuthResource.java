package com.common.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Objects;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthResource {
    /**
     * 应用场景： 如果带有该注解，并且值为false不验证
     *
     * @return 是否参与验证
     */
    boolean value() default true;

    String resourceType() default API_RESOURCE;

    class Auth {
        /**
         * @param method 方法
         */
        public static AuthResult check(Method method) {
            Objects.requireNonNull(method, "方法不能为空");
            AuthResource annotation = method.getAnnotation(AuthResource.class);
            //根据应用场景，没有该注解必须验证
            if (annotation == null) {
                //不需要校验
                return new AuthResult(false, null);
            } else {
                return new AuthResult(annotation.value(), annotation.resourceType());
            }
        }
    }

    String API_RESOURCE = "API";

    class AuthResult {
        //api page
        private boolean needCheck;
        private String resourceType;

        AuthResult(boolean needCheck, String resourceType) {
            this.needCheck = needCheck;
            this.resourceType = resourceType;
        }

        public String resourceType() {
            return resourceType;
        }

        public boolean needCheck() {
            return needCheck;
        }

    }

}
