package com.sadashi.apps.ui.samples.usecase

import io.reactivex.Completable

data class ProxySetting(
    val proxy: String,
    val port: Int?,
    val id: String,
    val password: String
)

data class ConnectionSettingDto(
    val domain: String,
    val isProxyEnabled: Boolean,
    val proxySetting: ProxySetting
)

interface SaveConnectionSettingUseCase {
    fun execute(setting: ConnectionSettingDto): Completable
}