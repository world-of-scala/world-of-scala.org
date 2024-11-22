package com.example.worldofscala.app

import dev.cheleb.scalamigen.*
import dev.cheleb.scalamigen.ui5.UI5WidgetFactory

import dev.cheleb.ziolaminartapir.*

import com.example.worldofscala.domain.*

given Form[Password] = secretForm(Password(_))

given f: WidgetFactory = UI5WidgetFactory

given session: Session[UserToken] = SessionLive[UserToken]
