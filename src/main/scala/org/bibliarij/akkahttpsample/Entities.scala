package org.bibliarij.akkahttpsample

import java.time.LocalDateTime

/**
  * @author Vladimir Nizamutdinov (astartes91@gmail.com)
  */
case class Account(accountNumber: String, amount: BigDecimal)
case class Transaction(
  id: Long,
  senderAccountNumber: String,
  recipientAccountNumber: String,
  amount: BigDecimal,
  time: LocalDateTime
)
