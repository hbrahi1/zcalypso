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

import com.calypso.model.Pet;

/**
 * Subclass of Pet that carries temporary id properties which are only relevant for a JDBC implementation of the
 * CalypsoService.
 *
 * @author Juergen Hoeller
 */
class JdbcPet extends Pet {

    private int typeId;

    private int businessPartnerId;


    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return this.typeId;
    }

    public void setBusinessPartnerId(int businessPartnerId) {
        this.businessPartnerId = businessPartnerId;
    }

    public int getBusinessPartnerId() {
        return this.businessPartnerId;
    }

}
