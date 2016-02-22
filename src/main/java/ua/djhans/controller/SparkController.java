package ua.djhans.controller;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * При создании экземпляра этого класса создается объект dataFrame класса DataFrame,
 * в который помещается содержимое таблицы contacts из базы данных PostgreSQL,
 * и далее хранится в паямти для быстрого доступа к данным.
 * При внесении изменений в таблицу contacts, изменния вносятся и в dataFrame.
 */

@Controller
public class SparkController {
    private DataFrame dataFrame;
    private String dataBaseHost;
    private String dataBasePort;
    private String dataBaseName;
    private String dataBaseUser;
    private String dataBasePassword;
    private String dataBaseTable;

    public SparkController(){
        JavaSparkContext sc = new JavaSparkContext(new SparkConf().setAppName("JavaSparkSQL").setMaster("local[*]"));
        SQLContext sqlContext = new SQLContext(sc);

        try {
            ResourceBundle bundle = ResourceBundle.getBundle("DBConnection");
            dataBaseHost = bundle.getString("host");
            dataBasePort = bundle.getString("port");
            dataBaseName = bundle.getString("database");
            dataBaseUser = bundle.getString("user");
            dataBasePassword = bundle.getString("password");
            dataBaseTable = bundle.getString("table");
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL JDBC Driver Registered!");
        } catch (ClassNotFoundException | MissingResourceException e) {
            e.printStackTrace();
        }

        Map<String, String> options = new HashMap<>();
        options.put("url", "jdbc:postgresql://" + dataBaseHost + ":" + dataBasePort +"/" +
                dataBaseName + "?user=" + dataBaseUser + "&password=" + dataBasePassword);
        options.put("dbtable", dataBaseTable);
        options.put("driver", "org.postgresql.Driver");

        dataFrame = sqlContext.read().format("jdbc").options(options).load();
    }

    @Bean
    public DataFrame getDataFrame() {
        return dataFrame;
    }
}
