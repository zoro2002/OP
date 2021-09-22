package com.op.backend.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OpRepository {

    private JdbcTemplate jdbcTemplate;

    public void createTable(){
        var query="CREATE TABLE post(id SERIAL PRIMARY, name varchar(255) NOT NULL)";
        int update = this.jdbcTemplate.update(query);
        System.out.println(update);
    }
}
