package org.bibliarij.akkahttpsample.db

import java.time.LocalDateTime

import org.bibliarij.akkahttpsample.Transaction
import org.bibliarij.akkahttpsample.web.MoneyTransferDto
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * @author Vladimir Nizamutdinov (astartes91@gmail.com)
  */
object TransactionRepository {

  def createTransaction(moneyTransferDto: MoneyTransferDto) = {
    val transaction: Transaction = Transaction(
      0,
      moneyTransferDto.senderAccountNumber,
      moneyTransferDto.recipientAccountNumber,
      moneyTransferDto.amount,
      LocalDateTime.now()
    )
    Db.transactions.returning(Db.transactions.map(_.id)) += transaction
  }

  def getTransactions(): Seq[Transaction] = {
    Await.result(Db.db.run(Db.transactions.result), Duration.Inf)
  }
}
