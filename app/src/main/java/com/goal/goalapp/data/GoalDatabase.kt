package com.goal.goalapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.goal.goalapp.data.comment.Comment
import com.goal.goalapp.data.comment.CommentDao
import com.goal.goalapp.data.goal.CompletionCriterion
import com.goal.goalapp.data.goal.Goal
import com.goal.goalapp.data.goal.GoalDao
import com.goal.goalapp.data.goal.Routine
import com.goal.goalapp.data.goal.RoutineCalendarDays
import com.goal.goalapp.data.goal.RoutineSummary
import com.goal.goalapp.data.group.Group
import com.goal.goalapp.data.group.GroupCategory
import com.goal.goalapp.data.group.GroupDao
import com.goal.goalapp.data.group.GroupGroupCategoryCrossRef
import com.goal.goalapp.data.post.Post
import com.goal.goalapp.data.post.PostDao
import com.goal.goalapp.data.user.User
import com.goal.goalapp.data.user.UserDao
import com.goal.goalapp.data.user.UserGroupCrossRef
import com.goal.goalapp.data.user_session.UserSession
import com.goal.goalapp.data.user_session.UserSessionDao


@Database(entities = [
    User::class,
    UserSession::class,
    Goal::class,
    CompletionCriterion::class,
    Routine::class,
    RoutineCalendarDays::class,
    Group::class,
    GroupCategory::class,
    Post::class,
    RoutineSummary::class,
    Comment::class,
    UserGroupCrossRef::class,
    GroupGroupCategoryCrossRef::class
                     ], version = 9, exportSchema = false)
@TypeConverters(Converters::class)
abstract class GoalDatabase : RoomDatabase(){

    abstract fun commentDao(): CommentDao
    abstract fun goalDao(): GoalDao
    abstract fun groupDao(): GroupDao
    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao
    abstract fun userSessionDao(): UserSessionDao

    companion object {
        //Volatile means that changes made by one thread to Instance are immediately visible to all other threads.
        @Volatile
        private var Instance: GoalDatabase? = null

        fun getDatabase(context: Context): GoalDatabase {
            //Wrapping the code to get the database inside a synchronized block means that only one thread of execution at a time can enter this block of code,
            // which makes sure the database only gets initialized once.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, GoalDatabase::class.java, "goal_database")
                    .fallbackToDestructiveMigration()//: Normally, you would provide a migration object with a migration strategy for when the schema changes,
                    // but fallbackToDestructiveMigration says to to destroy and rebuild the database. This isnt good when you want to keep the data of the database
                    .build()
                    .also {
                        Instance = it
                    } //also block keep a reference to the recently created db instance


            }
        }
    }
}