package org.example.build_restaurant_system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
class ConnectDatabase{
    private static final Logger log = LoggerFactory.getLogger(ConnectDatabase.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @GetMapping("/connect")
    public void connect(){
        try{
            jdbcTemplate.queryForList("select 1", Integer.class);
            log.info("数据库连接成功！");
        } catch (DataAccessException e) {
            log.error("数据库连接失败，位于方法->connect()"+e.getMessage());
        }
    }
    public List<Map<String, Object>>getUserInformation(){
        String sql="select * from user_system";
        return jdbcTemplate.queryForList(sql);
    }
}
@RestController
public class RestaurantSystem {
   public static final Logger log = LoggerFactory.getLogger(RestaurantSystem.class);
   @Autowired
   private ConnectDatabase connectDatabase;
   @Autowired
   private JdbcTemplate jdbcTemplate;

    @GetMapping("/api/t")
    public List<Map<String,Object>>t(){
       return connectDatabase.getUserInformation();
    }
    @GetMapping("/api/add")
    public String add(String username,String user_type){
       String sql="insert into user_system(username,user_type) values (?,?)";
       username="林安ax";
       jdbcTemplate.update(sql,username,"老用户");
       return "添加成功！"+username;
    }
    @GetMapping("api/delete")
    public String delete(String username, String user_type) {
        // 固定要删除的用户
        username = "林安ax";
        user_type = "老用户";
        // 直接删除，返回影响的行数
        String deleteSql = "delete from user_system where username=? and user_type=?";
        int rows = jdbcTemplate.update(deleteSql, username, user_type);
        if (rows > 0) return "删除成功！共删除 " + rows + " 条记录，用户名：" + username;
        else return "删除失败！用户不存在或类型不匹配";
    }
}
