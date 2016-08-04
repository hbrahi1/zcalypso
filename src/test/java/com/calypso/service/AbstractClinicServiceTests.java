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
package com.calypso.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.calypso.model.BusinessPartner;
import com.calypso.model.Contact;
import com.calypso.model.ContactType;
import com.calypso.model.Vet;
import com.calypso.model.Visit;
import com.calypso.service.CalypsoService;
import com.calypso.util.EntityUtils;

/**
 * <p> Base class for {@link CalypsoService} integration tests. </p> <p> Subclasses should specify Spring context
 * configuration using {@link ContextConfiguration @ContextConfiguration} annotation </p> <p>
 * AbstractcalypsoServiceTests and its subclasses benefit from the following services provided by the Spring
 * TestContext Framework: </p> <ul> <li><strong>Spring IoC container caching</strong> which spares us unnecessary set up
 * time between test execution.</li> <li><strong>Dependency Injection</strong> of test fixture instances, meaning that
 * we don't need to perform application context lookups. See the use of {@link Autowired @Autowired} on the <code>{@link
 * AbstractcalypsoServiceTests#calypsoService calypsoService}</code> instance variable, which uses autowiring <em>by
 * type</em>. <li><strong>Transaction management</strong>, meaning each test method is executed in its own transaction,
 * which is automatically rolled back by default. Thus, even if tests insert or otherwise change database state, there
 * is no need for a teardown or cleanup script. <li> An {@link org.springframework.context.ApplicationContext
 * ApplicationContext} is also inherited and can be used for explicit bean lookup if necessary. </li> </ul>
 *
 * @author Ken Krebs
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
public abstract class AbstractClinicServiceTests {

    @Autowired
    protected CalypsoService calypsoService;

    @Test
    @Transactional
    public void findBusinessPartners() {
        Collection<BusinessPartner> businessPartners = this.calypsoService.findBusinessPartnerByLastName("Davis");
        assertEquals(2, businessPartners.size());
        businessPartners = this.calypsoService.findBusinessPartnerByLastName("Daviss");
        assertEquals(0, businessPartners.size());
    }

    @Test
    public void findSingleBusinessPartner() {
        BusinessPartner businessPartner1 = this.calypsoService.findBusinessPartnerById(1);
        assertTrue(businessPartner1.getLastName().startsWith("Franklin"));
        BusinessPartner businessPartner10 = this.calypsoService.findBusinessPartnerById(10);
        assertEquals("Carlos", businessPartner10.getFirstName());

        assertEquals(businessPartner1.getContacts().size(), 1);
    }

    @Test
    @Transactional
    public void insertBusinessPartner() {
        Collection<BusinessPartner> businessPartners = this.calypsoService.findBusinessPartnerByLastName("Schultz");
        int found = businessPartners.size();
        BusinessPartner businessPartner = new BusinessPartner();
        businessPartner.setFirstName("Sam");
        businessPartner.setLastName("Schultz");
        businessPartner.setAddress("4, Evans Street");
        businessPartner.setCity("Wollongong");
        businessPartner.setTelephone("4444444444");
        this.calypsoService.saveBusinessPartner(businessPartner);
        Assert.assertNotEquals("BusinessPartner Id should have been generated", businessPartner.getId().longValue(), 0);
        businessPartners = this.calypsoService.findBusinessPartnerByLastName("Schultz");
        assertEquals("Verifying number of businessPartners after inserting a new one.", found + 1, businessPartners.size());
    }

    @Test
    @Transactional
    public void updateBusinessPartner() throws Exception {
        BusinessPartner o1 = this.calypsoService.findBusinessPartnerById(1);
        String old = o1.getLastName();
        o1.setLastName(old + "X");
        this.calypsoService.saveBusinessPartner(o1);
        o1 = this.calypsoService.findBusinessPartnerById(1);
        assertEquals(old + "X", o1.getLastName());
    }

	@Test
	public void findContact() {
	    Collection<ContactType> types = this.calypsoService.findContactTypes();
	    Contact contact7 = this.calypsoService.findContactById(7);
	    assertTrue(contact7.getName().startsWith("Samantha"));
	    assertEquals(EntityUtils.getById(types, ContactType.class, 1).getId(), contact7.getType().getId());
	    assertEquals("Jean", contact7.getBusinessPartner().getFirstName());
	    Contact contact6 = this.calypsoService.findContactById(6);
	    assertEquals("George", contact6.getName());
	    assertEquals(EntityUtils.getById(types, ContactType.class, 4).getId(), contact6.getType().getId());
	    assertEquals("Contacter", contact6.getBusinessPartner().getFirstName());
	}

	@Test
	public void getContactTypes() {
	    Collection<ContactType> contactTypes = this.calypsoService.findContactTypes();
	
	    ContactType contactType1 = EntityUtils.getById(contactTypes, ContactType.class, 1);
	    assertEquals("cat", contactType1.getName());
	    ContactType contactType4 = EntityUtils.getById(contactTypes, ContactType.class, 4);
	    assertEquals("snake", contactType4.getName());
	}

	@Test
	@Transactional
	public void insertContact() {
	    BusinessPartner businessPartner6 = this.calypsoService.findBusinessPartnerById(6);
	    int found = businessPartner6.getContacts().size();
	    Contact contact = new Contact();
	    contact.setName("bowser");
	    Collection<ContactType> types = this.calypsoService.findContactTypes();
	    contact.setType(EntityUtils.getById(types, ContactType.class, 2));
	    contact.setBirthDate(new DateTime());
	    businessPartner6.addContact(contact);
	    assertEquals(found + 1, businessPartner6.getContacts().size());
	    // both storeContact and storeBusinessPartner are necessary to cover all ORM tools
	    this.calypsoService.saveContact(contact);
	    this.calypsoService.saveBusinessPartner(businessPartner6);
	    businessPartner6 = this.calypsoService.findBusinessPartnerById(6);
	    assertEquals(found + 1, businessPartner6.getContacts().size());
	    assertNotNull("Contact Id should have been generated", contact.getId());
	}

	@Test
	@Transactional
	public void updateContact() throws Exception {
	    Contact contact7 = this.calypsoService.findContactById(7);
	    String old = contact7.getName();
	    contact7.setName(old + "X");
	    this.calypsoService.saveContact(contact7);
	    contact7 = this.calypsoService.findContactById(7);
	    assertEquals(old + "X", contact7.getName());
	}

	@Test
	public void findVets() {
	    Collection<Vet> vets = this.calypsoService.findVets();
	
	    Vet v1 = EntityUtils.getById(vets, Vet.class, 2);
	    assertEquals("Leary", v1.getLastName());
	    assertEquals(1, v1.getNrOfSpecialties());
	    assertEquals("radiology", (v1.getSpecialties().get(0)).getName());
	    Vet v2 = EntityUtils.getById(vets, Vet.class, 3);
	    assertEquals("Douglas", v2.getLastName());
	    assertEquals(2, v2.getNrOfSpecialties());
	    assertEquals("dentistry", (v2.getSpecialties().get(0)).getName());
	    assertEquals("surgery", (v2.getSpecialties().get(1)).getName());
	}

	@Test
	@Transactional
	public void insertVisit() {
	    Contact contact7 = this.calypsoService.findContactById(7);
	    int found = contact7.getVisits().size();
	    Visit visit = new Visit();
	    contact7.addVisit(visit);
	    visit.setDescription("test");
	    // both storeVisit and storeContact are necessary to cover all ORM tools
	    this.calypsoService.saveVisit(visit);
	    this.calypsoService.saveContact(contact7);
	    contact7 = this.calypsoService.findContactById(7);
	    assertEquals(found + 1, contact7.getVisits().size());
	    assertNotNull("Visit Id should have been generated", visit.getId());
	}


}
