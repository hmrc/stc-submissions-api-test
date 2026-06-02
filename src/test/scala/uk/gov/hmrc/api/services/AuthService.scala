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

import play.api.libs.json.Json
import play.api.libs.ws.WSBodyWritables.*
import uk.gov.hmrc.api.conf.TestEnvironment
import uk.gov.hmrc.api.models.AuthStubRequest
import uk.gov.hmrc.apitestrunner.util.ApiLogger.log
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import java.net.URI
import scala.concurrent.{ExecutionContext, Future}

class AuthService(client: HttpClientV2)(implicit ec: ExecutionContext) {

  private val authUrl: String = TestEnvironment.url("authStub")

  def getBearerToken(request: AuthStubRequest = AuthStubRequest()): Future[String] = {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    log.info(s"Fetching bearer token from $authUrl")
    client
      .post(URI.create(authUrl).toURL)
      .withBody(Json.toJson(request))
      .execute[HttpResponse]
      .map { response =>
        response.headers
          .find { case (k, _) => k.equalsIgnoreCase("Authorization") }
          .flatMap { case (_, values) => values.headOption }
          .flatMap(_.split(",").find(_.trim.startsWith("Bearer ")))
          .map(_.trim.replace("Bearer ", ""))
          .getOrElse(throw new RuntimeException(s"No Bearer token in auth stub response (status: ${response.status})"))
      }
  }
}
