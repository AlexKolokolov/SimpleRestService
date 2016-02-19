package ua.djhans.boot;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.djhans.controller.DAO;
import ua.djhans.model.Contact;

/**
 * Контроллер REST сервиса. При обращенни к нему по URI localhost:8080/hello/contacts
 * выдает JSON массив из 100 контактов из базы данных (первых по id).
 * При добавлении к URI параметра nameFilter=regex (regex - регулярное выражение),
 * массив будет содержать только те контакты из базы данных, в которых поле name не совпадает
 * с заданным регулярным выражением. Их количество также не будет превышать 100 штук.
 */
@RequestMapping("/hello")
@RestController
public class RestServiceController {

    @Autowired
    private DAO dao;

    @RequestMapping("/contacts")
    public Contact[] getContacts(@RequestParam(value="nameFilter", defaultValue="") String regEx) {
        return dao.getFilteredContacts(regEx);
    }
}

