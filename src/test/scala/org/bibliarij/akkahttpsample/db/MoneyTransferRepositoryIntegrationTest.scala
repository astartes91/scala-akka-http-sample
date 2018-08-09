package org.bibliarij.akkahttpsample.db

import org.assertj.core.api.Assertions
import org.bibliarij.akkahttpsample.web.MoneyTransferDto
import org.bibliarij.akkahttpsample.{Account, Transaction}
import org.junit.{After, Test}
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * @author Vladimir Nizamutdinov (astartes91@gmail.com)
 */
class MoneyTransferRepositoryIntegrationTest {

  @After
  def after(): Unit = {
    Await.result(Db.db.run(Db.transactions.delete), Duration.Inf)
    Await.result(Db.db.run(Db.accounts.delete), Duration.Inf)
  }

  /**
    * @verifies throw exception when sender does not have enough fundsshould throw exception when sender
    * does not have enough funds
    * @see MoneyTransferRepository#createMoneyTransfer
    */
  @Test
  def createMoneyTransfer_shouldThrowExceptionWhenSenderDoesNotHaveEnoughFunds(): Unit = {
    AccountRepository.createAccount(Account("1", 2))
    AccountRepository.createAccount(Account("2", 3))
    Assertions.assertThatExceptionOfType(classOf[RuntimeException])
      .isThrownBy(() => MoneyTransferRepository.createMoneyTransfer(MoneyTransferDto("1", "2", 3)))
      .withMessage("Sender does not have enough funds")
    val transactions: Seq[Transaction] = Await.result(Db.db.run(Db.transactions.result), Duration.Inf)
    Assertions.assertThat(transactions.isEmpty).isTrue
    Assertions.assertThat(getAccount("1").amount).isEqualTo(2)
    Assertions.assertThat(getAccount("2").amount).isEqualTo(3)
  }

  /**
    * @verifies behave correctly
    * @see MoneyTransferRepository#createMoneyTransfer
    */
  @Test
  def createMoneyTransfer_shouldBehaveCorrectly(): Unit = {
    AccountRepository.createAccount(Account("1", 3))
    AccountRepository.createAccount(Account("2", 4))
    val transactionId = MoneyTransferRepository.createMoneyTransfer(MoneyTransferDto("1", "2", 2))
    Assertions.assertThat(getAccount("1").amount).isEqualTo(1)
    Assertions.assertThat(getAccount("2").amount).isEqualTo(6)

    val transaction: Transaction = Await.result(
      Db.db.run(Db.transactions.filter(_.id === transactionId).result.head), Duration.Inf
    )
    Assertions.assertThat(transaction.amount).isEqualTo(2)
    Assertions.assertThat(transaction.senderAccountNumber).isEqualTo("1")
    Assertions.assertThat(transaction.recipientAccountNumber).isEqualTo("2")
    Assertions.assertThat(transaction.time).isNotNull
  }

  private def getAccount(accountNumber: String): Account = {
    Await.result(Db.db.run(Db.accounts.filter(_.accountNumber === accountNumber).result.head), Duration.Inf)
  }
}