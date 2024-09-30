package com.adyen.android.assignment

import com.adyen.android.assignment.money.Change

/**
 * The CashRegister class holds the logic for performing transactions.
 *
 * @param change The change that the CashRegister is holding.
 */
class CashRegister(private val change: Change) {
    /**
     * Performs a transaction for a product/products with a certain price and a given amount.
     *
     * @param price The price of the product(s).
     * @param amountPaid The amount paid by the shopper.
     *
     * @return The change for the transaction.
     *
     * @throws TransactionException If the transaction cannot be performed.
     */

    /**
    My notes:
    Chosen algorithm O(price-amountPaid*change.elemnts.size), general idea:
    1. Iterate from 0 cents to changeDue and looking for optimal amount of moneyElements to get this amount
    2. We back track array from first step to find out which moneyElements were used to make required change

    My solution takes condition "register returns the minimal amount of change"
    as priority and always returns optimal Change in this regard. However in real project I think we more likely sacrifice
    this condition for calculation speed and go with greedy solution starting from biggest moneyElement that is smaller then change
    and trying to make a change using biggest moneyElements (we can discuss it), which it much faster, but sometimes can will use
    not optimal amount of moneyElements.

    We also can think of ways to speedup my solution without changing main approach, for example:
    1. We can iterate with bigger steps, if we need to give 100 euro 50 cents, we don't need to find amount of moneyElements
    for every cent, we can use 50 cents as our iteration step, so we iterate 50,100,150,200...
    2. It might be a good idea to separate euros and cents from amountPaid, so lets say we have 120 euro 15 cents change to give
    we separately look for best way to make 120 from our change using only moneyElements with minorValue > 1_00,
    and then try to find best way to make 15 cents using only  moneyElements with minorValue < 1_00
     */
    fun performTransaction(price: Long, amountPaid: Change): Change {
        val totalPaid = amountPaid.total
        val changeDue = totalPaid - price

        if (changeDue < 0) {
            throw TransactionException("Insufficient funds provided by the customer")
        }

        if (changeDue == 0L) {
            updateRegister(amountPaid, Change.none())
            return Change.none()
        }

        val maxAmount = changeDue.toInt()

         // bestShots[i] is the minimum number of coins needed to make amount i
        val bestShots = IntArray(maxAmount + 1) { Int.MAX_VALUE } // can take too much memory
        bestShots[0] = 0 // No coins are needed to make 0 amount

        // Iterate over amounts from 1 to changeDue
        for (amount in 1..maxAmount) {
            // For each amount, try every monetaryElement
            for (monetaryElement in change.getElements()) {
                val minorValue = monetaryElement.minorValue
                if (amount >= minorValue) {
                    val previousAmount = amount - minorValue
                    if (bestShots[previousAmount] != Int.MAX_VALUE) {
                        bestShots[amount] = minOf(bestShots[amount], bestShots[previousAmount] + 1)
                    }
                }
            }
        }

        if (bestShots[maxAmount] == Int.MAX_VALUE) {
            throw TransactionException("Cannot provide exact change with the monetaryElement available.")
        }

        // Backtrack
        val changeToGive = Change.none()
        var amount = maxAmount
        while (amount > 0) {
            for (monetaryElement in change.getElements()) {
                val minorValue = monetaryElement.minorValue
                if (change.getCount(monetaryElement) > 0 &&
                    amount >= minorValue &&
                    bestShots[amount] == bestShots[amount - minorValue] + 1
                ) {
                    changeToGive.add(monetaryElement, 1)
                    amount -= minorValue
                    break
                }
            }
        }

        updateRegister(amountPaid, changeToGive)
        return changeToGive
    }

    private fun updateRegister(amountPaid: Change, changeGiven: Change) {
        amountPaid.getElements().forEach { element ->
            change.add(element, amountPaid.getCount(element))
        }
        changeGiven.getElements().forEach { element ->
            change.remove(element, changeGiven.getCount(element))
        }
    }

    class TransactionException(message: String, cause: Throwable? = null) :
        Exception(message, cause)
}
