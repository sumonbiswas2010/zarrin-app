package com.example.zarrin_app;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ApiResponseDao {
    @Insert
    void insert(ApiResponseEntity apiResponseEntity);

    @Query("SELECT * FROM api_responses")
    List<ApiResponseEntity> getAllApiResponses();

    @Query("DELETE FROM api_responses")
    void deleteAllApiResponses();


}
