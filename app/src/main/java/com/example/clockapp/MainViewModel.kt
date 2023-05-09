package com.example.clockapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val compositeDisposable = CompositeDisposable()
    val clockAngelLD = MutableLiveData<ClockAngel>()

    fun startArrows() {
        val calendar = Calendar.getInstance()
        compositeDisposable.add(
            Observable.interval(1, TimeUnit.SECONDS)
                .map {
                    calendar.timeInMillis = System.currentTimeMillis()
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)
                    val second = calendar.get(Calendar.SECOND)
                    ClockAngel(hour, minute, second)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    calendar.timeInMillis = System.currentTimeMillis()
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)
                    val second = calendar.get(Calendar.SECOND)
                    clockAngelLD.value = ClockAngel(hour, minute, second)
                }
                .subscribe {
                    clockAngelLD.value = it
                }
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}


