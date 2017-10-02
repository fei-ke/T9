package com.fei_ke.t9

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.OnModelClickListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (0 until gridLayout.childCount).forEach {
            val view = gridLayout.getChildAt(it)
            view.tag = it + 1
            view.setOnClickListener(onKeyboardClickListener)

            if (it == gridLayout.childCount - 1) {
                view.setOnLongClickListener {
                    textViewKeyword.text = null
                    true
                }
            }
        }

        setupRecyclerView()

        mainViewModel = ViewModelProviders.of(this, ViewModelFactory()).get(MainViewModel::class.java)
        mainViewModel.appList().observe(this, Observer {
            if (it!!.loadMore) {
                listAdapter.addMore(it.data)
            } else {
                listAdapter.update(it.data)
            }
        })

        mainViewModel.query(null)

        textViewKeyword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                mainViewModel.query(text.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

    private fun setupRecyclerView() {
        val spanCount = 3
        val layoutManager = GridLayoutManager(this, spanCount)
        listAdapter.spanCount = spanCount
        layoutManager.spanSizeLookup = listAdapter.spanSizeLookup
        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = layoutManager

        recyclerView.itemAnimator = null

        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                finish()
                return true
            }
        })

        recyclerView.setOnTouchListener({ _, event ->
            gestureDetector.onTouchEvent(event)
        })
    }

    private val onKeyboardClickListener = View.OnClickListener {
        when {
            it.tag == 10 -> if (textViewKeyword.length() > 0) textViewKeyword.text = null else finish()
            it.tag == 12 -> if (textViewKeyword.length() > 0) textViewKeyword.editableText.delete(textViewKeyword.length() - 1, textViewKeyword.length())
            it.tag == 11 -> textViewKeyword.append("0")
            else -> textViewKeyword.append(it.tag.toString())
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (!textViewKeyword.text.isEmpty()) {
            textViewKeyword.setText(null)
        } else {
            finish()
        }
    }

    private val onAppClickListener = OnModelClickListener<AppModel_, AppModel.ViewHolder> { model, _, _, _ ->
        val app = model.app
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.setClassName(app.pkgName, app.className)
        if (this.packageName == app.pkgName) {
            return@OnModelClickListener
        } else {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        }
        startActivity(intent)
    }

    private val listAdapter = object : EpoxyAdapter() {
        init {
        }

        fun addMore(appList: List<App>) {
            val list = appList.map {
                AppModel_(it)
                        .onAppClickListener(onAppClickListener)
            }
            addModels(list)
        }

        fun update(appList: List<App>) {
            val list = appList.map {
                AppModel_(it)
                        .onAppClickListener(onAppClickListener)
            }
            models.clear()
            models.addAll(list)
            notifyDataSetChanged()
        }
    }
}
