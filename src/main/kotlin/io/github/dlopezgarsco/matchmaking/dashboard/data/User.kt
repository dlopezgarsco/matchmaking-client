package io.github.dlopezgarsco.matchmaking.dashboard.data

import arrow.core.Either
import arrow.core.Option
import arrow.core.toOption
import io.github.dlopezgarsco.matchmaking.dbQuery
import io.github.dlopezgarsco.matchmaking.models.User
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import java.util.UUID

object Users : UUIDTable()

data object UserDAO : DAO<User, Users> {

  override suspend fun create(data: User): Either<DAOError, Int> {
    if (getByUser(data.user).isNotEmpty())
      return Either.Left(DAOError.RecordAlreadyExists)
    return Either.Right(dbQuery {
      Users.insert {
        it[user] = data.user
        it[password] = data.password
      }
    } get Users.id)
  }

  override suspend fun update(data: User): Boolean {
    val updatedRows: Int = getByUser(data.user).fold({ throw DAOError.InaccessibleDatabase }, {
      dbQuery {
        Users.update(where = { Users.user eq data.user }) {
          it[password] = data.password
        }
      }
    })
    return updatedRows > 0
  }

  override suspend fun delete(id: Int): Boolean {
    val updatedRows: Int = get(id).fold({ throw DAOError.InaccessibleDatabase }, {
      dbQuery {
        Users.deleteWhere { Users.id eq it.id }
      }
    })
    return updatedRows > 0
  }

  override suspend fun delete(data: User): Boolean {
    val updatedRows: Int = getByUser(data.user).fold({ throw DAOError.InaccessibleDatabase }, {
      dbQuery {
        Users.deleteWhere { Users.id eq it.id }
      }
    })
    return updatedRows > 0
  }

  override suspend fun ResultRow.toPOJO(): User = User(
    uuid = this[uuid],
  )
}