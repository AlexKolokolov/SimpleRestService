package ua.djhans.controller;

import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ua.djhans.model.Contact;

import java.util.*;

/**
 * При создании экземпляра этого класса создается объект dataFrame класса DataFrame,
 * в который помещается содержимое таблицы contacts из базы данных PostgreSQL,
 * и далее хранится в паямти для быстрого доступа к данным.
 * При внесении изменений в таблицу cntacts, изменния вносятся и в dataFrame.
 */

@Controller
public class SparkDAO implements DAO {

    @Autowired
    private DataFrame dataFrame;

    public void setDataFrame(DataFrame dataFrame) {
        this.dataFrame = dataFrame;
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
