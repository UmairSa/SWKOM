package org.App;

import org.springframework.stereotype.Service;

@Service
public class PaperlessService {

    public String getGreeting() {
        return "Methode1";
    }

    public String bye() {
        return "goodbye";
    }
}
