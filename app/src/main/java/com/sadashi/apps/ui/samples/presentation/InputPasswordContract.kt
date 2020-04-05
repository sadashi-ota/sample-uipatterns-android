package com.sadashi.apps.ui.samples.presentation

import com.sadashi.apps.ui.samples.usecase.ConnectionSettingDto

interface InputPasswordContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showConfirmConnection()
        fun hideConfirmConnection()
        fun finishSave()
        fun setEnableProxySetting(enabled: Boolean)
        fun updateSetting(setting: ConnectionSettingDto)
    }

    interface Presenter {
        fun start()
        fun stop()
        fun onEnableProxySettingChanged(enabled: Boolean)
        fun onClickDone(setting: ConnectionSettingDto)
    }
}