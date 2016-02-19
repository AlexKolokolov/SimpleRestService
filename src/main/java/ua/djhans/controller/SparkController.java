package ua.djhans.controller;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import org.springframework.stereotype.Controller;

import ua.djhans.model.Contact;

import java.util.HashMap;
import java.util.List;
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
public class SparkController implements DAO {
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

    /**
     * Метод принимает на вход регулярное выражение и возвращает объект класса DataFrame,
     * в котором не содержится записей, совпадающих с регулярным выражением.
     * При этом установлен лимит на количество записей в результирущем наборе в размере 100 шт.
     */
    private DataFrame filterData(String regEx) {
        return dataFrame.filter(dataFrame.col("name").rlike(invertRegEx(regEx))).limit(100);
    }

    /**
     * Метод принимает на вход регулярное выражение и возвращает массив объектов Contact,
     * у которых поле name не совпадает с ргулярным выражением. Массив содержит максимум 100 элементов.
     */
    @Override
    public Contact[] getFilteredContacts(String regEx) {
        List<Row> dataList = filterData(regEx).collectAsList();
        Contact[] contacts = new Contact[dataList.size()];
        for (int i = 0; i < dataList.size(); i++) {
            contacts[i] = new Contact(dataList.get(i).getLong(0), dataList.get(i).getString(1));
        }
        return contacts;
    }

    /**
     * Метод принимает на вход регулярное выражение и возвращает регулярное выражение,
     * обратное принятому.
     */
    private static String invertRegEx(String regEx) {
        return "^(?!" + regEx + "$).*$";
    }
}
