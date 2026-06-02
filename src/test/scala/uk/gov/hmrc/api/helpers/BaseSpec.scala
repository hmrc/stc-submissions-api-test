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

package uk.gov.hmrc.api.helpers

import org.scalatest.GivenWhenThen
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.Application
import uk.gov.hmrc.api.services.ServiceFactory
import uk.gov.hmrc.http.client.HttpClientV2

import scala.concurrent.ExecutionContext

trait BaseSpec
    extends AnyFeatureSpec
    with GivenWhenThen
    with Matchers
    with ScalaFutures
    with FormatHelper
    with GuiceOneServerPerSuite {

  implicit lazy val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  implicit override val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(10, Seconds), interval = Span(500, Millis))

  override lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure("play.server.provider" -> "play.core.server.PekkoHttpServerProvider")
      .bindings(new Module)
      .build()

  protected lazy val httpClient: HttpClientV2 = app.injector.instanceOf[HttpClientV2]
  protected lazy val service: ServiceFactory  = new ServiceFactory(httpClient)
}
