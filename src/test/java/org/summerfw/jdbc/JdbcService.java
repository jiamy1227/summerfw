package org.summerfw.jdbc;

import org.summerfw.annotataion.Autowired;
import org.summerfw.annotataion.Component;

/**
 * @author: jiamy
 * @create: 2025/1/10 11:18
 **/
@Component
public class JdbcService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public String search(String sql, String[] args){
        return jdbcTemplate.queryForObject(sql, String.class, args);
    }
}
