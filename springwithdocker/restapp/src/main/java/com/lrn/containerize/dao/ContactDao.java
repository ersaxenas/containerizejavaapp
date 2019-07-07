package com.lrn.containerize.dao;

import com.lrn.containerize.exception.ServiceException;
import com.lrn.containerize.model.ContactDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
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
        List<ContactDetails> contactDetailsList = null;
        try {
            contactDetailsList = this.getNamedParameterJdbcTemplate().query(sql, params, rowMapper);
        } catch (DataAccessException exp) {
            logger.error("Failed to fetch contact details from database. " + exp.getLocalizedMessage(), exp);
        }
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
        List<ContactDetails> contactDetailsList = null;
        try {
            contactDetailsList = this.getNamedParameterJdbcTemplate().query(sql, params, rowMapper);
            if(!contactDetailsList.isEmpty()) {
                logger.debug("Fetched ROWS " + contactDetailsList.size() + " from database.");
                return Optional.of(contactDetailsList.get(0));
            }
        } catch (DataAccessException exp) {
          logger.error("Failed to fetch contact details from database. " + exp.getLocalizedMessage(), exp);
        }

        return Optional.empty();
    }

    public void saveOrUpdateContact(ContactDetails contactDetails) {
        String sql = "MERGE INTO CONTACT_DETAILS (CONTACT_ID, NAME, ADDRESS, PHONE, EMAIL)  KEY (CONTACT_ID) VALUES (:userId, :name, :address, :phone, :email)";
        logger.debug("Saving contact to database. UserId :" + contactDetails.getContactId());
        Map<String, String> params = new HashMap<>(5);
        params.put("userId", contactDetails.getContactId());
        params.put("name", contactDetails.getUserName());
        params.put("address", contactDetails.getAddress());
        params.put("phone", contactDetails.getPhoneNo());
        params.put("email", contactDetails.getEmail());
        logger.debug("sql = " + sql);
        try {
            this.getNamedParameterJdbcTemplate().update(sql, params);
        } catch (DataAccessException exp) {
            logger.error("Failed to persist contact to database: UserId: "+ contactDetails.getContactId() + " : "+ exp.getLocalizedMessage(), exp);
            throw new ServiceException("Failed to save contact").addContextValue("userId", contactDetails.getContactId());
        }
    }
}
