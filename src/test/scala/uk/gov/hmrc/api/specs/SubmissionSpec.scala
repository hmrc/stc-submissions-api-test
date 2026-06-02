/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.api.specs

import org.scalatest.BeforeAndAfterAll
import org.scalatest.prop.TableDrivenPropertyChecks.forAll
import org.scalatest.prop.Tables.Table
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.api.helpers.BaseSpec
import uk.gov.hmrc.api.models.*
import uk.gov.hmrc.api.models.SubmissionBatchPayload.given
import uk.gov.hmrc.http.HttpResponse

import java.time.LocalDate
import scala.concurrent.Future

class SubmissionSpec extends BaseSpec with BeforeAndAfterAll {

  private var bearerToken: String = _

  override def beforeAll(): Unit =
    bearerToken = service.getBearerToken.futureValue

  private val singleTransfer: JsValue =
    Json.toJson(SubmissionBatchPayload())

  private val multipleTransfers: JsValue = {
    val transfer1 = SingleTransferRequest(recordId = 1)
    val transfer2 = SingleTransferRequest(
      recordId = 2,
      transactionDetails = SingleTransferTransactionDetails(
        transactionType = 2,
        typeOfSecurity = "Preference Shares",
        numberOfShares = 200,
        originalChargingPoint = LocalDate.of(2026, 2, 20),
        considerationActual = 10000,
        isConnectedPartiesTransactions = true,
        companyName = "Another Corporation"
      ),
      mainSellerDetails = SingleTransferSellerDetails(
        sellerName = "Another Seller",
        addr1 = "200 Seller Street",
        postcode = "CD34EF"
      ),
      mainBuyerDetails = SingleTransferBuyerDetails(
        buyerName = "Another Buyer",
        addr1 = "75 Buyer Avenue",
        postcode = "EF56GH",
        email = "another.buyer@example.com",
        taxRate = 2
      )
    )
    Json.toJson(SubmissionBatchPayload(transfers = Seq(transfer1, transfer2)))
  }

  private val emptyTransfers: JsValue =
    Json.toJson(SubmissionBatchPayload(transfers = Seq.empty))

  private val duplicateRecordIds: JsValue =
    Json.toJson(
      SubmissionBatchPayload(
        transfers = Seq(
          SingleTransferRequest(recordId = 1),
          SingleTransferRequest(recordId = 1)
        )
      )
    )

  private val missingDeclarationName: JsValue = Json.obj(
    "declaration" -> Json.obj(
      "role1"         -> "1",
      "addr1"         -> "123 Main Street",
      "postcode"      -> "AB12CD",
      "country"       -> "GB",
      "isCorrectInfo" -> true
    ),
    "transfers"   -> Json.arr(Json.toJson(SingleTransferRequest()))
  )

  Feature("Submit securities transfer charges") {

    val testCases = Table(
      ("description", "request", "expectedStatus"),
      ("Success - Single transfer", () => service.postSubmission("sub-001", singleTransfer, bearerToken), 200),
      ("Success - Multiple transfers", () => service.postSubmission("sub-002", multipleTransfers, bearerToken), 200),
      ("Error - Empty transfers array", () => service.postSubmission("sub-001", emptyTransfers, bearerToken), 400),
      ("Error - Duplicate recordIds", () => service.postSubmission("sub-001", duplicateRecordIds, bearerToken), 400),
      ("Error - Invalid JSON", () => service.postSubmission("sub-001", missingDeclarationName, bearerToken), 400),
      (
        "Error - Missing required headers",
        () => service.postSubmissionWithoutRequiredHeaders("sub-001", singleTransfer, bearerToken),
        400
      ),
      ("Error - Path not found", () => service.postToInvalidPath(bearerToken), 404)
    )

    forAll(testCases) { (description: String, request: () => Future[HttpResponse], expectedStatus: Int) =>
      Scenario(description) {

        Given("a submission request is prepared")

        When("the request is sent")
        whenReady(request()) { response =>
          Then(s"the response status should be $expectedStatus")
          checkResponseStatus(response.status, expectedStatus)
        }
      }
    }
  }
}
