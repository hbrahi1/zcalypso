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
package com.calypso.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.core.style.ToStringCreator;

/**
 * Simple JavaBean domain object representing an businessPartner.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
@Entity
@Table(name = "businessPartners")
public class BusinessPartner extends Person {
    @Column(name = "address")
    @NotEmpty
    private String address;

    @Column(name = "city")
    @NotEmpty
    private String city;

    @Column(name = "telephone")
    @NotEmpty
    @Digits(fraction = 0, integer = 10)
    private String telephone;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "businessPartner")
    private Set<Contact> contacts;


    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    protected void setContactsInternal(Set<Contact> contacts) {
        this.contacts = contacts;
    }

    protected Set<Contact> getContactsInternal() {
        if (this.contacts == null) {
            this.contacts = new HashSet<Contact>();
        }
        return this.contacts;
    }

    public List<Contact> getContacts() {
        List<Contact> sortedContacts = new ArrayList<Contact>(getContactsInternal());
        PropertyComparator.sort(sortedContacts, new MutableSortDefinition("name", true, true));
        return Collections.unmodifiableList(sortedContacts);
    }

    public void addContact(Contact contact) {
        getContactsInternal().add(contact);
        contact.setBusinessPartner(this);
    }

    /**
     * Return the Contact with the given name, or null if none found for this BusinessPartner.
     *
     * @param name to test
     * @return true if contact name is already in use
     */
    public Contact getContact(String name) {
        return getContact(name, false);
    }

    /**
     * Return the Contact with the given name, or null if none found for this BusinessPartner.
     *
     * @param name to test
     * @return true if contact name is already in use
     */
    public Contact getContact(String name, boolean ignoreNew) {
        name = name.toLowerCase();
        for (Contact contact : getContactsInternal()) {
            if (!ignoreNew || !contact.isNew()) {
                String compName = contact.getName();
                compName = compName.toLowerCase();
                if (compName.equals(name)) {
                    return contact;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)

                .append("id", this.getId())
                .append("new", this.isNew())
                .append("lastName", this.getLastName())
                .append("firstName", this.getFirstName())
                .append("address", this.address)
                .append("city", this.city)
                .append("telephone", this.telephone)
                .toString();
    }
}
