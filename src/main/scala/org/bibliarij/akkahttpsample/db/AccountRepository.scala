package org.bibliarij.akkahttpsample.db

import org.bibliarij.akkahttpsample.Account
import org.bibliarij.akkahttpsample.web.MoneyTransferDto
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
  * @author Vladimir Nizamutdinov (astartes91@gmail.com)
  */
trait AccountRepository {
  def isAccountExists(accountNumber: String): Boolean
}

object AccountRepository extends AccountRepository {

  override def isAccountExists(accountNumber: String): Boolean = {
    Await.result(Db.db.run(queryAccountByNumber(accountNumber).exists.result), Duration.Inf)
  }

  def getAccounts(): Seq[Account] = {
    Await.result(Db.db.run(Db.accounts.result), Duration.Inf)
  }

  def createAccount(account: Account): Unit = {
    Await.result(Db.db.run(Db.accounts += account), Duration.Inf)
  }

  def updateAccountAmounts(moneyTransferDto: MoneyTransferDto) = {
    val senderAccountNumber: String = moneyTransferDto.senderAccountNumber
    val recipientAccountNumber: String = moneyTransferDto.recipientAccountNumber
    val amount: BigDecimal = moneyTransferDto.amount
    for {
      senderAccount <- getAccountForUpdate(senderAccountNumber)
      _ <-
        if (senderAccount.amount.compare(amount) < 0) {
          DBIO.failed(new RuntimeException("Sender does not have enough funds"))
        } else {
          updateAccountValue(senderAccountNumber, senderAccount.amount - amount)
        }
      recipientAccount <- AccountRepository.getAccountForUpdate(recipientAccountNumber)
      _ <- updateAccountValue(recipientAccountNumber, recipientAccount.amount + amount)
    } yield ()
  }

  private def getAccountForUpdate(accountNumber: String) = {
    queryAccountByNumber(accountNumber).forUpdate.result.head
  }

  private def updateAccountValue(accountNumber: String, amount: BigDecimal) = {
    queryAccountByNumber(accountNumber).map(_.amount).update(amount)
  }

  private def queryAccountByNumber(accountNumber: String): Query[AccountTable, Account, Seq] = {
    Db.accounts.filter(_.accountNumber === accountNumber)
  }
}
