package springsideproject1.springsideproject1production.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserMainPageController {

    @GetMapping("/")
    public String mainPage() {
        return "user/userMainPage";
    }
}