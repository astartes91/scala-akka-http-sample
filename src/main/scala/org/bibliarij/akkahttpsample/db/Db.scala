package org.bibliarij.akkahttpsample.db

import java.time.LocalDateTime

import slick.ast.BaseTypedType
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcType
import slick.lifted.TableQuery

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * @author Vladimir Nizamutdinov (astartes91@gmail.com)
  */
object Db {

  lazy implicit val localDateTimeMapping: JdbcType[LocalDateTime] with BaseTypedType[LocalDateTime] =
    MappedColumnType.base[LocalDateTime, String](_.toString, LocalDateTime.parse)

  lazy val accounts = TableQuery[AccountTable]
  lazy val transactions = TableQuery[TransactionTable]

  lazy val db = Database.forConfig("databases.money-transfers")
  Await.result(db.run((accounts.schema ++ transactions.schema).create), Duration.Inf)
}
