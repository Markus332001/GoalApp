package com.goal.goalapp.data

enum class CompletionType {
    ReachGoal, ConnectRoutine, ReachTargetValue
}

enum class Frequency {
    Daily, Weekly, IntervalDays
}

enum class Role(val rank: Int){
    OWNER(2), ADMIN(1), MEMBER(0)
}
