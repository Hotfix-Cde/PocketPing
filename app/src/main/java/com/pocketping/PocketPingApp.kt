package com.pocketping

import android.app.Application
import com.pocketping.domain.PocketPingGraph

class PocketPingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        PocketPingGraph.init(this)
    }
}
