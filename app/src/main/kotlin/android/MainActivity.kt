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

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import net.lsafer.edgeseek.app.android.MainApplication.Companion.globalLocal
import net.lsafer.edgeseek.app.android.MainApplication.Companion.globalNavCtrl
import net.lsafer.edgeseek.app.components.window.main.MainWindow
import net.lsafer.edgeseek.app.components.wrapper.AppLocaleProvider
import net.lsafer.edgeseek.app.components.wrapper.AppTheme
import net.lsafer.edgeseek.app.components.wrapper.AppWindowCompat

class MainActivity : ComponentActivity() {
    private val local = globalLocal
    private val navCtrl = globalNavCtrl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        setContent {
            context(local, navCtrl) {
                AppLocaleProvider {
                    AppWindowCompat {
                        AppTheme {
                            Surface(color = MaterialTheme.colorScheme.background) {
                                MainWindow()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MainService.start(this)
    }
}
