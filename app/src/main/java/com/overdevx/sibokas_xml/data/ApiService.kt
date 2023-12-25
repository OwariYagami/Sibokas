package com.overdevx.sibokas_xml.data


import com.overdevx.sibokas_xml.data.getBuildingList.BuildingResponse
import com.overdevx.sibokas_xml.data.getCheckin.CheckInResponse
import com.overdevx.sibokas_xml.data.getCheckout.CheckOutResponse
import com.overdevx.sibokas_xml.data.getCheckout.CheckoutRequest
import com.overdevx.sibokas_xml.data.getLogin.UserResponse
import com.overdevx.sibokas_xml.data.getClassroomByBuilding.BuildingWithClassroomsResponse
import com.overdevx.sibokas_xml.data.getDetailClassroom.ClassroomDetails
import com.overdevx.sibokas_xml.data.getHistory.HistoryResponse
import com.overdevx.sibokas_xml.data.getReport.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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


    @POST("check-out/{idBooking}")
    fun checkOut(
        @Header("Authorization") token : String,
        @Path("idBooking") idBooking: Int,
        @Body request: CheckoutRequest
    ): Call<CheckOutResponse>

    @GET("booking-history-student")
    fun getHistory(@Header("Authorization") token: String): Call<HistoryResponse>

    @Multipart
    @POST("report")
    fun report(
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody,
        @Part("description") desc: RequestBody,
        @Part("classroom_id") classId: RequestBody,
        @Part photo: MultipartBody.Part
    ): Call<UploadResponse>
}