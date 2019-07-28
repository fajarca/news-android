package io.fajarca.news.api;

import io.fajarca.news.model.NewsHeadlineResult;
import io.fajarca.news.model.NewsResult;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsService {
    @GET("everything")
    Observable<NewsResult> getEverything(
            @Query("q") String query,
            @Query("sortBy") String sortBy,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );

    @GET("top-headlines")
    Observable<NewsHeadlineResult> getNewsHeadline(@Query("country") String country,
                                                   @Query("page") int page,
                                                   @Query("pageSize") int pageSize);
}
