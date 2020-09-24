package com.umbrella.budgetapp.ui.components

import android.util.Log
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
        if (currentOperator == CalculatorFunction.EQUALS) {
            return value.abs().toPlainString()
        } else {
            if (currentValue == null) return BigDecimal.ZERO.toPlainString()
            return BigDecimal(currentValue).abs().toPlainString()
        }
    }

    /**
     * Returns the current value.
     *
     * @return String - Current value with signum.
     */
    fun getValue() : String {
        if (currentOperator == CalculatorFunction.EQUALS) {
            return value.toPlainString()
        } else {
            if (currentValue == null) return BigDecimal.ZERO.toPlainString()
            return BigDecimal(currentValue).toPlainString()
        }
    }

    //The basic function to make proper changes.
    fun calculate(function: CalculatorFunction) {

        val build = StringBuilder(function.name).append(" {").appendln()
        build
                .append("Operator: $currentOperator").appendln()
                .append("Current value: $currentValue").appendln()
                .append("Total value: $value").appendln()
                .append(" } -> results: {").appendln()

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

                if (currentValue.isNullOrEmpty()) currentValue = "0"

                if (currentValue != null) updateListener.onUpdate(currentValue!!)
            }
            CalculatorFunction.EQUALS -> {
                if (currentValue != null) {
                    if (currentValue!!.last() == '.') currentValue!!.dropLast(1)
                    if (currentValue!!.isNotEmpty() || BigDecimal(currentValue!!) != BigDecimal.ZERO) {
                        if (currentOperator != null) {
                            val tempValue = BigDecimal(currentValue)
                            when (currentOperator) {
                                CalculatorFunction.DIVIDE -> value = value.divide(tempValue)
                                CalculatorFunction.MULTIPLY -> value = value.multiply(tempValue)
                                CalculatorFunction.SUBTRACT -> value = value.subtract(tempValue)
                                CalculatorFunction.ADD -> value = value.add(tempValue)
                                else -> Unit
                            }
                        }
                    }
                    currentValue = null
                    currentOperator = null
                    updateListener.onUpdate(value.toPlainString())
                }
            }
            CalculatorFunction.DIVIDE, CalculatorFunction.MULTIPLY, CalculatorFunction.SUBTRACT, CalculatorFunction.ADD -> {
                if (currentValue != null) {
                    if (currentOperator != null) {
                        val tempValue = BigDecimal(currentValue)
                        when (currentOperator) {
                            CalculatorFunction.DIVIDE -> value = value.divide(tempValue)
                            CalculatorFunction.MULTIPLY -> value = value.multiply(tempValue)
                            CalculatorFunction.SUBTRACT -> value = value.subtract(tempValue)
                            CalculatorFunction.ADD -> value = value.add(tempValue)
                            else -> Unit
                        }
                        updateListener.onUpdate(value.toPlainString())
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

        build
                .append("Operator: $currentOperator").appendln()
                .append("Current value: $currentValue").appendln()
                .append("Total value: $value").appendln()

        Log.d("_Test", build.append("}").toString());
    }
}