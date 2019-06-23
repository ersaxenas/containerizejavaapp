package com.lrn.containerize.dao;

import com.lrn.containerize.model.ContactDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.*;

@Component
public class ContactDao extends NamedParameterJdbcDaoSupport {
    private final Logger logger = LoggerFactory.getLogger(ContactDao.class);
    private final int defaultPageSize = 100;

    @Autowired
    DaoUtils daoUtils;

    @Autowired
    public ContactDao(@Qualifier("contactsdb") DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    private RowMapper<ContactDetails> rowMapper = (resultSet, i) -> {
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.setContactId(resultSet.getString("CONTACT_ID"));
        contactDetails.setUserName(resultSet.getString("NAME"));
        contactDetails.setAddress(resultSet.getString("ADDRESS"));
        contactDetails.setPhoneNo(resultSet.getString("PHONE"));
        contactDetails.setEmail(resultSet.getString("EMAIL"));
        contactDetails.setCreatedDate(resultSet.getDate("CREATED_DT"));
        contactDetails.setUpdatedDate(resultSet.getDate("UPDATED_DT"));
        return contactDetails;
    };

    public List<ContactDetails> getContacts(int pageSize, int pageNo) {
        String sql = "SELECT CONTACT_ID, NAME, ADDRESS, PHONE, EMAIL, CREATED_DT, UPDATED_DT FROM CONTACT_DETAILS ORDER BY CONTACT_ID LIMIT :LIMIT OFFSET :OFFSET";
        Integer limit = defaultPageSize;
        if(pageNo > 0 ) {
            limit = pageSize;
        }
        Integer start =0;
        if(pageNo > 1) {
            start = (pageNo * pageSize) - pageSize;
        }
        logger.debug("fetching contact details from database");
        Map<String, Integer> params = new HashMap<>(2);
        params.put("LIMIT", limit);
        params.put("OFFSET", start);
        logger.debug("sql = " + sql);
        List<ContactDetails> contactDetailsList = this.getNamedParameterJdbcTemplate().query(sql, params, rowMapper);
        if(!contactDetailsList.isEmpty()) {
            logger.debug("Fetched ROWS " + contactDetailsList.size() + " from database.");
            return contactDetailsList;
        }
        return new ArrayList<>();
    }

    public Optional<ContactDetails> getContactDetail(String userId) {
        String sql = "SELECT CONTACT_ID, NAME, ADDRESS, PHONE, EMAIL, CREATED_DT, UPDATED_DT FROM CONTACT_DETAILS WHERE CONTACT_ID=:userId";
        logger.debug("fetching contact details from database for user" + userId);
        Map<String, String> params = new HashMap<>(2);
        params.put("userId", userId);
        logger.debug("sql = " + sql);
        List<ContactDetails> contactDetailsList = this.getNamedParameterJdbcTemplate().query(sql, params, rowMapper);
        if(!contactDetailsList.isEmpty()) {
            logger.debug("Fetched ROWS " + contactDetailsList.size() + " from database.");
            return Optional.of(contactDetailsList.get(0));
        }
        return Optional.empty();
    }
}
