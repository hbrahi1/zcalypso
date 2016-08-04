/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.calypso.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Repository;

import com.calypso.model.BusinessPartner;
import com.calypso.model.Contact;
import com.calypso.model.ContactType;
import com.calypso.model.Visit;
import com.calypso.repository.BusinessPartnerRepository;
import com.calypso.repository.ContactRepository;
import com.calypso.repository.VisitRepository;
import com.calypso.util.EntityUtils;

/**
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Sam Brannen
 * @author Thomas Risberg
 * @author Mark Fisher
 */
@Repository
public class JdbcContactRepositoryImpl implements ContactRepository {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private SimpleJdbcInsert insertContact;

    private BusinessPartnerRepository businessPartnerRepository;

    private VisitRepository visitRepository;


    @Autowired
    public JdbcContactRepositoryImpl(DataSource dataSource, BusinessPartnerRepository businessPartnerRepository, VisitRepository visitRepository) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

        this.insertContact = new SimpleJdbcInsert(dataSource)
                .withTableName("contacts")
                .usingGeneratedKeyColumns("id");

        this.businessPartnerRepository = businessPartnerRepository;
        this.visitRepository = visitRepository;
    }

    @Override
    public List<ContactType> findContactTypes() throws DataAccessException {
        Map<String, Object> params = new HashMap<String, Object>();
        return this.namedParameterJdbcTemplate.query(
                "SELECT id, name FROM types ORDER BY name",
                params,
                ParameterizedBeanPropertyRowMapper.newInstance(ContactType.class));
    }

    @Override
    public Contact findById(int id) throws DataAccessException {
        JdbcContact contact;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("id", id);
            contact = this.namedParameterJdbcTemplate.queryForObject(
                    "SELECT id, name, birth_date, type_id, businessPartner_id FROM contacts WHERE id=:id",
                    params,
                    new JdbcContactRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            throw new ObjectRetrievalFailureException(Contact.class, new Integer(id));
        }
        BusinessPartner businessPartner = this.businessPartnerRepository.findById(contact.getBusinessPartnerId());
        businessPartner.addContact(contact);
        contact.setType(EntityUtils.getById(findContactTypes(), ContactType.class, contact.getTypeId()));

        List<Visit> visits = this.visitRepository.findByContactId(contact.getId());
        for (Visit visit : visits) {
            contact.addVisit(visit);
        }
        return contact;
    }

    @Override
    public void save(Contact contact) throws DataAccessException {
        if (contact.isNew()) {
            Number newKey = this.insertContact.executeAndReturnKey(
                    createContactParameterSource(contact));
            contact.setId(newKey.intValue());
        } else {
            this.namedParameterJdbcTemplate.update(
                    "UPDATE contacts SET name=:name, birth_date=:birth_date, type_id=:type_id, " +
                            "businessPartner_id=:businessPartner_id WHERE id=:id",
                    createContactParameterSource(contact));
        }
    }

    /**
     * Creates a {@link MapSqlParameterSource} based on data values from the supplied {@link Contact} instance.
     */
    private MapSqlParameterSource createContactParameterSource(Contact contact) {
        return new MapSqlParameterSource()
                .addValue("id", contact.getId())
                .addValue("name", contact.getName())
                .addValue("birth_date", contact.getBirthDate().toDate())
                .addValue("type_id", contact.getType().getId())
                .addValue("businessPartner_id", contact.getBusinessPartner().getId());
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<Contact> findAll() throws DataAccessException {
		return this.namedParameterJdbcTemplate.query("SELECT * FROM contacts", new RowMapper<Contact>(){

			@Override
			public Contact mapRow(ResultSet rs, int idx) throws SQLException {
				Contact contact = new Contact();
				ContactType type = new ContactType();
				type.setId(rs.getInt("type_id"));
				contact.setName(rs.getString("name"));
				contact.setId(rs.getInt("id"));
				contact.setBirthDate(new DateTime(rs.getDate("birth_date")));
				contact.setType(type);
			
				return contact;
			}
			
		});
	}

	@Override
	public Collection<Contact> findByName(String query) throws DataAccessException {
		
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("query","%" + query + "%");
		
		return this.namedParameterJdbcTemplate.query("SELECT * FROM contacts WHERE UPPER(name) LIKE UPPER(:query)", paramMap, new RowMapper<Contact>(){

			@Override
			public Contact mapRow(ResultSet rs, int idx) throws SQLException {
				Contact contact = new Contact();
				ContactType type = new ContactType();
				type.setId(rs.getInt("type_id"));
				contact.setName(rs.getString("name"));
				contact.setId(rs.getInt("id"));
				contact.setBirthDate(new DateTime(rs.getDate("birth_date")));
				contact.setType(type);
			
				return contact;
			}
			
		});
	}

}
