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

package uk.gov.hmrc.api.models

import play.api.libs.json.{Json, Reads, Writes}

import java.time.LocalDate

final case class SingleTransferDeclaration(
  role1: Option[String] = Some("1"),
  role2: Option[String] = None,
  name: String = "Declarant Name",
  addr1: String = "123 Main Street",
  addr2: Option[String] = None,
  addr3: Option[String] = None,
  addr4: Option[String] = None,
  postcode: String = "AB12CD",
  country: String = "GB",
  selfDeclarationAgent: Option[Boolean] = None,
  isCorrectInfo: Boolean = true
)

final case class SingleTransferTransactionDetails(
  transactionType: Int = 1,
  reasonForPurchase: Option[Int] = None,
  typeOfSecurity: String = "Ordinary Shares",
  numberOfShares: Int = 100,
  nominalValue: Option[BigDecimal] = None,
  marketValue: Option[BigDecimal] = None,
  qualifyAsTreasuryShares: Option[Boolean] = None,
  maxPricePaid: Option[BigDecimal] = None,
  minPricePaid: Option[BigDecimal] = None,
  originalChargingPoint: LocalDate = LocalDate.of(2026, 1, 15),
  considerationActual: BigDecimal = 5000,
  isConnectedPartiesTransactions: Boolean = false,
  companyName: String = "Test Corporation",
  companyRegistrationNumber: Option[String] = None,
  reliefClaimedName: Option[String] = None,
  reliefPercentage: Option[Int] = None
)

final case class SingleTransferContingentDetails(
  provisionalDate: LocalDate = LocalDate.of(2026, 1, 15),
  isAmountUnasertainable: Boolean = false,
  unascertainableAmount: Option[BigDecimal] = None,
  ascertainableAmount: Option[BigDecimal] = None,
  defermentOfPayment: Boolean = false,
  originalDefermentDate: Option[LocalDate] = None
)

final case class SingleTransferSellerDetails(
  sellerName: String = "Seller Name",
  addr1: String = "100 Seller Lane",
  addr2: Option[String] = None,
  addr3: Option[String] = None,
  addr4: Option[String] = None,
  postcode: String = "AB12CD",
  country: String = "GB"
)

final case class SingleTransferOtherSellerName(
  sellerName: String = "Other Seller"
)

final case class SingleTransferBuyerDetails(
  buyerName: String = "Buyer Name",
  addr1: String = "50 Buyer Road",
  addr2: Option[String] = None,
  addr3: Option[String] = None,
  addr4: Option[String] = None,
  postcode: String = "AB12CD",
  country: String = "GB",
  email: String = "buyer@example.com",
  uniqueId: Option[String] = None,
  taxRate: Int = 1,
  isPLC: Option[Boolean] = None
)

final case class SingleTransferOtherBuyerName(
  buyerName: String = "Other Buyer"
)

final case class SingleTransferAgentDetails(
  name: String = "Agent Name",
  addr1: String = "Agent Address",
  addr2: Option[String] = None,
  addr3: Option[String] = None,
  addr4: Option[String] = None,
  postcode: String = "AB12CD",
  country: String = "GB",
  phone: String = "01234567890",
  email: String = "agent@example.com",
  clientReference: String = "CLIENT-REF-001"
)

final case class SingleTransferRequest(
  recordId: Int = 1,
  transactionDetails: SingleTransferTransactionDetails = SingleTransferTransactionDetails(),
  contingentDetails: Option[Seq[SingleTransferContingentDetails]] = None,
  mainSellerDetails: SingleTransferSellerDetails = SingleTransferSellerDetails(),
  otherSellers: Option[Seq[SingleTransferOtherSellerName]] = None,
  mainBuyerDetails: SingleTransferBuyerDetails = SingleTransferBuyerDetails(),
  otherBuyers: Option[Seq[SingleTransferOtherBuyerName]] = None,
  agentDetails: Option[Seq[SingleTransferAgentDetails]] = None
)

final case class SubmissionBatchPayload(
  declaration: SingleTransferDeclaration = SingleTransferDeclaration(),
  transfers: Seq[SingleTransferRequest] = Seq(SingleTransferRequest())
)

object SubmissionBatchPayload:
  given Reads[LocalDate]  = Reads.DefaultLocalDateReads
  given Writes[LocalDate] = Writes.DefaultLocalDateWrites

  given Reads[SingleTransferDeclaration]  = Json.reads[SingleTransferDeclaration]
  given Writes[SingleTransferDeclaration] = Json.writes[SingleTransferDeclaration]

  given Reads[SingleTransferAgentDetails]  = Json.reads[SingleTransferAgentDetails]
  given Writes[SingleTransferAgentDetails] = Json.writes[SingleTransferAgentDetails]

  given Reads[SingleTransferOtherBuyerName]  = Json.reads[SingleTransferOtherBuyerName]
  given Writes[SingleTransferOtherBuyerName] = Json.writes[SingleTransferOtherBuyerName]

  given Reads[SingleTransferBuyerDetails]  = Json.reads[SingleTransferBuyerDetails]
  given Writes[SingleTransferBuyerDetails] = Json.writes[SingleTransferBuyerDetails]

  given Reads[SingleTransferOtherSellerName]  = Json.reads[SingleTransferOtherSellerName]
  given Writes[SingleTransferOtherSellerName] = Json.writes[SingleTransferOtherSellerName]

  given Reads[SingleTransferSellerDetails]  = Json.reads[SingleTransferSellerDetails]
  given Writes[SingleTransferSellerDetails] = Json.writes[SingleTransferSellerDetails]

  given Reads[SingleTransferContingentDetails]  = Json.reads[SingleTransferContingentDetails]
  given Writes[SingleTransferContingentDetails] = Json.writes[SingleTransferContingentDetails]

  given Reads[SingleTransferTransactionDetails]  = Json.reads[SingleTransferTransactionDetails]
  given Writes[SingleTransferTransactionDetails] = Json.writes[SingleTransferTransactionDetails]

  given Reads[SingleTransferRequest]  = Json.reads[SingleTransferRequest]
  given Writes[SingleTransferRequest] = Json.writes[SingleTransferRequest]

  given Reads[SubmissionBatchPayload]  = Json.reads[SubmissionBatchPayload]
  given Writes[SubmissionBatchPayload] = Json.writes[SubmissionBatchPayload]
