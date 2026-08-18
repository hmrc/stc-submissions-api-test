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

import play.api.libs.json.{Json, OFormat}

final case class AuthStubIdentifier(key: String, value: String)

final case class AuthStubEnrolment(
  key: String,
  identifiers: Seq[AuthStubIdentifier],
  state: String = "Activated"
)

final case class AuthStubRequest(
  credId: String = "test-cred-id",
  affinityGroup: String = "Individual",
  confidenceLevel: Int = 50,
  credentialStrength: String = "strong",
  enrolments: Seq[AuthStubEnrolment] = Seq(
    AuthStubEnrolment(
      key = "HMRC-STC-ORG",
      identifiers = Seq(AuthStubIdentifier(key = "STCID", value = "STC0123456789"))
    )
  )
)

object AuthStubRequest {
  implicit val identifierFormat: OFormat[AuthStubIdentifier] = Json.format[AuthStubIdentifier]
  implicit val enrolmentFormat: OFormat[AuthStubEnrolment]   = Json.format[AuthStubEnrolment]
  implicit val format: OFormat[AuthStubRequest]              = Json.format[AuthStubRequest]
}
