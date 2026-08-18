#!/usr/bin/env bash

ENVIRONMENT=$1

sbt scalafmtAll scalafmtSbt scalafmtCheckAll scalafmtSbtCheck clean compile -Denvironment="${ENVIRONMENT:=qa}" "testOnly uk.gov.hmrc.api.specs.*"