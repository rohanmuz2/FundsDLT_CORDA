package net.corda.iou.state

import net.corda.core.contracts.Amount
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.crypto.keys
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.iou.contract.IOUContract
import java.security.PublicKey
import java.security.Timestamp
import java.time.LocalDateTime
import java.util.*

/**
 * The IOU State object, with the following properties:
 * - [amount] The amount owed by the [borrower] to the [lender]
 * - [lender] The lending party.
 * - [borrower] The borrowing party.
 * - [contract] Holds a reference to the [IOUContract]
 * - [paid] Records how much of the [amount] has been paid.
 * - [linearId] A unique id shared by all LinearState states representing the same agreement throughout history within
 *   the vaults of all parties. Verify methods should check that one input and one output share the id in a transaction,
 *   except at issuance/termination.
 */

  /*  var tDate = new Date('December 07, 2017 23:15:30');
    var day = tDate.getDate();*/

data class IOUState(
                val fundId: String,
                val txType: String,
                val transactionAmount: Int,
                val borrower: Party,   //Transfer Agent
                val fundManager: Party,
                val transactionID: Int,
                //val transactionDate: Date,
                //val transactionSettlementDate: Date,
                val investorId: String,
                val nav: Float,
                val units: Float,
                val kycValidated: String,
                val txnStatus: String,
                val ccy: String,
                val amountPaid: Float,
                val lender: Party,
                val transactiondate:LocalDateTime,
                //Investor
               //val paid: Amount<Currency> = Amount(0, amount.token),
               override val linearId: UniqueIdentifier = UniqueIdentifier()): LinearState {
    /**
     * This function determines if the [IOUState] is relevant to a Corda node based on whether the public keys
     * of the lender or borrower are known to the node, i.e. if the node is the lender or borrower.
     *
     * We do this by checking that the set intersection of the vault public keys with the participant public keys
     * is not the empty set.
     */
    override fun isRelevant(ourKeys: Set<PublicKey>): Boolean {
        return ourKeys.intersect(participants.flatMap {
            it.owningKey.keys
        }).isNotEmpty()
    }

    /**
     *  This property holds a list of the nodes which can "use" this state in a valid transaction. In this case, the
     *  lender or the borrower.
     */
    override val participants: List<AbstractParty> get() = listOf(lender, borrower, fundManager)

    /**
     * A reference to the IOUContract. Make sure this is not part of the [IOUState] constructor, if it is then
     * equality won't work property on this state type. ** Don't change this property! **
     */
    override val contract get() = IOUContract()

    /**
     * Helper method which creates a new state with the [paid] amount incremented by [amountToPay]. No validation is performed.
     */
    //fun pay(amountToPay: Amount<Currency>) = copy(paid = paid + amountToPay)

    /**
     * Helper method which creates a copy of the current state with a newly specified lender. For use when transferring.
     */
    fun withNewLender(newLender: Party) = copy(lender = newLender)

    //function to get the current business day

    fun updateAmount(Amonut: Float) = copy(amountPaid =Amonut)

    //Function to generate the transaction Id

    fun transactionId(TranID:Int) = copy (transactionID=TranID)


    fun updateKYC(kycStatus: String) = copy(kycValidated = kycStatus)
    fun updateTransactionStatus(txStatus: String) = copy(txnStatus = txStatus)



    fun updateNav(navValue: Float) = copy(nav = navValue)

    fun updateUnits(navUnits: Float) = copy(units = navUnits)

    fun updateTxansactionDate (transactiondate:LocalDateTime)=copy(transactiondate =transactiondate )
    /**
     * Helper method which creates a copy of the current state with a dummy paid amount. Useful for checking that two
     * [IOUState]s
     */
   // fun withoutPaid() = copy(paid = Amount(0, Currency.getInstance("INVALID_CCY_CODE")))

    /**
     * A toString() helper method for displaying IOUs in the console.
     */
  //  override fun toString() = "IOU($linearId): ${borrower.name} owes ${lender.name} $amount and has paid $paid so far."
}