package org.App;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaperlessController {

    @Autowired
    PaperlessService paperlessService;

    @GetMapping("/hello")
    public String hello() {
        return paperlessService.getGreeting();
    }

    @GetMapping("/bye")
    public String bye() {
        return paperlessService.bye();
    }

}