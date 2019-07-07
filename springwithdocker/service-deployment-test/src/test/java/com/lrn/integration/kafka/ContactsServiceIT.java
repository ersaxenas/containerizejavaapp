package com.lrn.integration.kafka;

import static org.junit.Assert.*;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ContactsServiceIT {
    private Logger logger = LoggerFactory.getLogger(ContactsServiceIT.class);

    @Test
    public void testContactsService() throws IOException {
        logger.info("Integration test is running ..................................................");
        HttpUriRequest request = new HttpGet("http://localhost:8090/contact/10");
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
        assertNotNull("Failed to receive response from contact service",httpResponse);
        assertEquals("Http response is not 200.",200,httpResponse.getStatusLine().getStatusCode());
        String expectedResponse = "{\"contactId\":\"10\",\"userName\":\"Julianna Bennett\",\"phoneNo\":\"8-664-534-0760\",\"address\":\"Philadelphia\",\"email\":\"Julianna_Bennett7016@zorer.org\",\"createdDate\":\"2019-07-07\",\"updatedDate\":\"2019-07-07\"}";
        try (InputStream contentStream = httpResponse.getEntity().getContent()) {
            final String jsonText = IOUtils.toString(contentStream, StandardCharsets.UTF_8);
            logger.info("Response received :" + jsonText);
            assertEquals("Incorrect JSON response.",expectedResponse, jsonText);
        }

    }
}
