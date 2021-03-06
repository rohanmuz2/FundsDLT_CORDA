package net.corda.iou.contract

import net.corda.contracts.asset.Cash
import net.corda.contracts.asset.sumCash
import net.corda.core.contracts.*
import net.corda.core.contracts.Requirements.using
import net.corda.core.contracts.TransactionForContract.InOutGroup
import net.corda.core.crypto.SecureHash
import net.corda.iou.state.IOUState

/**
 * The IOUContract can handle three transaction types involving [IOUState]s:
 * - Issuance. Issuing a new [IOUState] on the ledger, which is a bilateral agreement between two parties.
 * - Transfer. Re-assigning the lender/beneficiary.
 * - Settle. Fully or partially settling the [IOUState] using the Corda [Cash] contract.
 */
class IOUContract : Contract {
    /**
     * Legal prose hash. This is just a dummy string for the time being.
     */
    override val legalContractReference: SecureHash = SecureHash.zeroHash
    /**
     * Add any commands required for this contract as classes within this interface.
     * It is useful to encapsulate your commands inside an interface, so you can use the [requireSingleCommand]
     * function to check for a number of commands which implement this interface.
     */
    interface Commands : CommandData {
        class Issue : TypeOnlyCommandData(), Commands
        class Transfer : TypeOnlyCommandData(), Commands
        class Settle : TypeOnlyCommandData(), Commands
    }

    /**
     * The contract code for the [IOUContract].
     * The constraints are self documenting so don't require any additional explanation.
     */
    override fun verify(tx: TransactionForContract): Unit {
        val command = tx.commands.requireSingleCommand<IOUContract.Commands>()
        when (command.value) {
            is Commands.Issue -> requireThat {
                "No inputs should be consumed when issuing an IOU." using (tx.inputs.isEmpty())
                "Only one output state should be created when issuing an IOU." using (tx.outputs.size == 1)
                val iou = tx.outputs.single() as IOUState
                //"A newly issued IOU must have a positive amount." using (iou.amount > Amount(0, iou.amount.token))
                "The lender and borrower cannot be the same identity." using (iou.borrower != iou.lender)
                "Both lender and borrower together only may sign IOU issue transaction." using
                        (command.signers.toSet() == iou.participants.map { it.owningKey }.toSet())
                "Transaction Amount should be graeter than 500." using (iou.transactionAmount >= 500)
            }
            is Commands.Transfer ->  {
//                "An IOU transfer transaction should only consume one input state." using (tx.inputs.size == 1)
//                "An IOU transfer transaction should only create one output state." using (tx.outputs.size == 1)
//                val input = tx.inputs.single() as IOUState
//                val output = tx.outputs.single() as IOUState
//                "Only the lender property may change." using (input == output.withNewLender(input.lender))
//                "The lender property must change in a transfer." using (input.lender != output.lender)
//                "The borrower, old lender and new lender only must sign an IOU transfer transaction" using
//                        (command.signers.toSet() == (input.participants.map { it.owningKey }.toSet() `union`
//                                output.participants.map { it.owningKey }.toSet()))
            }
            is Commands.Settle -> {
                // Check there is only one group of IOUs and that there is always an input IOU.
               /* val ious: InOutGroup<IOUState, UniqueIdentifier> = tx.groupStates<IOUState, UniqueIdentifier> { it.linearId }.single()
                require(ious.inputs.size == 1) { "There must be one input IOU." }

                // Check there are output cash states.
                val cash = tx.outputs.filterIsInstance<Cash.State>()
                require(cash.isNotEmpty()) { "There must be output cash." }

                // Check that the cash is being assigned to us.
                val inputIou = ious.inputs.single()
                val acceptableCash = cash.filter { it.owner == inputIou.lender }
                require(acceptableCash.isNotEmpty()) { "There must be output cash paid to the recipient." }

                // Sum the cash being sent to us (we don't care about the issuer).
                val sumAcceptableCash = acceptableCash.sumCash().withoutIssuer()
                //val amountOutstanding = inputIou.amount - inputIou.paid
                //require(amountOutstanding >= sumAcceptableCash) { "The amount settled cannot be more than the amount outstanding." }

                // Check to see if we need an output IOU or not.
                if (amountOutstanding == sumAcceptableCash) {
                    // If the IOU has been fully settled then there should be no IOU output state.
                    require(ious.outputs.isEmpty()) { "There must be no output IOU as it has been fully settled." }
                } else {
                    // If the IOU has been partially settled then it should still exist.
                    require(ious.outputs.size == 1) { "There must be one output IOU." }

                    // Check only the paid property changes.
                    val outputIou = ious.outputs.single()
                    requireThat {
                        "The amount may not change when settling." using (inputIou.amount == outputIou.amount)
                        "The borrower may not change when settling." using (inputIou.borrower == outputIou.borrower)
                        "The lender may not change when settling." using (inputIou.lender == outputIou.lender)
                        "The linearId may not change when settling." using (inputIou.linearId == outputIou.linearId)
                    }

                    // Check the paid property is updated correctly.
                    //require(outputIou.paid == inputIou.paid + sumAcceptableCash) { "Paid property incorrectly updated." }
                }

                // Checks the required parties have signed.
                "Both lender and borrower together only must sign IOU settle transaction." using
                        (command.signers.toSet() == inputIou.participants.map { it.owningKey }.toSet())*/
            }
        }
    }
}
