package com.sadashi.apps.ui.samples.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

class SaveConnectionSettingUseCaseImpl: SaveConnectionSettingUseCase {
    override fun execute(setting: ConnectionSettingDto): Completable {
        return Completable.create {
            Thread.sleep(1000)
            it.onComplete()
        }.subscribeOn(Schedulers.computation())
    }
}