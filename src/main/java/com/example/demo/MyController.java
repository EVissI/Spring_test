package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyController {

    @GetMapping("/json")
    @ResponseBody
    public Json hello() {
        String test_1 = "{\"last name\":\"иванов\",\"address\":{\"city\":\"Ленинград\",\"postalCode\":101101,\"street Adress\":\"Московское ш., 101, кв.101\"},\"first name\":\"иван\",\"phoneNumbers\":[\"812 123-1234\",\"916 123-4567\"]}";
        return new Json(test_1);
    }

}