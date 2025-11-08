package com.example.study.common.authentication.backoffice;

import com.example.study.common.authentication.fo.UnauthenticatedException;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class BackofficeAuthenticationArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == BackOfficeAuthentication.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {
        BackOfficeAuthentication authentication = BackofficeAuthenticationHolder.get();

        if (authentication == null) {
            throw new UnauthenticatedException();
        }

        return authentication;
    }
}