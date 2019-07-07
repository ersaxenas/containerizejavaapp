package com.lrn.containerize.listener;

import com.lrn.containerize.service.ContactDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ContactsTopicListener {
    private Logger logger = LoggerFactory.getLogger(ContactsTopicListener.class);

    @Autowired
    ContactDetailService contactDetailService;

    @KafkaListener(topics = "contacts_update", groupId = "restapp-group")
    public void onMessage(String message) {
       logger.debug("Received message :  {}", message);
        contactDetailService.insertOrUpdateContact(message);
    }
}
