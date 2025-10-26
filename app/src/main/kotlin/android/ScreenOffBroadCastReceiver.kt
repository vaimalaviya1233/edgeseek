/*
 *	Copyright 2020-2022 LSafer
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */
package net.lsafer.edgeseek.app.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import net.lsafer.edgeseek.app.android.MainApplication.Companion.globalLocal

class ScreenOffBroadCastReceiver : BroadcastReceiver() {
    companion object {
        private val TAG = ScreenOffBroadCastReceiver::class.simpleName!!
    }

    private val local = globalLocal

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_SCREEN_OFF) {
            if (!globalLocal.repo.brightnessReset)
                return

            try {
                Settings.System.putInt(
                    context.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                )
                local.dimmer.update(0)
            } catch (e: Exception) {
                Log.e(TAG, "Couldn't toggle auto brightness", e)
            }
        }
    }
}
