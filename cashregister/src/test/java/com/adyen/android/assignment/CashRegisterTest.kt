package com.adyen.android.assignment

import com.adyen.android.assignment.money.Bill
import com.adyen.android.assignment.money.Change
import com.adyen.android.assignment.money.Coin
import org.junit.Assert.assertEquals
import org.junit.Test

class CashRegisterTest {

    @Test
    fun noChangeTransaction() {
        val registerChange = Change().apply {
            add(Bill.FIVE_EURO, 2)
            add(Coin.TWO_EURO, 5)
            add(Coin.ONE_EURO, 10)
        }

        val cashRegister = CashRegister(registerChange)

        val amountPaid = Change().apply {
            add(Bill.FIVE_EURO, 1)
        }

        val result = cashRegister.performTransaction(500L, amountPaid)
        val expectedChange = Change.none()

        assertEquals(expectedChange, result)
    }

    @Test(expected = CashRegister.TransactionException::class)
    fun insufficientFunds() {
        val registerChange = Change().apply {
            add(Bill.FIVE_EURO, 1)
            add(Coin.TWO_EURO, 5)
        }

        val cashRegister = CashRegister(registerChange)

        val amountPaid = Change().apply {
            add(Coin.TWO_EURO, 1)
        }

        cashRegister.performTransaction(500L, amountPaid)
    }

    @Test(expected = CashRegister.TransactionException::class)
    fun cannotProvideExactChange() {
        val registerChange = Change().apply {
            add(Bill.TWENTY_EURO, 1)
            add(Coin.TWO_EURO, 2)
        }

        val cashRegister = CashRegister(registerChange)

        val amountPaid = Change().apply {
            add(Bill.TWENTY_EURO, 1)
        }

        cashRegister.performTransaction(1500L, amountPaid)
    }

    @Test
    fun provideChangeWithLimitedCoins() {
        val registerChange = Change().apply {
            add(Bill.TWENTY_EURO, 1)
            add(Coin.TWO_EURO, 1)
            add(Coin.ONE_EURO, 1)
        }

        val cashRegister = CashRegister(registerChange)

        val amountPaid = Change().apply {
            add(Bill.TEN_EURO, 1)
        }

        val result = cashRegister.performTransaction(700L, amountPaid)

        val expectedChange = Change().apply {
            add(Coin.TWO_EURO, 1)
            add(Coin.ONE_EURO, 1)
        }

        assertEquals(expectedChange, result)
    }


    /**
    This test will wail with greedy algorithm
     */
    @Test
    fun giveOptimalChange() {
        val registerChange = Change().apply {
            add(Bill.FIFTY_EURO, 1)
            add(Bill.TWENTY_EURO, 3)
            add(Coin.ONE_EURO, 10)
        }

        val cashRegister = CashRegister(registerChange)

        val amountPaid = Change().apply {
            add(Bill.ONE_HUNDRED_EURO, 1) // Paying 100 EUR
        }

        val price = 40_00L

        // Price is 40 EUR, expecting to give 60 EUR change, 20 euro X 3 is optimal change
        val result = cashRegister.performTransaction(price, amountPaid)

        val expectedChange = Change().apply {
            add(Bill.TWENTY_EURO, 3)
        }

        assertEquals(expectedChange, result)
    }

    // TODO performance tests for large change scenarios
}
