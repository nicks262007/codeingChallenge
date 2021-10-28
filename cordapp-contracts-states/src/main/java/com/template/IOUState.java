package com.template;

import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;

import java.util.Arrays;
import java.util.List;

// *********
// * State *
// *********
//@BelongsToContract(IOUContract.class)
public class IOUState implements  LinearState {
    private  final String productName;
    private  final String productColor;
    private  String status;
    private  final AbstractParty to;
    private  final AbstractParty from;
    private final UniqueIdentifier linearId;


    public IOUState(String productName, String productColor, String status,
                    AbstractParty to, AbstractParty from, UniqueIdentifier linearId) {
        this.productName = productName;
        this.productColor = productColor;
        this.status = status;
        this.to = to;
        this.from = from;
        this.linearId = linearId;

    }




    public AbstractParty getTo() {
        return to;
    }

    public AbstractParty getFrom() {
        return from;
    }

    public String getProductColor() {
        return productColor;
    }

    public String getProductName() {
        return productName;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status){
        this.status=status;
    }


    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(to, from);
    }


    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

}