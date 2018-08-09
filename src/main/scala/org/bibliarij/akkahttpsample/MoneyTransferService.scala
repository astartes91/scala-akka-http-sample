package org.bibliarij.akkahttpsample

import org.bibliarij.akkahttpsample.db.{AccountRepository, MoneyTransferRepository}
import org.bibliarij.akkahttpsample.web.MoneyTransferDto

class MoneyTransferService(moneyTransferRepository: MoneyTransferRepository, accountRepository: AccountRepository) {

  /**
    * @should behave correctly
    * @param moneyTransferDto
    * @return
    */
  def transferMoney(moneyTransferDto: MoneyTransferDto): Long = {

    require(moneyTransferDto != null, "Request body can't be null")

    val senderAccountNumber: String = moneyTransferDto.senderAccountNumber
    require(senderAccountNumber != null, "Sender account number can't be null")

    val recipientAccountNumber: String = moneyTransferDto.recipientAccountNumber
    require(recipientAccountNumber != null, "Recipient account number can't be null")

    val amount: BigDecimal = moneyTransferDto.amount
    require(amount != null, "Amount can't be null")
    require(amount.compare(BigDecimal.valueOf(0)) > 0, "Amount can't be less or equal to zero")

    require(accountRepository.isAccountExists(senderAccountNumber), "Sender account not found")
    require(accountRepository.isAccountExists(recipientAccountNumber), "Recipient account not found")

    moneyTransferRepository.createMoneyTransfer(moneyTransferDto)
  }
}
