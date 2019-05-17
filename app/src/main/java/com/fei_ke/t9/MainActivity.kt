package com.fei_ke.t9

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.OnModelClickListener
import com.airbnb.epoxy.OnModelLongClickListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolBar)
        val actionBar = supportActionBar!!
        actionBar.setDisplayShowTitleEnabled(false)

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

        mainViewModel =
                ViewModelProviders.of(this, ViewModelFactory()).get(MainViewModel::class.java)
        mainViewModel.appList().observe(this, Observer {
            if (it!!.loadMore) {
                listAdapter.addMore(it.data)
            } else {
                listAdapter.update(it.data)
            }
        })

        mainViewModel.query(null)

        textViewKeyword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(text: Editable?) {
                mainViewModel.query(text.toString())
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_project -> startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.project_url))
                )
            )
            R.id.action_licenses -> startActivity(Intent(this, LicensesActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        textViewKeyword.text = null
    }

    private fun setupRecyclerView() {
        val spanCount = 3
        val layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, spanCount)
        listAdapter.spanCount = spanCount
        layoutManager.spanSizeLookup = listAdapter.spanSizeLookup
        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = layoutManager

        recyclerView.itemAnimator = null

        //close activity when tap empty area
        val gestureDetector = GestureDetector(this, object : SingleTapGestureListener() {
            override fun onSingleTap() {
                finish()
            }
        })
        recyclerView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }
    }

    private val onKeyboardClickListener = View.OnClickListener {
        when {
            it.tag == 10 -> if (textViewKeyword.length() > 0) textViewKeyword.text =
                    null else finish()
            it.tag == 12 -> if (textViewKeyword.length() > 0) textViewKeyword.editableText.delete(
                textViewKeyword.length() - 1,
                textViewKeyword.length()
            )
            it.tag == 11 -> textViewKeyword.append("0")
            else -> textViewKeyword.append(it.tag.toString())
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (!textViewKeyword.text.isEmpty()) {
            textViewKeyword.text = null
        } else {
            finish()
        }
    }

    private val onAppClickListener =
        OnModelClickListener<ShortcutModel_, ShortcutModel.ViewHolder> { model, _, _, _ ->
            val app = model.shortcut
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setClassName(app.pkgName, app.className)
            if (this.packageName == app.pkgName) {
                return@OnModelClickListener
            } else {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
            startActivity(intent)
        }

    private val onAppLongClickListener =
        OnModelLongClickListener<ShortcutModel_, ShortcutModel.ViewHolder> { model, _, clickedView, _ ->
            val shortcut = model.shortcut
            val popupMenu = PopupMenu(this, clickedView)
            popupMenu.menu.add(R.string.app_info).setOnMenuItemClickListener {
                Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    data = Uri.parse("package:${shortcut.pkgName}")
                }.run(::startActivity)
                true
            }
            popupMenu.show()
            true
        }

    private val listAdapter = object : EpoxyAdapter() {
        init {
        }

        fun addMore(shortcutList: List<Shortcut>) {
            val list = shortcutList.map {
                ShortcutModel_(it)
                    .onShortcutClickListener(onAppClickListener)!!
                    .onShortcutLongClickListener(onAppLongClickListener)
            }
            addModels(list)
        }

        fun update(shortcutList: List<Shortcut>) {
            val list = shortcutList.map {
                ShortcutModel_(it)
                    .onShortcutClickListener(onAppClickListener)!!
                    .onShortcutLongClickListener(onAppLongClickListener)
            }
            models.clear()
            models.addAll(list)
            notifyDataSetChanged()
        }
    }

    abstract class SingleTapGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            onSingleTap()
            return true
        }

        abstract fun onSingleTap()
    }
}
