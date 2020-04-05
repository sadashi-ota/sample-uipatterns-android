package com.sadashi.apps.ui.samples.usecase

import io.reactivex.Completable

interface LoadLatestInfoUseCase {
    fun execute(setting: ConnectionSettingDto): Completable
}