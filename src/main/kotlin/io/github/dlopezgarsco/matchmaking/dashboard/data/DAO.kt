package io.github.dlopezgarsco.matchmaking.dashboard.data

import arrow.core.*
import arrow.core.Either.*
import io.github.dlopezgarsco.matchmaking.dbQuery
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll


sealed interface DAO<A, T, C> {
  suspend fun<T: IdTable<C>, C: Comparable<C>> get(id: C, table: T) : Either<DAOError, A> = when
      (val result = dbQuery { table.select { (table.id eq id) } }.firstOrNone()) {
        is None -> Left(DAOError.RecordDoesntExist)
        is Some -> Right(result.value.toPOJO())
      }
  suspend fun<T: IdTable<C>> getAll(table: T): List<A> = dbQuery {
    table.selectAll().map { it.toPOJO() }
  }
  suspend fun<T: IdTable<C>, C:Comparable<C>> create(data: A, table: T): Either<DAOError, C>
  suspend fun update(data: A): Boolean
  suspend fun delete(id: Int): Boolean
  suspend fun delete(data: A): Boolean
  suspend fun ResultRow.toPOJO(): A

}

sealed class DAOError {

  object InaccessibleDatabase : Exception()
  data object RecordDoesntExist : DAOError()
  data object RecordAlreadyExists : DAOError()

}