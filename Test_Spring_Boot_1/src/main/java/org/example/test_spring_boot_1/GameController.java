package org.example.test_spring_boot_1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    @GetMapping("/game")
    public String getGame(@RequestParam(value = "input", required = false) String userInput) {
        if (userInput == null) {
            return "请输入内容，示例：/game?input=你好";
        }
        return "你输入了：" + userInput;
    }
}