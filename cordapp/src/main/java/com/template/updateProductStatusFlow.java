package com.template;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class updateProductStatusFlow extends FlowLogic<SignedTransaction> {

    private String status;
    private  UniqueIdentifier linearId;
   // private final AbstractParty otherParty;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public updateProductStatusFlow(String status,UniqueIdentifier linearId) {
        this.status = status;
        this.linearId=linearId;
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }



    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
       try {
           final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

           QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(
                   null,
                   Arrays.asList(linearId),
                   Vault.StateStatus.UNCONSUMED,
                   null);


           List<StateAndRef<IOUState>> iouState = getServiceHub()
                   .getVaultService()
                   .queryBy(IOUState.class, queryCriteria)
                   .getStates();

           IOUState iouStateObj = iouState.get(0).getState().getData();

           //IOUState iouStateObj = iouState.get(0).getState().c;
           String exiStatus = iouStateObj.getStatus();
           String newStatus = exiStatus.replace(exiStatus, status);
           iouStateObj.setStatus(newStatus);

           IOUState state = new IOUState(iouStateObj.getProductName(),
                   iouStateObj.getProductColor(),
                   newStatus,
                   getOurIdentity(),
                   iouStateObj.getFrom(),
                   iouStateObj.getLinearId());

           //IOUState state = iouState.get(0);
           List<PublicKey> requiredSigners = Arrays.asList(getOurIdentity()
                   .getOwningKey(),
                   iouStateObj
                           .getTo()
                           .getOwningKey());
           Command command = new Command<>(new IOUContract.UpdateStatus(), requiredSigners);

           // We create a transaction builder and add the components.
           TransactionBuilder txBuilder1 = new TransactionBuilder(notary)
                   .addOutputState(state, IOUContract.ID)
                   .addInputState(iouState.get(0))
                   .addCommand(command);

           // Verifying the transaction.
           txBuilder1.verify(getServiceHub());

           // Signing the transaction.
           SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder1);

           // Creating a session with the other party.
           FlowSession otherPartySession = initiateFlow((Party) iouStateObj.getTo());

           // Obtaining the counterparty's signature.
           SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(
                   signedTx, Arrays.asList(otherPartySession), CollectSignaturesFlow.tracker()));

           //Finalising the transaction.
           subFlow(new FinalityFlow(fullySignedTx));

       }catch (Exception e){
           e.printStackTrace();
           return null;
       }
        return null;

    }
}

