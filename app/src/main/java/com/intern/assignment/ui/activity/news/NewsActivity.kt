package com.intern.assignment.ui.activity.news

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intern.assignment.R
import com.intern.assignment.adapters.NewsAdapter
import com.intern.assignment.databinding.ActivityNewsBinding
import com.intern.assignment.models.Article
import com.intern.assignment.repository.NewsRepository
import com.intern.assignment.util.Constants.Companion.QUERY_PAGE_SIZE
import com.intern.assignment.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.intern.assignment.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding
    private lateinit var viewModel: NewsViewModel

    private lateinit var newsAdapter: NewsAdapter

    val TAG = "NEWSACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpSearch()

        val newsRepository = NewsRepository()
        val viewModelProviderFactory = NewsViewModelProviderFactory(application,newsRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(NewsViewModel::class.java)

        viewModel.breakingNews.observe(this@NewsActivity, Observer { response ->
            when(response){
                is Resource.Success ->{
                    hideProgressBar()
                    response.data?.let {
                        setUpRecyclerView(it.articles,false)
                        val totalPages = it.totalResults/ QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if (isLastPage){
                            binding.rvNews.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error ->{
                   hideProgressBar()
                    response.message?.let {
                        Log.e(TAG,"An error occured: $it")
                        Toast.makeText(this@NewsActivity,"An error occured: $it",Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading ->{
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
       binding.progressBar.visibility = View.GONE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val breakingNewsScrollListner = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBegining = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBegining && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate){
                viewModel.getBreakingNews("in")
                isScrolling = false
            }else{
                ////
            }
        }
    }



    private fun setUpRecyclerView(mList:List<Article>,isSearch:Boolean){

        binding.rvNews.layoutManager = LinearLayoutManager(this@NewsActivity)
        newsAdapter = NewsAdapter(mList)
        if (isSearch){
            binding.rvNews.apply {
                adapter = newsAdapter
            }
        }else{
            binding.rvNews.apply {
                adapter = newsAdapter
                addOnScrollListener(this@NewsActivity.breakingNewsScrollListner)
            }
        }
    }

    private fun setUpSearch() {

        var job: Job? = null
      binding.etSearch.addTextChangedListener{editable ->
          job?.cancel()
          job = MainScope().launch {
              delay(SEARCH_NEWS_TIME_DELAY)
              editable?.let {
                  if (editable.toString().isNotEmpty()){
                      viewModel.searchNews(editable.toString())
                  }else{
                      viewModel.getBreakingNews("in")
                  }
              }

          }

          viewModel.searchNews.observe(this@NewsActivity, Observer { response ->
              when(response){
                  is Resource.Success ->{
                      hideProgressBar()
                      response.data?.let {
                          setUpRecyclerView(it.articles,true)
                      }
                  }
                  is Resource.Error ->{
                      hideProgressBar()
                      response.message?.let {
                          Log.e(TAG,"An error occured: $it")

                          Toast.makeText(this@NewsActivity,"An error occured: $it",Toast.LENGTH_LONG).show()
                      }
                  }
                  is Resource.Loading ->{
                      showProgressBar()
                  }
              }
          })

      }
    }
}