package ua.djhans.controller;

import ua.djhans.model.Contact;

/**
 * Created by Administrator on 16.02.2016.
 */
public interface DAO {
    Contact[] getFilteredContacts(String regEx);
}
