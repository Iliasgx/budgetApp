package com.umbrella.budgetapp.database.defaults

import android.content.Context
import android.content.res.TypedArray
import com.umbrella.budgetapp.R
import com.umbrella.budgetapp.database.collections.Category

/* EXPENSES
 *1 Groceries
 *2 Restaurant
 *3 Bar, cafe
 *4 Campus card
 *5 Health and beauty
 *6 Health care, doctor
 *7 Mortgage
 *8 Insurance
 *9 Housing repairs
 *10 Transportation
 *11 Fuel
 *12 Vehicle maintenance
 *13 Active sport
 *14 Education
 *15 Hobbies
 *16 TV, Streaming
 *17 Culture
 *18 Holiday, trips, hotels
 *19 Software, apps, games
 *20 Pets, animals
 */

/* INCOME
 *21 Loan
 *22 Savings
 *23 Pocket money
 *24 Interests, dividends
 *25 Lending
 */

/* BOTH
 *26 Clothes & shoes
 *27 Life events
 *28 Gifts
 *29 Investments
 *30 Lottery, gambling
 *31 Taxes
 */

class DefaultCategories(private val context: Context) {

    private var iconsTA: TypedArray = context.resources.obtainTypedArray(R.array.icons)

    fun getCategories() : Array<Category> {
        val colorIndices = listOf(4,3,5,13,10,8,15,16,1,6,2,3,19,7,12,15,6,14,4,9,14,15,5,1,2,5,16,15,19,3,2)
        val list = mutableListOf<Category>()

        for (index in colorIndices.indices) {
            list.add(Category(name = str(index), icon = draw(index), color = colorIndices[index]))
        }

        iconsTA.recycle()

        return list.toTypedArray()
    }

    private fun str(nameIndex: Int) = context.resources.getStringArray(R.array.default_category_names)[nameIndex]

    private fun draw(drawableIndex: Int) = iconsTA.getResourceId(drawableIndex, 0)

}
