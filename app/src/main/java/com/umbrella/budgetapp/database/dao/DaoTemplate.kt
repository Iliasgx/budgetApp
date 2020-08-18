package com.umbrella.budgetapp.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import androidx.room.Transaction
import com.umbrella.budgetapp.database.collections.Template
import com.umbrella.budgetapp.database.collections.subcollections.ExtendedTemplate
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
    @Query("SELECT template_id, name, position, extended_category_name FROM template_cross ORDER BY position ASC")
    fun getAllTemplates() : Flow<List<ExtendedTemplate>>

    /**
     * Find Template by ID. Template with crossReferences.
     *
     * @param id: The id corresponding with the Template.
     * @return A crossReference of the template with the attached references.
     */
    @Transaction
    @Query("SELECT * FROM template_cross WHERE template_id = :id")
    fun getTemplateById(id: Long) : ExtendedTemplate

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
}