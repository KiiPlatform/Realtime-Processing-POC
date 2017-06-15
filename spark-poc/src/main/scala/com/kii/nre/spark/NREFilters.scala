package com.kii.nre.spark
import com.kii.nre.util._
import com.kii.objects._

trait NREFilter[A] {
    def filter(param : A, key : String,value : String) : Boolean
}

object BucketFilter extends NREFilter[KiiBucket] {
    def filter(param : KiiBucket, key : String, value : String) : Boolean = {
        val parsedBucket = URIParser.parse(key)._1
        param == parsedBucket
    }
}

