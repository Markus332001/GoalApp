package com.goal.goalapp.data.user_session

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.goal.goalapp.data.user.User

@Entity(
    tableName = "user_sessions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,               // Verweist auf die Haupttabelle
            parentColumns = ["id"],            // Primärschlüssel der Haupttabelle
            childColumns = ["userId"],         // Fremdschlüssel in dieser Tabelle
            onDelete = ForeignKey.CASCADE      // Verhalten beim Löschen des Hauptdatensatzes
        )
    ],
    indices = [Index("userId")]  // Fügt einen Index auf die userId-Spalte hinzu
)
data class UserSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val token: String,
    val createdAt: Long,
    val expiresAt: Long
)
