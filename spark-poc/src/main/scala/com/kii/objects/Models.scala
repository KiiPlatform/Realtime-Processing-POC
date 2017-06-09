package com.kii.objects

case class KiiBucket(val scope: String, val scopeId: String, val bucketId: String)
case class NREConfig(
                    val truststorePassword: String, val keystorePassword : String,
                    val keyPassword : String,
                    val kafkaNREbootstrapServers : String, val kafkaNRETopic : String,
                    val kafkaNREGroupId : String
                     )
case class KiiConfig(val host : String, val appID : String, val appKey : String, val accessToken : String)