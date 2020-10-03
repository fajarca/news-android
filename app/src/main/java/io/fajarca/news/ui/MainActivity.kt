package io.fajarca.news.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import io.fajarca.news.R
import io.fajarca.news.common.UiState
import io.fajarca.news.common.gone
import io.fajarca.news.common.visible
import io.fajarca.news.databinding.ActivityMainBinding
import io.fajarca.news.util.plusAssign
import io.fajarca.news.viewmodel.NewsViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var newsAdapter : NewsPagedListAdapter
    private lateinit var headlineAdapter: HeadlinePagerAdapter
    private var compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: NewsViewModel
    private lateinit var binding : ActivityMainBinding
    private lateinit var viewPager : ViewPager
    private lateinit var searchView : SearchView
    private var isInitialFetch = true

    companion object {
        const val FIRST_PAGE = 1
        const val NUM_OF_HEADLINE_NEWS_TO_SHOW = 5
        const val SWIPE_INTERVAL = 5000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)


        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NewsViewModel::class.java)

        initDataBinding()
        initPagedListRecyclerView()
        initBanner()

        searchNewsWithKeyword("android")
        fetchHeadlineBanner()

        viewModel.news.observe(this, Observer {
            val itemSize = it.size
            newsAdapter.submitList(it)

            toggleView(itemSize)
        })


        viewModel.networkState.observe(this, Observer {
            it?.let {
                when(it){
                    is UiState.Loading -> {
                        showProgressBar()
                    }
                    is UiState.NoData -> {
                        onEmptyNewsData(R.string.search_not_found)
                        hideProgressBar()
                    }
                    is UiState.Error -> {
                        Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                        onErrorFetchNewsData(it.errorMessage)
                        hideProgressBar()
                    }
                    is UiState.Success -> {
                        hideProgressBar()
                    }

                    is UiState.NoInternetConnection -> {
                        hideProgressBar()
                        Snackbar.make(binding.root, getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show()
                    }
                }
            }

        })


        viewModel.headlines.observe(this, androidx.lifecycle.Observer {
            it?.let {
                headlineAdapter.refreshHeadlines(it)
                headlineAdapter.notifyDataSetChanged()
                initBannerSwipeScheduler()
            }
        })

    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.view = this
        binding.vm = viewModel
    }

    private fun initPagedListRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        newsAdapter = NewsPagedListAdapter()
        binding.recyclerView.adapter = newsAdapter
        binding.recyclerView.setHasFixedSize(true)
    }

    fun onButtonTryAgainPressed() {
        searchNewsWithKeyword("android")
        fetchHeadlineBanner()
        hideEmptyResultLayout()
    }

    private fun initBanner() {
        viewPager = binding.contentHeadlineBanner.viewpager
        val tabLayout = binding.contentHeadlineBanner.tabLayout

        headlineAdapter = HeadlinePagerAdapter(ArrayList(), this)
        viewPager.adapter = headlineAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun searchNewsWithKeyword(keyword: String) {
        viewModel.searchNews(keyword)
    }

    private fun fetchHeadlineBanner() {
        //Set the default country code
        var countryCode = "id"
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        if (telephonyManager.networkCountryIso != null) {
            countryCode = telephonyManager.networkCountryIso
        }

        viewModel.getHeadline(countryCode, FIRST_PAGE, NUM_OF_HEADLINE_NEWS_TO_SHOW)
    }


    private fun initBannerSwipeScheduler() {
        compositeDisposable += Observable.interval(SWIPE_INTERVAL, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Long>(){
                override fun onComplete() {

                }

                override fun onNext(t: Long) {
                    val currentViewpagerPosition = viewPager.currentItem
                    val headlineBannerSize = headlineAdapter.count


                    if (currentViewpagerPosition < headlineBannerSize - 1) {
                        viewPager.setCurrentItem(currentViewpagerPosition + 1, true)
                    } else {
                        viewPager.setCurrentItem(0, true)
                    }
                }

                override fun onError(e: Throwable) {

                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu.findItem(R.id.action_search)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = searchItem.actionView as SearchView

        registerTextChangeListener(searchView)
            .debounce(300, TimeUnit.MILLISECONDS)
            .filter { text ->
                !text.isEmpty()
            } //Filter unwanted string (empty string) to avoid unnecessary network call
            .distinctUntilChanged() //To avoid duplicate network call.  Suppress duplicate consecutive items emitted by the source Observable.
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<String> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(text: String) {
                    Log.v("Result", "TextChange : $text")
                    searchNewsWithKeyword(text)
                }

                override fun onError(e: Throwable) {

                }

                override fun onComplete() {
                    Log.v("Result", "Submit")
                }
            })


        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        return true
    }


    private fun registerTextChangeListener(searchView: SearchView): Observable<String> {

        val subject = PublishSubject.create<String>()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                subject.onComplete()
                return true
            }

            override fun onQueryTextChange(text: String): Boolean {
                subject.onNext(text)
                return true
            }
        })

        return subject
    }

    private fun onEmptyNewsData(onEmptyMessage: Int) {
        binding.recyclerView.gone()

        showEmptyResultLayout(onEmptyMessage)

        binding.executePendingBindings()
    }

    private fun onErrorFetchNewsData(errorMessage: String) {
        binding.recyclerView.gone()

        binding.contentResult.layoutError.visible()
        binding.contentResult.tvError.text = errorMessage

        binding.executePendingBindings()
    }


    private fun showProgressBar() {
        binding.progressBar.visible()
    }
    private fun hideProgressBar() {
        binding.progressBar.gone()
    }

    private fun showEmptyResultLayout(messageResId : Int) {
        binding.contentResult.layoutError.visible()
        binding.contentResult.tvError.text = getString(messageResId)
        hideProgressBar()
        binding.executePendingBindings()
    }

    private fun hideEmptyResultLayout() {
        binding.contentResult.layoutError.gone()
        hideProgressBar()
        binding.executePendingBindings()
    }

    private fun showRecyclerView() {
        binding.recyclerView.visible()
    }


    private fun toggleView(itemSize: Int) {
        if (isInitialFetch && itemSize == 0) {
            hideEmptyResultLayout()
            isInitialFetch = false
        } else if (!isInitialFetch && itemSize == 0) {
            showEmptyResultLayout(R.string.search_not_found)
        } else {
            hideEmptyResultLayout()
            showRecyclerView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}

