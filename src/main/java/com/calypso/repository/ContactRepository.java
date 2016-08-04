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
package com.calypso.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.calypso.model.BaseEntity;
import com.calypso.model.Contact;
import com.calypso.model.ContactType;

/**
 * Repository class for <code>Contact</code> domain objects All method names are compliant with Spring Data naming
 * conventions so this interface can easily be extended for Spring Data See here: http://static.springsource.org/spring-data/jpa/docs/current/reference/html/jpa.repositories.html#jpa.query-methods.query-creation
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
public interface ContactRepository {

    /**
     * Retrieve all <code>ContactType</code>s from the data store.
     *
     * @return a <code>Collection</code> of <code>ContactType</code>s
     */
    List<ContactType> findContactTypes() throws DataAccessException;

    /**
     * Retrieve a <code>Contact</code> from the data store by id.
     *
     * @param id the id to search for
     * @return the <code>Contact</code> if found
     * @throws org.springframework.dao.DataRetrievalFailureException
     *          if not found
     */
    Contact findById(int id) throws DataAccessException;

    /**
     * Save a <code>Contact</code> to the data store, either inserting or updating it.
     *
     * @param contact the <code>Contact</code> to save
     * @see BaseEntity#isNew
     */
    void save(Contact contact) throws DataAccessException;
    
    /**
     * Get all contacts
     * @return 
     *
     */
    Collection<Contact> findAll() throws DataAccessException;

    /**
     * Get all contacts by name
     * @return 
     *
     */
    Collection<Contact> findByName(String query) throws DataAccessException;

}
