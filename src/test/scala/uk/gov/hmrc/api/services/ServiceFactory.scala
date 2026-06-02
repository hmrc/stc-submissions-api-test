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

package uk.gov.hmrc.api.services

import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSBodyWritables.*
import uk.gov.hmrc.api.conf.TestEnvironment
import uk.gov.hmrc.apitestrunner.util.ApiLogger.log
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{Authorization, HeaderCarrier, HttpResponse}

import java.net.URI
import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ServiceFactory @Inject() (client: HttpClientV2)(implicit ec: ExecutionContext) {

  private val submissionsBaseUrl: String = TestEnvironment.url("stcSubmissions")
  private val submissionsApiBase: String = TestEnvironment.url("stcSubmissionsBase")
  private val authService: AuthService   = new AuthService(client)

  def getBearerToken: Future[String] = authService.getBearerToken()

  def postSubmission(
    submissionId: String,
    payload: JsValue,
    bearerToken: String,
    subscriptionId: String = "sub-id-001"
  ): Future[HttpResponse] = {
    val correlationId              = UUID.randomUUID().toString
    val url                        = s"$submissionsBaseUrl/$submissionId"
    log.info(s"POST $url with correlation-id: $correlationId")
    implicit val hc: HeaderCarrier = HeaderCarrier(
      authorization = Some(Authorization(s"Bearer $bearerToken")),
      extraHeaders = Seq(
        "correlation-id"  -> correlationId,
        "subscription-id" -> subscriptionId
      )
    )
    client
      .post(URI.create(url).toURL)
      .withBody(payload)
      .execute[HttpResponse]
  }

  def postSubmissionWithoutRequiredHeaders(
    submissionId: String,
    payload: JsValue,
    bearerToken: String
  ): Future[HttpResponse] = {
    val url                        = s"$submissionsBaseUrl/$submissionId"
    log.info(s"POST $url (no required headers)")
    implicit val hc: HeaderCarrier = HeaderCarrier(
      authorization = Some(Authorization(s"Bearer $bearerToken"))
    )
    client
      .post(URI.create(url).toURL)
      .withBody(payload)
      .execute[HttpResponse]
  }

  def postToInvalidPath(bearerToken: String): Future[HttpResponse] = {
    val url                        = s"$submissionsApiBase/invalid-path"
    val correlationId              = UUID.randomUUID().toString
    log.info(s"POST $url")
    implicit val hc: HeaderCarrier = HeaderCarrier(
      authorization = Some(Authorization(s"Bearer $bearerToken")),
      extraHeaders = Seq(
        "correlation-id"  -> correlationId,
        "subscription-id" -> "sub-id-001"
      )
    )
    client
      .post(URI.create(url).toURL)
      .withBody(Json.obj())
      .execute[HttpResponse]
  }
}
