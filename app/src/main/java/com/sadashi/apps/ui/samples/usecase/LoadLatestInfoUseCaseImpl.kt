package com.sadashi.apps.ui.samples.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

class LoadLatestInfoUseCaseImpl : LoadLatestInfoUseCase {
    override fun execute(setting: ConnectionSettingDto): Completable {
        return Completable.create {
            Thread.sleep(1000)
            isValid(setting)
            it.onComplete()
        }.subscribeOn(Schedulers.computation())
    }

    private fun isValid(setting: ConnectionSettingDto) {
        setting.domain.isNotEmpty() || throw IllegalArgumentException("Argument is invalid.")
        if (setting.isProxyEnabled) {
            if (setting.proxySetting.proxy.isNotEmpty() &&
                setting.proxySetting.port != null &&
                setting.proxySetting.id.isNotEmpty() &&
                setting.proxySetting.password.isNotEmpty()
            ) {
                return
            }
            throw IllegalArgumentException("Argument is invalid.")
        }
    }
}