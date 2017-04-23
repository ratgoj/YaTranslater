package com.fern.yatranslater.net.interfaces;

import com.fern.yatranslater.entities.Translate;
import com.fern.yatranslater.entities.TranslateLangs;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Andrey Saprykin on 16.04.2017.
 */

public interface YaNetService {
    @Headers({ "Host: translate.yandex.net", "Accept: */*", "Content-Length: 17", "Content-Type: application/x-www-form-urlencoded" })
    @POST("/api/v1.5/tr.json/getLangs")
    Call<TranslateLangs> getLangs(@Query("ui") String lang, @Query("key") String key);

    @FormUrlEncoded
    @Headers({ "Host: translate.yandex.net", "Accept: */*", "Content-Length: 17", "Content-Type: application/x-www-form-urlencoded" })
    @POST("/api/v1.5/tr.json/translate")
    Call<Translate> translate(@Query("lang") String lang, @Query("key") String key, @Field("text") String text);
}
