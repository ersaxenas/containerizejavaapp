package com.lrn.containerize.controller;

import com.lrn.containerize.model.UserDetails;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactDetailsController {


    @RequestMapping(method = RequestMethod.GET, path = "/contact", produces = "application/json")
    public UserDetails getContactDetails(String userId) {
        UserDetails userDetails = new UserDetails()
                .withUserId("1")
                .withUserName("Test")
                .withAddress("Chicago")
                .withEmail("test@email");
        return userDetails;
    }

}
