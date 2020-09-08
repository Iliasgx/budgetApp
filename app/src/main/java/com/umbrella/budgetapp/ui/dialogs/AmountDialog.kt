package com.umbrella.budgetapp.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.enums.CalculatorFunction.*
import com.umbrella.budgetapp.extensions.setNavigationResult
import com.umbrella.budgetapp.ui.components.Calculator
import kotlinx.android.synthetic.main.dialog_amount.*
import java.math.BigDecimal

class AmountDialog : DialogFragment() {

    private val args by navArgs<AmountDialogArgs>()

    private var amount: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_amount, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calculatorListeners()

        dialog_Amount_Cancel.setOnClickListener { dismiss() }
        dialog_Amount_Ok.setOnClickListener {
            if (args.minValue != 0F && BigDecimal(amount).compareTo(args.minValue.toBigDecimal()) == -1) {
                Toast.makeText(context, getString(R.string.dialog_Amount_error_min, args.minValue), Toast.LENGTH_SHORT).show()
            } else if (args.maxValue != 0F && BigDecimal(amount).compareTo(args.maxValue.toBigDecimal()) == 1) {
                Toast.makeText(context, getString(R.string.dialog_Amount_error_max, args.maxValue), Toast.LENGTH_SHORT).show()
            } else {
                setNavigationResult("amount", amount)
            }
        }
    }

    private fun calculatorListeners() {
        val cal = Calculator()

        cal.updateListener = object : Calculator.OnUpdateListener {
            override fun onUpdate(value: String) {
                amount = value
                dialog_Amount_Value.text = cal.getValue()
            }
        }

        dialog_Amount_Zero.setOnClickListener { cal.calculate(ZERO) }
        dialog_Amount_One.setOnClickListener { cal.calculate(ONE) }
        dialog_Amount_Two.setOnClickListener { cal.calculate(TWO) }
        dialog_Amount_Three.setOnClickListener { cal.calculate(THREE) }
        dialog_Amount_Four.setOnClickListener { cal.calculate(FOUR) }
        dialog_Amount_Five.setOnClickListener { cal.calculate(FIVE) }
        dialog_Amount_Six.setOnClickListener { cal.calculate(SIX) }
        dialog_Amount_Seven.setOnClickListener { cal.calculate(SEVEN) }
        dialog_Amount_Eight.setOnClickListener { cal.calculate(EIGHT) }
        dialog_Amount_Nine.setOnClickListener { cal.calculate(NINE) }

        dialog_Amount_Dot.setOnClickListener { cal.calculate(DOT) }
        dialog_Amount_Backspace.setOnClickListener { cal.calculate(REMOVE) }

        dialog_Amount_Divide.setOnClickListener { cal.calculate(DIVIDE) }
        dialog_Amount_Multiply.setOnClickListener { cal.calculate(MULTIPLY) }
        dialog_Amount_Subtract.setOnClickListener { cal.calculate(SUBTRACT) }
        dialog_Amount_Add.setOnClickListener { cal.calculate(ADD) }
        dialog_Amount_Equals.setOnClickListener { cal.calculate(EQUALS) }
    }

}