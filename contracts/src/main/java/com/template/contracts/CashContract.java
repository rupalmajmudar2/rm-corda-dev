package com.template.contracts;

import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;
import java.security.PublicKey;
import java.util.List;

import com.template.states.CashState;

/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
public class CashContract implements Contract {
    public static String ID = "org.vloyalty.contract.CashContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

        //(A) Shape rules - size of input and output
        if (tx.getCommands().size() != 1) throw new IllegalArgumentException("Exactly One command expected. Found: " + tx.getCommands().size());
        Command cmd= tx.getCommand(0);

        if (cmd.getValue() instanceof Commands.Transfer) { //TODO: Put in the checks here!
            System.out.println("TODO: CashTransfer Contract verification");
        }
        else {
            if (!(cmd.getValue() instanceof Commands.Issue))
                throw new IllegalArgumentException("Cmd has to be of type Issue. Found: " + cmd.getValue());

            if (tx.getInputStates().size() != 0)
                throw new IllegalArgumentException(("Txn inputs should be 0. Found: " + tx.getInputStates().size()));
            if (tx.getOutputStates().size() != 1)
                throw new IllegalArgumentException(("Txn outputs should be 1. Found: " + tx.getOutputStates().size()));

            //(B) Content rules -
            ContractState cs = tx.getOutput(0);
            if (!(cs instanceof CashState))
                throw new IllegalArgumentException("Tx output must be a TokenState. Found: " + cs.getClass());
            CashState ts = (CashState) cs;
            int amt = ((CashState) cs).getAmountInt();
            //if (amt <= 0) throw new IllegalArgumentException("Token amount must be positive");
            //No - we are allowing -ve cash with the sender (issuer)

            //(C) Signer rules
            final List<PublicKey> requiredSigners = cmd.getSigners();

            Party issuer = ts.getIssuer();
            PublicKey ipk = issuer.getOwningKey();
            if (!(requiredSigners.contains(ipk))) throw new IllegalArgumentException("Token issuer must be a signer");

            /*Party owner= ts.getOwner();
            PublicKey opk= owner.getOwningKey();
            if (!(requiredSigners.contains(opk))) throw new IllegalArgumentException("Token owner must be a signer");*/
        }
    }

    public interface Commands extends CommandData {
        public static class Issue implements Commands {
        }
        public static class Transfer implements Commands {
        }
    }
}