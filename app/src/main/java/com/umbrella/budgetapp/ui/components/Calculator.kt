package com.umbrella.budgetapp.ui.components

import com.umbrella.budgetapp.enums.CalculatorFunction
import java.math.BigDecimal

class Calculator {

    //Total value of the calculation.
    private var value = BigDecimal.ZERO

    //The current value used, null as first value or after operator activated.
    private var currentValue: String? = null

    //The current active operator, null if no operator used yet or equal was pressed.
    private var currentOperator: CalculatorFunction? = null

    /**
     * Listener for sending the updated value.
     */
    interface OnUpdateListener {
        fun onUpdate(value: String)
    }

    /**
     * setting the listener.
     */
    lateinit var updateListener: OnUpdateListener

    /**
     * Returns if the value is negative.
     * Happens when signum is equal to -1.
     *
     * @return true if value is lower than 0, otherwise return false.
     */
    fun isNegative() : Boolean {
        if (currentValue == null) return false

        return BigDecimal(currentValue).signum() == -1
    }

    /**
     * Returns the current absolute value.
     *
     * @return String - Current ABS value without signum.
     */
    fun getAbsValue() : String {
        if (currentValue == null) return BigDecimal.ZERO.toEngineeringString()

        return BigDecimal(currentValue).abs().toEngineeringString()
    }

    /**
     * Returns the current value.
     *
     * @return String - Current value with signum.
     */
    fun getValue() : String {
        if (currentValue == null) return BigDecimal.ZERO.toEngineeringString()

        return BigDecimal(currentValue).toEngineeringString()
    }

    //The basic function to make proper changes.
    fun calculate(function: CalculatorFunction) {
        synchronized(this) {
            when(function) {
                CalculatorFunction.DOT -> {
                    if (currentValue != null) {
                        if (!currentValue!!.contains('.')) {
                            if (currentValue!!.isEmpty()) {
                                currentValue = "0."
                            } else {
                                currentValue += "."
                            }
                        }
                    } else {
                        currentValue = "0."
                    }
                    updateListener.onUpdate(currentValue!!)
                }
                CalculatorFunction.REMOVE -> {
                    currentValue?.dropLast(1)

                    if (currentValue!!.isEmpty()) currentValue = "0"

                    if (currentValue != null) updateListener.onUpdate(currentValue!!)
                }
                CalculatorFunction.EQUALS -> {
                    if (currentValue != null) {
                        if (currentValue!!.last() == '.') currentValue!!.dropLast(1)
                        if (currentValue!!.isNotEmpty() || BigDecimal(currentValue!!) != BigDecimal.ZERO) {
                            if (currentOperator != null) {
                                val tempValue = BigDecimal(currentValue)
                                when (currentOperator) {
                                    CalculatorFunction.DIVIDE -> value.divide(tempValue)
                                    CalculatorFunction.MULTIPLY -> value.multiply(tempValue)
                                    CalculatorFunction.SUBTRACT -> value.subtract(tempValue)
                                    else /*Add*/ -> value.add(tempValue)
                                }
                            }
                        }
                        currentValue = null
                        currentOperator = null
                        updateListener.onUpdate(value.toEngineeringString())
                    }
                }
                CalculatorFunction.DIVIDE, CalculatorFunction.MULTIPLY, CalculatorFunction.SUBTRACT, CalculatorFunction.ADD -> {
                    if (currentValue != null) {
                        if (currentOperator != null) {
                            val tempValue = BigDecimal(currentValue)
                            when (currentOperator) {
                                CalculatorFunction.DIVIDE -> value.divide(tempValue)
                                CalculatorFunction.MULTIPLY -> value.multiply(tempValue)
                                CalculatorFunction.SUBTRACT -> value.subtract(tempValue)
                                else /*Add*/ -> value.add(tempValue)
                            }
                            updateListener.onUpdate(value.toEngineeringString())
                        } else {
                            value = BigDecimal(currentValue)
                        }
                        currentValue = null
                        currentOperator = function
                    }
                }
                else -> { // Numbers 0-9
                    if (currentValue != null) {
                        if (currentValue!!.isNotEmpty()) {
                            if (currentValue!! == "0") {
                                currentValue = function.ordinal.toString()
                            } else {
                                if (currentValue!!.contains('.')) {
                                    if (currentValue!!.substringAfter('.').length != 2) {
                                        currentValue += function.ordinal.toString()
                                    }
                                } else {
                                    currentValue += function.ordinal.toString()
                                }
                            }
                        } else {
                            currentValue = function.ordinal.toString()
                        }
                    } else {
                        currentValue = function.ordinal.toString()
                    }
                    updateListener.onUpdate(currentValue!!)
                }
            }
        }
    }
}