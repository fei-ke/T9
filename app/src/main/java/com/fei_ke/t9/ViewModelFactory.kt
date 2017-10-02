package com.fei_ke.t9

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(aClass: Class<T>): T {
        if (cache.containsKey(aClass)) {
            return cache[aClass] as T
        }
        val constructor = aClass.getConstructor(Application::class.java)
        val viewModel = constructor.newInstance(Application.instance)
        cache.put(aClass, viewModel)
        return viewModel
    }

    companion object {
        val cache = HashMap<Class<out ViewModel>, ViewModel>()
    }
}
