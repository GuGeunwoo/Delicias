package com.example.delicias.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.delicias.R
import com.example.delicias.data.repository.RestaurantDataRepository
import com.example.delicias.databinding.ActivitySearchBinding
import com.example.delicias.domain.SearchHistory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity(), androidx.appcompat.widget.SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private lateinit var restaurantDataRepository: RestaurantDataRepository
    private lateinit var binding: ActivitySearchBinding
    private lateinit var ivSearchRestaurant: ImageView
    private lateinit var tvSearchTitle: TextView
    private lateinit var ivTitleBackButton: ImageView
    private lateinit var searchHistoryLiveData: LiveData<List<SearchHistory>>
    private var searchResultLiveData = MutableLiveData<List<SearchHistory>>()
    private var isSearchingNowLiveData = MutableLiveData(false)
    private var isAutoSaveMode = MutableLiveData(false)


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)

        val searchTitle: View = binding.searchTitle
        ivSearchRestaurant = (searchTitle as ConstraintLayout).getViewById(R.id.iv_search_restaurant) as ImageView
        tvSearchTitle = searchTitle.getViewById(R.id.tv_map_title) as TextView
        ivTitleBackButton = searchTitle.getViewById(R.id.iv_map_title_back_button) as ImageView

        ivSearchRestaurant.visibility = View.INVISIBLE
        tvSearchTitle.text = "식당 검색"
        ivTitleBackButton.visibility = View.VISIBLE

        ivTitleBackButton.setOnClickListener {
            onBackPressed()
        }

        restaurantDataRepository = RestaurantDataRepository(this)

        binding.tvDeleteAllHistory.setOnClickListener {
            runBlocking {
                restaurantDataRepository.deleteAllSearchHistory()
            }
        }

        binding.tvCancel.setOnClickListener {
            onBackPressed()
        }

        searchHistoryAdapter = SearchHistoryAdapter(restaurantDataRepository, isSearchingNowLiveData, this)

        binding.svSearchRestaurant.setOnQueryTextListener(this)
        binding.svSearchRestaurant.setOnCloseListener(this)

        binding.rvSearchHistory.layoutManager = LinearLayoutManager(this)
        binding.rvSearchHistory.itemAnimator = DefaultItemAnimator()
        binding.rvSearchHistory.adapter = searchHistoryAdapter

        searchHistoryLiveData = Transformations.switchMap(isSearchingNowLiveData){ isSearchingNow ->
            if (!isSearchingNow) {
                binding.tvSearchResult.text = "최근 검색 결과"
                restaurantDataRepository.getAllSearchHistory().asLiveData()
            }
            else {
                binding.tvSearchResult.text = "검색 결과"
                searchResultLiveData
            }
        }

        searchHistoryLiveData.observe(this, {
            searchHistoryAdapter.submitList(it)
        })

        isAutoSaveMode.observe(this, { isAutoSaveModeNow ->
            if (isAutoSaveModeNow) {
                binding.tvAutoSaveMode.text = "자동저장 끄기"
            }
            else {
                binding.tvAutoSaveMode.text = "자동저장 켜기"
                runBlocking {
                    restaurantDataRepository.deleteAllSearchHistory()
                }
            }
        })

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchingModeOn()
            getResults(query)
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            //todo searchingmode
            searchingModeOn()
            getResults(newText)
        }
        return false
    }

    override fun onClose(): Boolean {
        searchingModeOff()
        return false
    }

    private fun getResults(newText: String) {
        val queryText = "%$newText%"

        if (newText.isEmpty())
            return

        runBlocking {
            if (searchResultLiveData.value == null)
                searchResultLiveData.value = emptyList()

            val searchedRestaurants = restaurantDataRepository.searchRestaurant(queryText).first()
            val searchHistoryList = ArrayList<SearchHistory>()
            searchedRestaurants.forEach {
                searchHistoryList.add(SearchHistory(name = it.name, searchDate = getCurrentDate()))
            }

            searchResultLiveData.postValue(searchHistoryList)
        }
    }

    private fun getCurrentDate(): String{
        val sdf = SimpleDateFormat("MM.dd", Locale.KOREA)

        return sdf.format(Date())
    }

    private fun searchingModeOn(){
        isSearchingNowLiveData.value = true
    }

    private fun searchingModeOff(){
        isSearchingNowLiveData.value = false
    }
}