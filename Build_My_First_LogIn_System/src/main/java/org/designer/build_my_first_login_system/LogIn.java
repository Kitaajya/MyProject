package org.designer.build_my_first_login_system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LogIn {
    @Autowired
    public JdbcTemplate jdbcTemplate;
    private Logger log = LoggerFactory.getLogger(LogIn.class);

    @GetMapping("/passwords")
    public String getPassword(@RequestParam String name, @RequestParam int password){
        String sql = "select password from log_table where name = ?";
        Integer realPwd = jdbcTemplate.queryForObject(sql, Integer.class, name);
        if (password == realPwd) {
            log.info("密码正确！");
            return "登陆成功！";
        } else {
            log.info("密码错误！");
            return "登陆失败！";
        }
    }
}
//http://localhost:8080/api/passwords