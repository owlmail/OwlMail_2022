package github.owlmail.app

import android.app.Application
import com.facebook.stetho.Stetho
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OwlMailApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Stetho.initializeWithDefaults(this)
    }
}
