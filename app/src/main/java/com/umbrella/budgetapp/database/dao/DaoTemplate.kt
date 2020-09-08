package com.umbrella.budgetapp.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import androidx.room.Transaction
import com.umbrella.budgetapp.database.collections.Template
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedTemplate
import com.umbrella.budgetapp.database.collections.subcollections.TemplateAndCategory
import kotlinx.coroutines.flow.Flow

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
interface DaoTemplate : Base<Template> {

    /**
     * Retrieves all templates in a crossReference with category.
     *
     * @return The list of templates in a Flow.
     */
    @Transaction
    @Query("SELECT * FROM template_cross_category ORDER BY position ASC")
    fun getAllTemplates() : Flow<List<TemplateAndCategory>>

    /**
     * Find Template by ID. Template with crossReferences.
     *
     * @param id: The id corresponding with the Template.
     * @return A crossReference of the template with the attached references.
     */
    @Transaction
    @Query("SELECT * FROM template_cross WHERE template_id = :id")
    fun getTemplateById(id: Long) : Flow<ExtendedTemplate>

    /**
     * Change the position of a single Template.
     *
     * @param id: The id corresponding with the Template.
     * @param position: The position to where it moved.
     */
    @Query("UPDATE templates SET position = :position WHERE template_id = :id")
    fun changePosition(id: Long, position: Int)

    /**
     * Increases the position of views.
     * Used when a Template is moved DOWN and these views have to move UP in the list.
     *
     * @param ids: An array of ID's of templates that have to be updated.
     */
    @Query("UPDATE templates SET position = position + 1 WHERE template_id IN (:ids)")
    suspend fun increasePositionOfIds(vararg ids: Long)

    /**
     * Decreases the position of views.
     * Used when a Template is moved UP and these views have to move DOWN in the list.
     *
     * @param ids: An array of ID's of templates that have to be updated.
     */
    @Query("UPDATE templates SET position = position - 1 WHERE template_id IN (:ids)")
    suspend fun decreasePositionOfIds(vararg ids: Long)

    @Query("UPDATE templates SET position = position + 1 WHERE position > :startPos AND position < :endPos")
    suspend fun increasePositions(startPos: Int, endPos: Int)

    @Query("UPDATE templates SET position = position - 1 WHERE position > :startPos AND position < :endPos")
    suspend fun decreasePositions(startPos: Int, endPos: Int)
}