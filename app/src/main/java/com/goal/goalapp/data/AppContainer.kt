package com.goal.goalapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.goal.goalapp.data.comment.CommentRepository
import com.goal.goalapp.data.comment.OfflineCommentRepository
import com.goal.goalapp.data.goal.GoalRepository
import com.goal.goalapp.data.goal.OfflineGoalRepository
import com.goal.goalapp.data.group.GroupRepository
import com.goal.goalapp.data.group.OfflineGroupRepository
import com.goal.goalapp.data.post.OfflinePostRepository
import com.goal.goalapp.data.post.PostRepository
import com.goal.goalapp.data.user.OfflineUserRepository
import com.goal.goalapp.data.user.UserRepository
import com.goal.goalapp.data.user_session.OfflineUserSessionRepository
import com.goal.goalapp.data.user_session.UserSessionRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer{
    val commentRepository: CommentRepository
    val goalRepository: GoalRepository
    val groupRepository: GroupRepository
    val postRepository: PostRepository
    val userRepository: UserRepository
    val userSessionRepository: UserSessionRepository
    val userSessionStorage: UserSessionStorage
}

/**
 * [AppContainer] implementation that provides instance of the Repositories
 */
class AppDataContainer(private val context: Context) : AppContainer{

    /**
     * Implementation for [CommentRepository]
     */
    override val commentRepository: CommentRepository by lazy {
        OfflineCommentRepository(GoalDatabase.getDatabase(context).commentDao())
    }

    /**
     * Implementation for [GoalRepository]
     */
    override val goalRepository: GoalRepository by lazy {
        OfflineGoalRepository(GoalDatabase.getDatabase(context).goalDao())
    }

    /**
     * Implementation for [GroupRepository]
     */
    override val groupRepository: GroupRepository by lazy {
        OfflineGroupRepository(GoalDatabase.getDatabase(context).groupDao())
    }

    /**
     * Implementation for [PostRepository]
     */
    override val postRepository: PostRepository by lazy {
        OfflinePostRepository(GoalDatabase.getDatabase(context).postDao())
    }

    /**
     * Implementation for [UserRepository]
     */
    override val userRepository: UserRepository by lazy {
        OfflineUserRepository(GoalDatabase.getDatabase(context).userDao())
    }

    /**
     * Implementation for [UserSessionRepository]
     */
    override val userSessionRepository: UserSessionRepository by lazy {
        OfflineUserSessionRepository(GoalDatabase.getDatabase(context).userSessionDao())
    }

    /**
     * Implementation for [UserSessionStorage]
     */
    override val userSessionStorage: UserSessionStorage by lazy {
        val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("user_prefs")
        }
        UserSessionStorage(dataStore)
    }
}