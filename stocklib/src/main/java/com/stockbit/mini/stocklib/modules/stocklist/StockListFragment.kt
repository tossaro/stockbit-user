package com.stockbit.mini.stocklib.modules.stocklist

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
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
import com.google.gson.Gson
import com.stockbit.mini.corelib.base.StockbitActivity
import com.stockbit.mini.corelib.base.StockbitViewModelFragment
import com.stockbit.mini.stocklib.BuildConfig
import com.stockbit.mini.stocklib.R
import com.stockbit.mini.stocklib.databinding.StockListFragmentBinding
import com.stockbit.mini.stocklib.repositories.remote.response.StockSocket
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
                stocks?.let { ss ->
                    mAdapter?.stocks?.addAll(ss)
                    mAdapter?.notifyDataSetChanged()
                    if (page == 1) rvStock.smoothScrollToPosition(0)
                    swipe_container.isRefreshing = false
                    ss.forEach{ s -> if (webSocketClient.isOpen) subscribe(s.name) }
                }
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
            webSocketClient.close()
            activity?.finish()
            return
        }

        doubleBackToExitPressedOnce = true
        Toast.makeText(context, getString(com.stockbit.mini.userlib.R.string.sign_in_tap_to_minimize), Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
            .isConnected
    }

    fun getStocks(p: Int) {
        page = p
        if (viewModel.isFromLocal != !isNetworkAvailable()) {
            page = 1
            mAdapter?.stocks?.clear()
        }
        viewModel.isFromLocal = !isNetworkAvailable()
        if (webSocketClient.isClosed) webSocketClient.reconnect()
        viewModel.getStocks(page)
    }

    override fun initViews() {
        super.initViews()
        swipe_container.setOnRefreshListener {
            viewModel.stocks.value = mutableListOf()
            getStocks(1)
        }
        mAdapter = StockAdapter()
        val mLayoutManager = LinearLayoutManager(activity)
        rvStock.layoutManager = mLayoutManager
        rvStock.adapter = mAdapter
        rvStock.addOnScrollListener (object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (! recyclerView.canScrollVertically(1)){
                    page++
                    getStocks(page)
                }
            }
        })
        createWebSocketClient()
    }

    private fun createWebSocketClient() {
        webSocketClient = object : WebSocketClient(URI(BuildConfig.WEBSOCKET_URL)) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Timber.d("onOpen")
                viewModel.getStocksLocal(1)
                viewModel.getStocksRemote(1)
            }
            override fun onMessage(message: String?) {
                Timber.d("onMessage: $message")
                activity?.runOnUiThread {
                    val usd = Gson().fromJson(message, StockSocket::class.java)
                    if (usd?.TOPTIERFULLVOLUME != null && viewModel.loadingIndicator.value == false) {
                        mAdapter?.stocks?.let { ss ->
                            ss.forEach { s ->
                                if (s.name == usd.SYMBOL) {
                                    s.price = String.format("%.5f", usd.TOPTIERFULLVOLUME.toDouble())
                                    mAdapter?.notifyItemChanged(ss.indexOf(s))
                                }
                            }
                        }
                    }
                }
            }
            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Timber.d("onClose")
                mAdapter?.stocks?.let { ss ->
                    ss.forEach{ s -> unsubscribe(s.name) }
                }
            }
            override fun onError(ex: Exception?) {
                Timber.d("onError: ${ex?.message}")
                viewModel.getStocksLocal(1)
                viewModel.getStocksRemote(1)
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
                    "    \"subs\": [\"21~$coin\"]\n" +
                    "}"
        )
    }

    private fun unsubscribe(coin: String) {
        webSocketClient.send(
            "{\n" +
                    "    \"action\": \"SubRemove\",\n" +
                    "    \"subs\": [\"2~Coinbase~$coin~USD\"]\n" +
                    "}"
        )
    }
}