package ua.djhans.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.djhans.model.Contact;

/**
 * Created by Administrator on 23.01.2016.
 */
@RequestMapping("/hello")
@RestController
public class RestServiceController {
    @RequestMapping("/contacts")
    public Contact[] getContacts(@RequestParam(value="nameFilter", defaultValue="") String regEx) {
        return SparkController.getInstance().getContacts(regEx);
    }
}

