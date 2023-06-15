package com.sevtinge.cemiuiler.module.securitycenter.sidebar.video

import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import com.sevtinge.cemiuiler.module.base.BaseHook
import com.sevtinge.cemiuiler.module.securitycenter.SecurityCenterDexKit
import com.sevtinge.cemiuiler.utils.DexKit
import java.util.Objects

object UnlockEnhanceContours : BaseHook() {
    override fun init() {
        try {
            val result = Objects.requireNonNull(
                SecurityCenterDexKit.mSecurityCenterResultClassMap["FrcSupport"]
            )
            for (descriptor in result) {
                val frcSupport = descriptor.getClassInstance(lpparam.classLoader)
                log("frcSupport class is $frcSupport")
                var counter = 0
                DexKit.dexKitBridge.findMethod {
                    methodDeclareClass = frcSupport.name
                    methodReturnType = "boolean"
                    methodParamTypes = arrayOf("java.lang.String")
                }.forEach { methods ->
                    counter++
                    if (counter == 3) {
                        methods.getMethodInstance(EzXHelper.classLoader).createHook {
                            returnConstant(true)
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        try {
            val result = Objects.requireNonNull(
                SecurityCenterDexKit.mSecurityCenterResultMap["AisSupport"]
            )
            for (descriptor in result) {
                val aisSupport = descriptor.getMethodInstance(lpparam.classLoader)
                log("aisSupport method is $aisSupport")
                val newChar = aisSupport.name.toCharArray()
                for (i in newChar.indices) {
                    newChar[i]++
                }
                val newName = String(newChar)
                aisSupport.declaringClass.methodFinder()
                    .filterByName(newName)
                    .first().createHook {
                        returnConstant(true)
                    }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}