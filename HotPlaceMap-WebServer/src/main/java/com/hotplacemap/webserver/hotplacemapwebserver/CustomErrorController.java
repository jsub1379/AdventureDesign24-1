package com.hotplacemap.webserver.hotplacemapwebserver;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute("javax.servlet.error.status_code");
        Object message = request.getAttribute("javax.servlet.error.message");
        Object exception = request.getAttribute("javax.servlet.error.exception");

        // Log or display the error details
        System.out.println("Error status code: " + status);
        System.out.println("Error message: " + message);
        if (exception != null) {
            System.out.println("Exception: " + exception);
        }

        // Return a custom error view
        return "error";
    }
}
