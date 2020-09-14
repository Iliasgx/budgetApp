package com.umbrella.budgetapp.enums

enum class GoalPrefabs(val colorIndex: Int, val iconIndex: Int) {
    VEHICLE         (9, 31),
    HOME            (16,32),
    HOLIDAY         (13,33),
    EDUCATION       (10,34),
    EMERGENCY_FUND  (14,35),
    HEALTH_CARE     (3, 36),
    PARTY           (15,37),
    CHARITY         (1, 38),
    NONE            (0, 0) // Default
}