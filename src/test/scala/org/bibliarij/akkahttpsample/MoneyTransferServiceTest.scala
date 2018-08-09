package org.bibliarij.akkahttpsample

import org.assertj.core.api.Assertions
import org.bibliarij.akkahttpsample.db.{AccountRepository, MoneyTransferRepository}
import org.bibliarij.akkahttpsample.web.MoneyTransferDto
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(classOf[MockitoJUnitRunner])
class MoneyTransferServiceTest {

  /**
    * @verifies should behave correctly
    */
  @Test
  def transferMoney_shouldBehaveCorrectly(): Unit = {

    val accountRepository: AccountRepository = Mockito.mock(classOf[AccountRepository])
    val moneyTransferRepository: MoneyTransferRepository = Mockito.mock(classOf[MoneyTransferRepository])

    Mockito.when(accountRepository.isAccountExists("1")).thenReturn(true)
    Mockito.when(accountRepository.isAccountExists("2")).thenReturn(true)

    val moneyTransferDto: MoneyTransferDto = MoneyTransferDto("1", "2", 3)
    Mockito.when(moneyTransferRepository.createMoneyTransfer(moneyTransferDto)).thenReturn(2)

    val transactionId: Long = new MoneyTransferService(moneyTransferRepository, accountRepository)
      .transferMoney(moneyTransferDto)
    Assertions.assertThat(transactionId).isEqualTo(2)
  }
}