package com.goal.goalapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.goal.goalapp.data.group.Comment
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


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
                     ], version = 18, exportSchema = false)
@TypeConverters(Converters::class)
abstract class GoalDatabase : RoomDatabase(){

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
                    .addCallback(DatabaseCallback()) //adds fallBack data, when the database is generated
                    .build()
                    .also {
                        Instance = it
                    } //also block keep a reference to the recently created db instance


            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            CoroutineScope(Dispatchers.IO).launch {
                //use the database instance
                val database = Instance ?: return@launch
                val groupDao = database.groupDao()
                val existingEntries = groupDao.getAllGroupCategoriesStream().first()
                if (existingEntries.isEmpty()) {
                    val defaultList = listOf(
                        GroupCategory(id = 1, name = "Abnehmen"),
                        GroupCategory(id = 2, name = "Jogging"),
                        GroupCategory(id = 3, name = "Sport"),
                        GroupCategory(id = 4, name = "Häkeln"),
                        GroupCategory(id = 5, name = "Speedrun"),
                        GroupCategory(id = 6, name = "Musikinstrumente"),
                        GroupCategory(id = 7, name = "Kochen"),
                        GroupCategory(id = 8, name = "Programmierung"),
                    )
                    groupDao.insertGroupCategories(defaultList) // adds the list to the database
                }
            }
        }
    }
}

