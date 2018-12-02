package com.fei_ke.t9

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(aClass: Class<T>): T {
        if (cache.containsKey(aClass)) {
            return cache[aClass] as T
        }
        val constructor = aClass.getConstructor(App::class.java)
        val viewModel = constructor.newInstance(App.instance)
        cache.put(aClass, viewModel)
        return viewModel
    }

    companion object {
        val cache = HashMap<Class<out ViewModel>, ViewModel>()
    }
}
