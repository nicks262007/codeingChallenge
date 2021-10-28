package com.template;

import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;

// ************
// * Contract *
// ************
public class IOUContract implements Contract {
    public static final String ID = "com.template.IOUContract";

    // Our Create command.
    public static class Create implements CommandData {
    }

    public static class UpdateStatus implements CommandData{

    }

    @Override
   public void verify(LedgerTransaction tx) {
        if(tx.getCommands().size() !=1)
            throw new IllegalArgumentException("Trasaction must have one command");
        Command command= tx.getCommand(0);
        List<PublicKey> requiredSignature=command.getSigners();
        CommandData commandType= command.getValue();
        final IOUState output = tx.outputsOfType(IOUState.class).get(0);
        if(commandType instanceof Create){
            if (!tx.getInputs().isEmpty())
                throw new IllegalArgumentException("No inputs should be consumed when issuing an IOU.");
            if (!(tx.getOutputs().size() == 1))
                throw new IllegalArgumentException("There should be one output state of type IOUState.");
            // IOU-specific constraints.
            final AbstractParty to = output.getTo();
            final AbstractParty from = output.getFrom();

            if (!(output.getProductColor().equals("Red") || output.getProductColor().equals("Black"))) {
                throw new IllegalArgumentException("Product color must be Red or Black ");
            }
            if (to.equals(from))
                throw new IllegalArgumentException("The lender and the borrower cannot be the same entity.");
            // Constraints on the signers.
            final List<PublicKey> requiredSigners = command.getSigners();
            final List<PublicKey> expectedSigners = Arrays.asList(from.getOwningKey(), to.getOwningKey());
            if (requiredSigners.size() != 2)
                throw new IllegalArgumentException("There must be two signers.");
            if (!(requiredSigners.containsAll(expectedSigners)))
                throw new IllegalArgumentException("The borrower and lender must be signers.");

        }else if (commandType instanceof UpdateStatus){
            if (tx.getInputs().isEmpty())
                throw new IllegalArgumentException("Input state should not be empty when Updating an IOU.");
            if (!(tx.getOutputs().size() == 1))
                throw new IllegalArgumentException("There should be one output state of type IOUState.");

            final AbstractParty to = output.getTo();
            final AbstractParty from = output.getFrom();

            if (!(output.getProductColor().equals("Red") || output.getProductColor().equals("Black"))) {
                throw new IllegalArgumentException("Product color must be Red or Black ");
            }
            if (to.equals(from))
                throw new IllegalArgumentException("The lender and the borrower cannot be the same entity.");
            // Constraints on the signers.
            final List<PublicKey> requiredSigners = command.getSigners();
            final List<PublicKey> expectedSigners = Arrays.asList(from.getOwningKey(), to.getOwningKey());
            if (requiredSigners.size() != 2)
                throw new IllegalArgumentException("There must be two signers.");
            if (!(requiredSigners.containsAll(expectedSigners)))
                throw new IllegalArgumentException("The borrower and lender must be signers.");

        }
    }
}