package ua.djhans.controller;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import ua.djhans.model.Contact;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 22.01.2016.
 */
public class SparkController {
    private static SparkController instance = new SparkController();
    private DataFrame dataFrame;

    private SparkController(){
        JavaSparkContext sc = new JavaSparkContext(new SparkConf().setAppName("JavaSparkSQL").setMaster("local[*]"));
        SQLContext sqlContext = new SQLContext(sc);

        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL JDBC Driver Registered!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Map<String, String> options = new HashMap<String, String>();
        options.put("url", "jdbc:postgresql://localhost:5432/contacts?user=postgres&password=7ktndNb,tnt");
        options.put("dbtable", "contacts");
        options.put("driver", "org.postgresql.Driver");

        dataFrame = sqlContext.read().format("jdbc").options(options).load();
    }

    public static SparkController getInstance() {
        return instance;
    }

    public DataFrame filterData(String regEx) {
        return dataFrame.filter(dataFrame.col("name").rlike(invertRegEx(regEx))).limit(100);
    }

    public Contact[] getContacts(String regEx) {
        List<Row> dataList = getInstance().filterData(regEx).collectAsList();
        Contact[] contacts = new Contact[dataList.size()];
        for (int i = 0; i < dataList.size() ; i++) {
            contacts[i] = new Contact(dataList.get(i).getLong(0), dataList.get(i).getString(1));
        }
        return contacts;
    }

    private static String invertRegEx(String regEx) {
        return "^(?!" + regEx + "$).*$";
    }

    public static void main(String[] args) {
        Contact[] contacts = getInstance().getContacts("^J.*$");
        for (Contact contact : contacts) System.out.println(contact);
    }
}
