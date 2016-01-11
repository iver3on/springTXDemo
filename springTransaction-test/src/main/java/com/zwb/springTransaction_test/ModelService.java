package com.zwb.springTransaction_test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;


public class ModelService {
	private final static Logger log = LoggerFactory.getLogger(ModelService.class);
	
	//通过@AutoWired标注自动注入到Service中，这是一个进行数据库交互非常方便的辅助类。
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	//添加模型的操作，将每一个模型都添加到数据库中
	/*这个操作，要么将所有人的信息都插入，要么任何人的信息都不插入，这是一个典型的事务。事务如何处理呢？其实只有一行代码，将@Transactional标注到book方法即可。
	简单得都要哭了。代码中无数的try {beginTransaction()..} catch {rollback} finally 
	{commit}代码历历在目啊。*/
	@Transactional
	public void addModel(String... models){
		for(String model:models){
			log.info("add model"+model+"in db");
			jdbcTemplate.update("insert into MODELS(MODEL_NAME) values (?)",model);
		}
	}
	
	//获得所有的模型
	public List<String> findAllModels() {
        return jdbcTemplate.query("select MODEL_NAME from MODELS", new RowMapper<String>() {
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("MODEL_NAME");
            }
        });
    }
}
