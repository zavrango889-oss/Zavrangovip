package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.SystemTelemetryRepository
import com.example.model.SensitivityPresetType
import com.example.model.SensitivityState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ZAVRANGOFFZ", appName)
    }

    @Test
    fun `sensitivity values strictly bounded between 0 and 200`() {
        val preset = SensitivityState.createPreset(SensitivityPresetType.ONE_TAP_HEADSHOT)
        assertTrue(preset.general in 0..200)
        assertTrue(preset.redDot in 0..200)
        assertTrue(preset.scope2x in 0..200)
        assertTrue(preset.scope4x in 0..200)
        assertTrue(preset.scopeSniper in 0..200)
        assertTrue(preset.freeLook in 0..200)

        val rushPreset = SensitivityState.createPreset(SensitivityPresetType.RUSH_SHOTGUN)
        assertTrue(rushPreset.general in 0..200)
        assertTrue(rushPreset.redDot in 0..200)
        assertTrue(rushPreset.scope2x in 0..200)
        assertTrue(rushPreset.scope4x in 0..200)
        assertTrue(rushPreset.scopeSniper in 0..200)
        assertTrue(rushPreset.freeLook in 0..200)
    }

    @Test
    fun `system telemetry initializes properly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = SystemTelemetryRepository(context)

        val memInfo = repository.fetchMemoryInfo()
        assertNotNull(memInfo)

        val storageInfo = repository.fetchStorageInfo()
        assertNotNull(storageInfo)

        val boosterPrefs = repository.boosterPrefs.value
        assertTrue(boosterPrefs.performanceMode)
        assertTrue(boosterPrefs.touchOptimization)
        assertTrue(boosterPrefs.fpsBooster)
    }

    @Test
    fun `free fire max package constants are verified`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = SystemTelemetryRepository(context)
        assertEquals("com.dts.freefiremax", repository.FREE_FIRE_MAX_PKG)
        assertEquals("com.dts.freefireth", repository.FREE_FIRE_STANDARD_PKG)
    }

    @Test
    fun `free fire sensitivity generator is strictly bounded between 150 and 200 inclusive`() {
        repeat(50) {
            val profile = com.example.ui.components.generateValid150To200Profile()
            assertTrue("Geral must be in 150..200, but was ${profile.geral}", profile.geral in 150..200)
            assertTrue("Ponto Vermelho must be in 150..200, but was ${profile.pontoVermelho}", profile.pontoVermelho in 150..200)
            assertTrue("Mira 2x must be in 150..200, but was ${profile.mira2x}", profile.mira2x in 150..200)
            assertTrue("Mira 4x must be in 150..200, but was ${profile.mira4x}", profile.mira4x in 150..200)
            assertTrue("Mira AWM must be in 150..200, but was ${profile.miraAwm}", profile.miraAwm in 150..200)
            assertTrue("Olhadinha must be in 150..200, but was ${profile.olhadinha}", profile.olhadinha in 150..200)
        }
    }

    @Test
    fun `crosshair preferences repository initializes with FOV 98 degrees and persists state`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = com.example.data.CrosshairPreferencesRepository.getInstance(context)

        val fov = repo.panelPrefs.value.fovDegrees
        assertEquals(98, fov)

        repo.updateFov(105)
        assertEquals(105, repo.panelPrefs.value.fovDegrees)

        repo.updateStyle(com.example.model.CrosshairStyle.TARGET)
        assertEquals(com.example.model.CrosshairStyle.TARGET, repo.crosshairState.value.style)

        repo.updateColor(com.example.model.CrosshairColor.CYAN)
        assertEquals(com.example.model.CrosshairColor.CYAN, repo.crosshairState.value.color)
    }
}
