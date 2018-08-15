package org.bibliarij.akkahttpsample.db

import org.bibliarij.akkahttpsample.web.MoneyTransferDto
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
  * @author Vladimir Nizamutdinov (astartes91@gmail.com)
  */
trait MoneyTransferRepository {
  def createMoneyTransfer(moneyTransferDto: MoneyTransferDto): Long
}

object MoneyTransferRepository extends MoneyTransferRepository {

  /**
    * @should behave correctly
    * @should throw exception when sender does not have enough funds
    * @param moneyTransferDto
    * @return
    */
  override def createMoneyTransfer(moneyTransferDto: MoneyTransferDto): Long = {
    val transactionAction =
      (for {
        _ <- AccountRepository.updateAccountAmounts(moneyTransferDto)
        transactionId <- TransactionRepository.createTransaction(moneyTransferDto)
      } yield transactionId).transactionally

    Await.result(Db.db.run(transactionAction), Duration.Inf)
  }
}
