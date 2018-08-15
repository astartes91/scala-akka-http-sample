package org.bibliarij.akkahttpsample.db

import java.time.LocalDateTime

import org.bibliarij.akkahttpsample.db.Db.localDateTimeMapping
import org.bibliarij.akkahttpsample.{Account, Transaction}
import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

/**
  * @author Vladimir Nizamutdinov (astartes91@gmail.com)
  */
class AccountTable(tag: Tag) extends Table[Account](tag, "accounts") {
  def accountNumber = column[String]("account_number", O.PrimaryKey)
  def amount = column[BigDecimal]("amount")

  override def * = (accountNumber, amount) <>
    (Account.tupled, Account.unapply)
}

class TransactionTable(tag: Tag) extends Table[Transaction](tag, "transactions"){
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def senderAccountNumber = column[String]("sender_account_number")
  def recipientAccountNumber = column[String]("recipient_account_number")
  def amount = column[BigDecimal]("amount")
  def time = column[LocalDateTime]("time")

  def senderAccountNumberKey =
    foreignKey("SENDER_ACCOUNT_NUMBER_FK", senderAccountNumber, Db.accounts)(_.accountNumber)
  def recipientAccountNumberKey =
    foreignKey("RECIPIENT_ACCOUNT_NUMBER_FK", recipientAccountNumber, Db.accounts)(_.accountNumber)

  override def * = (id, senderAccountNumber, recipientAccountNumber, amount, time) <>
    (Transaction.tupled, Transaction.unapply)
}
