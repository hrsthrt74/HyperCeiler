package com.sevtinge.cemiuiler.module.home.recent

import android.app.Activity
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.sevtinge.cemiuiler.module.base.BaseHook
import com.sevtinge.cemiuiler.utils.callStaticMethod
import com.sevtinge.cemiuiler.utils.findClass
import com.sevtinge.cemiuiler.utils.hookAfterAllMethods
import com.sevtinge.cemiuiler.utils.hookBeforeMethod

object BlurLevel : BaseHook() {
    override fun init() {
        val mBlurClass = loadClass("com.miui.home.launcher.common.BlurUtils")

        when (val blurLevel = mPrefsMap.getStringAsInt("home_recent_blur_level", 6)) {
            4 -> {
                mBlurClass.methodFinder().first {
                    name == "getBlurType"
                }.createHook {
                    returnConstant(0)
                }

                mBlurClass.methodFinder().first {
                    name == "isUseCompleteBlurOnDev"
                }.createHook {
                    returnConstant(false)
                }

                "com.miui.home.launcher.common.DeviceLevelUtils".hookBeforeMethod("isUseSimpleAnim") {
                    it.result = true
                }
            }
            5 -> {
                val blurClass = "com.miui.home.launcher.common.BlurUtils".findClass()
                val navStubViewClass = "com.miui.home.recents.NavStubView".findClass()
                val applicationClass = "com.miui.home.launcher.Application".findClass()
                navStubViewClass.hookAfterAllMethods("onTouchEvent") {
                    val mLauncher = applicationClass.callStaticMethod("getLauncher") as Activity
                    blurClass.callStaticMethod("fastBlur", 1.0f, mLauncher.window, true, 500L)
                }
                "com.miui.home.launcher.common.DeviceLevelUtils".hookBeforeMethod("isUseSimpleAnim") {
                    it.result = false
                }

                mBlurClass.methodFinder().first {
                    name == "getBlurType"
                }.createHook {
                    before {
                        when (blurLevel) {
                            5 -> it.result = 2
                        }
                    }
                }
            }
            else -> {
                "com.miui.home.launcher.common.DeviceLevelUtils".hookBeforeMethod("isUseSimpleAnim") {
                    it.result = false
                }

                mBlurClass.methodFinder().first {
                    name == "getBlurType"
                }.createHook {
                    before {
                        when (blurLevel) {
                            0 -> it.result = 2
                            2 -> it.result = 1
                            3 -> it.result = 0
                        }
                    }
                }

                mBlurClass.methodFinder().first {
                    name == "isUseCompleteBlurOnDev"
                }.createHook {
                    before {
                        when (blurLevel) {
                            1 -> it.result = true
                        }
                    }
                }
            }
        }

    }
}