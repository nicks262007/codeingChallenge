package com.template.webserver;


import net.corda.core.contracts.UniqueIdentifier;

import java.util.UUID;


public class ProductStatus {

    private  String status;
    private UniqueIdentifier linearId;

    public String getStatus() {
        return status;
    }

    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    public void setLinearId(String linearId) {
        UUID uuid=UUID.fromString(linearId);
        UniqueIdentifier linearid = new UniqueIdentifier(null, uuid);
       this.linearId=linearid;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
