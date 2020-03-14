package com.springvuegradle.seng302team600.advice;

import com.springvuegradle.seng302team600.controller.UserController;
import com.springvuegradle.seng302team600.model.User;
import org.apache.commons.io.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonInputMessage;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class UserRequestBodyAdvice implements RequestBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        System.out.println("In supports() method of " + getClass().getSimpleName());
        return methodParameter.getContainingClass() == UserController.class && targetType.getTypeName() == User.class.getTypeName();
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        InputStream body = inputMessage.getBody();
        String bodyStr = IOUtils.toString(body, StandardCharsets.UTF_8);

        StringBuilder sb = new StringBuilder(bodyStr);
        int passwordIndex = bodyStr.indexOf("\"password\"");
        int primaryEmailIndex = bodyStr.indexOf("\"primary_email\"");

        sb.insert(passwordIndex - 4, "}]");
        sb.insert(primaryEmailIndex + 18, "[{\"email\": ");

        bodyStr = sb.toString();

        HttpInputMessage ret = new MappingJacksonInputMessage(new ByteArrayInputStream(bodyStr.getBytes()), inputMessage.getHeaders()); //set the updated bodyStr

        System.out.println("In beforeBodyRead() method of " + getClass().getSimpleName());
        return ret;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        System.out.println("In afterBodyRead() method of " + getClass().getSimpleName());
        if (body instanceof User) {
            User user = (User) body;
            return user;
        }
        return body;
    }
    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                  Class<? extends HttpMessageConverter<?>> converterType) {
        System.out.println("In handleEmptyBody() method of " + getClass().getSimpleName());
        return body;
    }
}
