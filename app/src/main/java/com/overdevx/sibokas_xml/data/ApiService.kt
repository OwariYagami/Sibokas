package com.overdevx.sibokas_xml.data


import com.overdevx.sibokas_xml.data.getBuildingList.BuildingResponse
import com.overdevx.sibokas_xml.data.getCheckin.CheckInResponse
import com.overdevx.sibokas_xml.data.getLogin.UserResponse
import com.overdevx.sibokas_xml.data.getClassroomByBuilding.BuildingWithClassroomsResponse
import com.overdevx.sibokas_xml.data.getDetailClassroom.ClassroomDetails
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("login/student")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<UserResponse>

    @GET("buildings")
    fun getAllBuildings(@Header("Authorization") token: String): Call<BuildingResponse>

    @GET("building-with-classrooms/{buildingId}")
    fun getClassroomByBuilding(@Header("Authorization") token: String,
                               @Path("buildingId") buildingId: Int
    ):Call<BuildingWithClassroomsResponse>

    @GET("classroom-with-details/{classroomId}")
    fun getClassroomDetailsById(@Header("Authorization") token: String,
                                @Path("classroomId") classroomId: Int
    ):Call<ClassroomDetails>

    @FormUrlEncoded
    @POST("check-in")
    fun checkIn(
        @Header("Authorization") token: String,
        @Field("classroom_id") classroomId : Int
    ): Call<CheckInResponse>
}