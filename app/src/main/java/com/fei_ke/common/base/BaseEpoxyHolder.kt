package com.fei_ke.common.base

import androidx.annotation.CallSuper
import android.view.View
import com.airbnb.epoxy.EpoxyHolder

open class BaseEpoxyHolder : EpoxyHolder() {
    lateinit var itemView: View

    @CallSuper
    override fun bindView(view: View) {
        this.itemView = view
    }

}
