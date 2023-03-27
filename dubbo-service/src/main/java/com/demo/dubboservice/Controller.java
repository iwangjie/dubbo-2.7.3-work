package com.demo.dubboservice;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class Controller {

    public static int TIMEOUT = 0;
    public static boolean EXCEPTION = false;

    @RequestMapping("")
    public String config(int timeout,boolean exception){
        TIMEOUT = timeout;
        EXCEPTION = exception;
        return TIMEOUT+":"+EXCEPTION;

    }
}
