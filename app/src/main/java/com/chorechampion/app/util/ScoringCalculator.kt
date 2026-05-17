package com.chorechampion.app.util

object ScoringCalculator {
    
    /**
     * Converts completion percentage (0-100) to base points (1-5 scale)
     */
    fun getBasePoints(completionPercentage: Int): Int {
        return when (completionPercentage) {
            in 0..20 -> 1
            in 21..40 -> 2
            in 41..60 -> 3
            in 61..80 -> 4
            in 81..100 -> 5
            else -> 0
        }
    }
    
    /**
     * Calculates final points with weightage multiplier
     * Formula: basePoints * (weightage / 10)
     */
    fun calculateFinalPoints(completionPercentage: Int, weightage: Int): Float {
        val basePoints = getBasePoints(completionPercentage)
        return basePoints * (weightage / 10f)
    }
    
    /**
     * Calculates total points for a list of completions
     */
    fun calculateTotalPoints(completions: List<Pair<Int, Int>>): Float {
        return completions.sumOf { (percentage, weightage) ->
            calculateFinalPoints(percentage, weightage).toDouble()
        }.toFloat()
    }
}
