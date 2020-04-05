package com.sadashi.apps.ui.samples.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sadashi.apps.ui.samples.R
import com.sadashi.apps.ui.samples.presentation.InputPasswordContract
import com.sadashi.apps.ui.samples.presentation.InputPasswordPresenterImpl
import com.sadashi.apps.ui.samples.usecase.ConnectionSettingDto
import com.sadashi.apps.ui.samples.usecase.LoadLatestInfoUseCaseImpl
import com.sadashi.apps.ui.samples.usecase.ProxySetting
import com.sadashi.apps.ui.samples.usecase.SaveConnectionSettingUseCaseImpl
import kotlinx.android.synthetic.main.activity_input_password.*

class InputPasswordActivity : AppCompatActivity(), InputPasswordContract.View {

    lateinit var presenter: InputPasswordContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_password)

        presenter = InputPasswordPresenterImpl(
            this,
            LoadLatestInfoUseCaseImpl(),
            SaveConnectionSettingUseCaseImpl()
        )

        enabled_proxy.setOnCheckedChangeListener { _, enabled ->
            presenter.onEnableProxySettingChanged(enabled)
        }

        done.setOnClickListener {
            presenter.onClickDone(
                ConnectionSettingDto(
                    domain_text.text.toString(),
                    enabled_proxy.isChecked,
                    ProxySetting(
                        proxy_text.text.toString(),
                        port_text.text.toString().toIntOrNull(),
                        id_text.text.toString(),
                        password_text.text.toString()
                    )
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun onStop() {
        super.onStop()
        presenter.stop()
    }

    override fun showLoading() {}
    override fun hideLoading() {}
    override fun showConfirmConnection() {}
    override fun hideConfirmConnection() {}

    override fun finishSave() {
        Toast.makeText(this, "OK", Toast.LENGTH_LONG).show()
    }

    override fun setEnableProxySetting(enabled: Boolean) {
        enabled_proxy.isChecked = enabled
        proxy_text.isEnabled = enabled
        port_text.isEnabled = enabled
        id_text.isEnabled = enabled
        password_text.isEnabled = enabled
    }

    override fun updateSetting(setting: ConnectionSettingDto) {
        domain_text.setText(setting.domain)
        proxy_text.setText(setting.proxySetting.proxy)
        port_text.setText(setting.proxySetting.port.toString())
        id_text.setText(setting.proxySetting.id)
        password_text.setText(setting.proxySetting.password)
    }
}