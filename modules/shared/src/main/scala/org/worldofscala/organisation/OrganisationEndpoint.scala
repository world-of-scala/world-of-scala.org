package org.worldofscala.organisation

import zio.*
import sttp.tapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.*
import org.worldofscala.domain.*
import org.worldofscala.auth.LoginPassword
import sttp.model.HeaderNames
import org.worldofscala.BaseEndpoint
import org.worldofscala.auth.*

object OrganisationEndpoint extends BaseEndpoint:

  val create: PublicEndpoint[NewOrganisation, Throwable, Organisation, Any] = baseEndpoint
    .tag("Admin")
    .name("organisation")
    .post
    .in("organisation")
    .in(
      jsonBody[NewOrganisation]
        .description("Person to insert")
        .example(
          NewOrganisation(
            "EPFL",
            Some(LatLon(46.519653, 6.632273))
          )
        )
    )
    .out(jsonBody[Organisation])
    .description("Create person")
