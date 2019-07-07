package com.lrn.containerize.service;

import com.google.gson.Gson;
import com.lrn.containerize.dao.ContactDao;
import com.lrn.containerize.model.ContactDetails;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ContactDetailService {
    private final Logger logger = LoggerFactory.getLogger(ContactDetailService.class);

    @Autowired
    ContactDao contactDao;

    private Gson gson = new Gson();

    public List<ContactDetails> getContacts(int pageSize, int pageNo) {
     return contactDao.getContacts(pageSize, pageNo);
    }

    public Optional<ContactDetails> getContact(String contactId) {
      return contactDao.getContactDetail(contactId);
    }

    public void getContactByName(String name) {
         throw new UnsupportedOperationException("Get contact by name is not supported.");
    }

    public void getContactByPhone(String phone) {
        throw new UnsupportedOperationException("Get contact by phone is not supported.");
    }

    public boolean insertOrUpdateContact(String contactJson) {
        if(StringUtils.isBlank(contactJson)) {
            return false;
        }
        final ContactDetails contactDetails = gson.fromJson(contactJson, ContactDetails.class);
        contactDao.saveOrUpdateContact(contactDetails);
        return true;
    }

}
