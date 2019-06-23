package com.lrn.containerize.controller;

import com.lrn.containerize.dao.ContactDao;
import com.lrn.containerize.model.ContactDetails;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class ContactDetailsController {
    private final Logger logger = LoggerFactory.getLogger(ContactDetailsController.class);

    @Autowired
    ContactDao contactDao;

    @RequestMapping(method = RequestMethod.GET, path = "/contact/{userId}", produces = "application/json")
    public ResponseEntity<ContactDetails> getContactDetail(@PathVariable String userId) {
        logger.debug("Fetching contact details for user id:" + userId);
        Optional<ContactDetails> contactDetail = contactDao.getContactDetail(userId);
        ResponseEntity<ContactDetails> responseEntity;
        if(contactDetail.isPresent()) {
            responseEntity =  ResponseEntity.ok().body(contactDetail.get());
        } else {
           responseEntity = ResponseEntity.notFound().build();
        }
        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/allcontacts", produces = "application/json")
    public ResponseEntity<List<ContactDetails>> getAllContactDetails(String pageSize, String pageNo) {
        Integer lPageSize = 100, lPageNo = 1;
        if(StringUtils.isNotBlank(pageSize) && NumberUtils.isDigits(pageSize)) {
            lPageSize = Integer.valueOf(pageSize);
        }
        if(StringUtils.isNotBlank(pageNo) && NumberUtils.isDigits(pageNo)) {
            lPageNo = Integer.valueOf(pageNo);
        }
        List<ContactDetails> contactDetailsList = contactDao.getContacts(lPageSize, lPageNo);
        ResponseEntity<List<ContactDetails>> responseEntity;
        if(!contactDetailsList.isEmpty()) {
            responseEntity =  ResponseEntity.ok().body(contactDetailsList);
        } else {
           responseEntity = ResponseEntity.notFound().build();
        }
        return responseEntity;
    }

}
