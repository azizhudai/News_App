package com.mindfulness.googlenewsembed.data.entities

import com.mindfulness.googlenewsembed.data.entities.response.ArticlesResponse

open class DataResult<T>(var message:String?, var data: List<T>?){
    constructor():this(null,null)
}