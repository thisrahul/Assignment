package com.intern.assignment.repository

import com.intern.assignment.api.RetrofitInstance

class NewsRepository {

    suspend fun getBreakingNews(countryConde:String,pageNumber:Int) =
        RetrofitInstance.api.getBreakingNews(countryConde,pageNumber)

    suspend fun searchNews(searchQuery: String,pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)
}