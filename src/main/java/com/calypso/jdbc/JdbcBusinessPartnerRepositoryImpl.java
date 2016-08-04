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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Repository;

import com.calypso.model.BusinessPartner;
import com.calypso.model.Pet;
import com.calypso.model.PetType;
import com.calypso.model.Visit;
import com.calypso.repository.BusinessPartnerRepository;
import com.calypso.repository.VisitRepository;
import com.calypso.util.EntityUtils;

/**
 * A simple JDBC-based implementation of the {@link BusinessPartnerRepository} interface.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Sam Brannen
 * @author Thomas Risberg
 * @author Mark Fisher
 */
@Repository
public class JdbcBusinessPartnerRepositoryImpl implements BusinessPartnerRepository {

    private VisitRepository visitRepository;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private SimpleJdbcInsert insertBusinessPartner;

    @Autowired
    public JdbcBusinessPartnerRepositoryImpl(DataSource dataSource, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                   VisitRepository visitRepository) {

        this.insertBusinessPartner = new SimpleJdbcInsert(dataSource)
                .withTableName("businessPartners")
                .usingGeneratedKeyColumns("id");

        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

        this.visitRepository = visitRepository;
    }


    /**
     * Loads {@link BusinessPartner BusinessPartners} from the data store by last name, returning all businessPartners whose last name <i>starts</i> with
     * the given name; also loads the {@link Pet Pets} and {@link Visit Visits} for the corresponding businessPartners, if not
     * already loaded.
     */
    @Override
    public Collection<BusinessPartner> findByLastName(String lastName) throws DataAccessException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("lastName", lastName + "%");
        List<BusinessPartner> businessPartners = this.namedParameterJdbcTemplate.query(
                "SELECT id, first_name, last_name, address, city, telephone FROM businessPartners WHERE last_name like :lastName",
                params,
                ParameterizedBeanPropertyRowMapper.newInstance(BusinessPartner.class)
        );
        loadBusinessPartnersPetsAndVisits(businessPartners);
        return businessPartners;
    }

    /**
     * Loads the {@link BusinessPartner} with the supplied <code>id</code>; also loads the {@link Pet Pets} and {@link Visit Visits}
     * for the corresponding businessPartner, if not already loaded.
     */
    @Override
    public BusinessPartner findById(int id) throws DataAccessException {
        BusinessPartner businessPartner;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("id", id);
            businessPartner = this.namedParameterJdbcTemplate.queryForObject(
                    "SELECT id, first_name, last_name, address, city, telephone FROM businessPartners WHERE id= :id",
                    params,
                    ParameterizedBeanPropertyRowMapper.newInstance(BusinessPartner.class)
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new ObjectRetrievalFailureException(BusinessPartner.class, id);
        }
        loadPetsAndVisits(businessPartner);
        return businessPartner;
    }

    public void loadPetsAndVisits(final BusinessPartner businessPartner) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", businessPartner.getId().intValue());
        final List<JdbcPet> pets = this.namedParameterJdbcTemplate.query(
                "SELECT id, name, birth_date, type_id, businessPartner_id FROM pets WHERE businessPartner_id=:id",
                params,
                new JdbcPetRowMapper()
        );
        for (JdbcPet pet : pets) {
            businessPartner.addPet(pet);
            pet.setType(EntityUtils.getById(getPetTypes(), PetType.class, pet.getTypeId()));
            List<Visit> visits = this.visitRepository.findByPetId(pet.getId());
            for (Visit visit : visits) {
                pet.addVisit(visit);
            }
        }
    }

    @Override
    public void save(BusinessPartner businessPartner) throws DataAccessException {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(businessPartner);
        if (businessPartner.isNew()) {
            Number newKey = this.insertBusinessPartner.executeAndReturnKey(parameterSource);
            businessPartner.setId(newKey.intValue());
        } else {
            this.namedParameterJdbcTemplate.update(
                    "UPDATE businessPartners SET first_name=:firstName, last_name=:lastName, address=:address, " +
                            "city=:city, telephone=:telephone WHERE id=:id",
                    parameterSource);
        }
    }

    public Collection<PetType> getPetTypes() throws DataAccessException {
        return this.namedParameterJdbcTemplate.query(
                "SELECT id, name FROM types ORDER BY name", new HashMap<String, Object>(),
                ParameterizedBeanPropertyRowMapper.newInstance(PetType.class));
    }

    /**
     * Loads the {@link Pet} and {@link Visit} data for the supplied {@link List} of {@link BusinessPartner BusinessPartners}.
     *
     * @param businessPartners the list of businessPartners for whom the pet and visit data should be loaded
     * @see #loadPetsAndVisits(BusinessPartner)
     */
    private void loadBusinessPartnersPetsAndVisits(List<BusinessPartner> businessPartners) {
        for (BusinessPartner businessPartner : businessPartners) {
            loadPetsAndVisits(businessPartner);
        }
    }


}
