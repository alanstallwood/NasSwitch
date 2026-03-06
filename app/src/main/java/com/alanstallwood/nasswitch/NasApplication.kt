package com.alanstallwood.nasswitch

import android.app.Application
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security



class NasApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        Security.removeProvider("BC")
        Security.insertProviderAt(BouncyCastleProvider(), 1)
        container = AppContainer(this)
    }
}
