package com.zwb.springTransaction_test;

import javax.sql.DataSource;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App 
{
	//日志类
	private static final Logger log =LoggerFactory.getLogger(App.class);
	//相当于在xml中配置 
	@Bean
	ModelService modelService(){
		return new ModelService();
	}
	
	
	@Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) 
	{
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        log.info("Creating tables");
        jdbcTemplate.execute("drop table MODELS if exists");
        //只能存储最多20个字节  一个汉字两个字节   一个varchar 2个字节 16位
        jdbcTemplate.execute("create table MODELS("
                + "ID serial, MODEL_NAME varchar(10) NOT NULL)");
        return jdbcTemplate;
    }
	
	
	
    public static void main( String[] args )
    {
        ApplicationContext ac = SpringApplication.run(App.class, args);
        ModelService modelService = ac.getBean(ModelService.class);
        modelService.addModel("连铸模型一","钢制模型二","特殊模型三");
        //执行测试
        Assert.assertEquals("First add model should work with no problem", 3,
                modelService.findAllModels().size());
        try {
        	modelService.addModel("Chris","keys","钢制模型二啊哈哈哈哈哈");
        }
        catch (RuntimeException e) {
            log.info("v--- The following exception is expect because '钢制模型二啊哈哈哈哈' is too big for the DB ---v");
            log.error(e.getMessage());
        }
        for (String model : modelService.findAllModels()) {
            log.info("So far, " + model + " is added.");
        }
        log.info("You shouldn't see Chris or 钢制模型二啊哈哈哈哈. 钢制模型二啊哈哈哈哈  violated DB constraints, and Chris was rolled back in the same TX");
        Assert.assertEquals("'Samuel' should have triggered a rollback", 3,
                modelService.findAllModels().size());
        
        try {
            modelService.addModel("Buddy", null);
        }
        catch (RuntimeException e) {
            log.info("v--- The following exception is expect because null is not valid for the DB ---v");
            log.error(e.getMessage());
        }

        for (String model : modelService.findAllModels()) {
            log.info("So far, " + model + " is booked.");
        }
        log.info("You shouldn't see Buddy or null. null violated DB constraints, and Buddy was rolled back in the same TX");
        Assert.assertEquals("'null' should have triggered a rollback", 3, modelService
                .findAllModels().size());

        
    }
}
