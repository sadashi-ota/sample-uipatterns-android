package com.sadashi.apps.ui.samples.presentation

import com.sadashi.apps.ui.samples.BuildConfig
import com.sadashi.apps.ui.samples.usecase.ConnectionSettingDto
import com.sadashi.apps.ui.samples.usecase.LoadLatestInfoUseCase
import com.sadashi.apps.ui.samples.usecase.ProxySetting
import com.sadashi.apps.ui.samples.usecase.SaveConnectionSettingUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class InputPasswordPresenterImpl(
    private val view: InputPasswordContract.View,
    private val loadUseCase: LoadLatestInfoUseCase,
    private val saveUseCase: SaveConnectionSettingUseCase
) : InputPasswordContract.Presenter {

    private val disposable = CompositeDisposable()

    private lateinit var setting: ConnectionSettingDto

    override fun start() {
        isProxyEnabledForFirst().let {
            setting = ConnectionSettingDto(
                domain = "hoge",
                isProxyEnabled = it,
                proxySetting = ProxySetting(
                    "1.2.3.4",
                    789,
                    BuildConfig.PROXY_USER_NAME,
                    BuildConfig.PROXY_PASSWORD
                )
            )
            view.setEnableProxySetting(it)
        }
        view.updateSetting(setting)
    }

    override fun stop() {
        disposable.dispose()
    }

    override fun onEnableProxySettingChanged(enabled: Boolean) {
        view.setEnableProxySetting(enabled)
    }

    override fun onClickDone(setting: ConnectionSettingDto) {
        this.setting = setting
        saveUseCase.execute(setting)
            .doOnSubscribe { view.showLoading() }
            .andThen(loadUseCase.execute(setting))
            .doAfterTerminate {
                view.hideLoading()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                view.finishSave()
            }
            .addTo(disposable)
    }

    private fun isProxyEnabledForFirst(): Boolean {
        return BuildConfig.PROXY_USER_NAME.isNotEmpty() && BuildConfig.PROXY_PASSWORD.isNotEmpty()
    }

}