package com.stockbit.mini.stocklib.modules.stocklist

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.stockbit.mini.corelib.base.StockbitActivity
import com.stockbit.mini.corelib.base.StockbitViewModelFragment
import com.stockbit.mini.stocklib.BuildConfig
import com.stockbit.mini.stocklib.R
import com.stockbit.mini.stocklib.databinding.StockListFragmentBinding
import com.stockbit.mini.stocklib.repositories.remote.response.USD
import com.stockbit.mini.userlib.repositories.cache.storage.entity.User
import kotlinx.android.synthetic.main.stock_list_fragment.*
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import timber.log.Timber
import java.net.URI
import javax.net.ssl.SSLSocketFactory


class StockListFragment : StockbitViewModelFragment<StockListFragmentBinding, StockListViewModel>(
    layoutResId = R.layout.stock_list_fragment,
    StockListViewModel::class
) {
    var doubleBackToExitPressedOnce = false
    private lateinit var webSocketClient: WebSocketClient
    var mAdapter: StockAdapter? = null
    var page = 1

    override fun actionBarTitle(): String = getString(R.string.stock_list_title)

    override fun initBinding(binding: StockListFragmentBinding, viewModel: StockListViewModel) {
        super.initBinding(binding, viewModel)
        binding.viewModel = viewModel.also {
            it.loadingIndicator.observe(this, ::loadingIndicator)
            it.alertMessage.observe(this, ::showAlert)
            it.user.observe(this, ::isSignIn)
            it.stocks.observe(this, { stocks ->
                mAdapter?.stocks?.addAll(stocks)
                mAdapter?.notifyDataSetChanged()
                swipe_container.isRefreshing = false
            })
        }
    }

    private fun isSignIn(user: User?) {
        takeIf { user == null }?.run {
            view?.let { v ->
                val request = NavDeepLinkRequest.Builder
                    .fromUri("android-app://mini.stockbit.com/signin".toUri())
                    .build()
                (activity as? StockbitActivity)?.navHostFragment()?.findNavController()?.navigate(request)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_stock_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.action_sign_out) {
            AlertDialog.Builder(context)
                .setMessage(getString(R.string.stock_list_sign_out_sure))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.stock_list_sign_out_yes) { _, _ ->
                    viewModel.signOut()
                }
                .setNegativeButton(R.string.stock_list_sign_out_no, null).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            activity?.finish()
            return
        }

        doubleBackToExitPressedOnce = true
        Toast.makeText(context, getString(com.stockbit.mini.userlib.R.string.sign_in_tap_to_minimize), Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    private fun createWebSocketClient(coin: String) {
        webSocketClient = object : WebSocketClient(URI(BuildConfig.WEBSOCKET_URL)) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Timber.d("onOpen")
                subscribe(coin)
            }
            override fun onMessage(message: String?) {
                Timber.d("onMessage: $message")
                setUpPriceText(message)
            }
            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Timber.d("onClose")
                unsubscribe(coin)
            }
            override fun onError(ex: Exception?) {
                Timber.d("onError: ${ex?.message}")
            }
        }
        val socketFactory: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
        webSocketClient.setSocketFactory(socketFactory)
        webSocketClient.connect()
    }

    private fun subscribe(coin: String) {
        webSocketClient.send(
            "{\n" +
                    "    \"action\": \"SubAdd\",\n" +
                    "    \"subs\": [\"5~CCCAGG~$coin~USD\"]\n" +
                    "}"
        )
    }

    private fun unsubscribe(coin: String) {
        webSocketClient.send(
            "{\n" +
                    "    \"action\": \"SubRemove\",\n" +
                    "    \"subs\": [\"5~CCCAGG~$coin~USD\"]\n" +
                    "}"
        )
    }

    private fun setUpPriceText(message: String?) {
        message?.let {
            val moshi = Moshi.Builder().build()
            val adapter: JsonAdapter<USD> = moshi.adapter(USD::class.java)
            val usd = adapter.fromJson(message)
            activity?.runOnUiThread {
                rvStock.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun initViews() {
        super.initViews()
        swipe_container.setOnRefreshListener {
            viewModel.stocks.value = mutableListOf()
            viewModel.getStocks(1)
        }
        mAdapter = StockAdapter()
        val mLayoutManager = LinearLayoutManager(activity)
        rvStock.layoutManager = mLayoutManager
        rvStock.adapter = mAdapter
        rvStock.addOnScrollListener (object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (! recyclerView.canScrollVertically(1)){
                    page++
                    viewModel.getStocks(page)
                }
            }
        })
        viewModel.getStocks(page)
    }
}