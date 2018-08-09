package org.bibliarij.akkahttpsample.web

import akka.http.scaladsl.model.{HttpEntity, HttpResponse, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.{Directives, ExceptionHandler, Route}
import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import org.bibliarij.akkahttpsample.db.{AccountRepository, MoneyTransferRepository, TransactionRepository}
import org.bibliarij.akkahttpsample.{Account, MoneyTransferService}

/**
  * @author Vladimir Nizamutdinov (astartes91@gmail.com)
  */
object Router extends Directives with JacksonJsonSupport {

  private lazy val moneyTransferService: MoneyTransferService = new MoneyTransferService(
    MoneyTransferRepository, AccountRepository
  )

  implicit lazy val myExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case e: Throwable =>
        e.printStackTrace()
        complete(
          HttpResponse(
            StatusCodes.InternalServerError,
            entity = HttpEntity(MediaTypes.`application/json`, serialize(Map("error" -> e.getMessage)))
          )
        )
    }

  lazy val jacksonObjectMapper = new ObjectMapper()
  jacksonObjectMapper.findAndRegisterModules()
  jacksonObjectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

  private def moneyTransfersRoute: Route =
    path("money-transfers") {
      get {
        complete(TransactionRepository.getTransactions())
      } ~
      post {
        entity(as[MoneyTransferDto]) { moneyTransfer =>
          complete(Map("id" -> moneyTransferService.transferMoney(moneyTransfer)))
        }
      }
    }

  private def accountsRoute: Route =
    path("accounts") {
      get {
        complete(AccountRepository.getAccounts())
      } ~
      post {
        entity(as[Account]) { account =>
          AccountRepository.createAccount(account)
          complete(StatusCodes.OK)
        }
      }
    }

  def route: Route = moneyTransfersRoute ~ accountsRoute
}
