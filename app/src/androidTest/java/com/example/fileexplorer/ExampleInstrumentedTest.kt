package com.example.fileexplorer

import android.content.pm.ActivityInfo
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun useAppContext() {
        assertEquals("com.example.fileexplorer", appContext.packageName)
    }

    @Test
    fun testAppCreation() {
        val app = appContext.applicationContext
        assertNotNull(app)
    }

    // Test for launching a specific activity
    @Test
    fun testLaunchMainActivity() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            assertNotNull(scenario)
        }
    }

    // Test for checking a resource (e.g., a string)
    @Test
    fun testStringResource() {
        val appName = appContext.getString(R.string.app_name)
        assertEquals("File Explorer", appName)
    }

    // Test for handling configuration changes like screen rotation
    @Test
    fun testConfigurationChange() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity { activity ->
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }
}