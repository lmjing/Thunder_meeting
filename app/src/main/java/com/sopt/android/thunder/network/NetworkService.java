package com.sopt.android.thunder.network;


import com.sopt.android.thunder.detail.model.Comment;
import com.sopt.android.thunder.detail.model.Content;
import com.sopt.android.thunder.detail.model.Content_Mygroup;
import com.sopt.android.thunder.detail.model.Content_group;
import com.sopt.android.thunder.detail.model.Content_member;
import com.sopt.android.thunder.detail.model.Content_notice;
import com.sopt.android.thunder.detail.model.Login;
import com.sopt.android.thunder.detail.model.Participant;
import com.sopt.android.thunder.detail.model.SearchClass;
import com.sopt.android.thunder.detail.model.User;
import com.sopt.android.thunder.detail.model.ViewCheck;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;

public interface NetworkService {

    // TODO: 1. 서버와 네트워킹을 하기 위한 서비스(인터페이스로 구현)
    /**
     * GET 방식과 POST 방식의 사용법을 잘 이해하셔야 합니다.
     * GET("/경로") 경로는 서버 파트에게 물어보세요. (※baseUrl 뒤에 붙는 경로입니다.ex) http://baseUrl/경로)
     * ("/경로/{식별자}) ~~(@Path{"식별자"} String value) 어떤 식별자를 통해 리소스를 구분하여 요청합니다. uri의 정의 기억나시죠? ex) http://baseUrl/경로/value
     * POST 방식은 @Body 에 뭔가를 담아서 보내야하죠?
     **/

    //@GET("/member/test") Call<Content> getContent();
    //
    //@Headers("Content-Type: application/json")
    //
    //@POST("/member/test") Call<Content> newContent(@Body Content content);
    //

    // 회원탈퇴
    //@GET("/contents/register")
    //Call<>

    // 등록된 번개 요청
    @GET("/content/thumbnails/{groupid}")
    Call<List<Content>> getThunderList(@Path("groupid") int id);

    // 등록된 회원정보 요청****
    @GET("/content/memberlist")
    Call<List<User>> getMemberList();

    // 등록된 소속리스트 요청
    @GET("/content/grouplist/{userid}")
    Call<List<Content_group>> getGroupList(@Path("userid") String id);

    // 자신이 소속되어 있는 소속 리스트 요청
    @GET("/content/main_thumbnails/{user_id}")
    Call<List<Content_Mygroup>> getMyGroupList(@Path("user_id") String id);


    //로그인 회원 정보
    @GET("/member/userinfo/{article-id}")
    Call<User> getUser(@Path("article-id") String id);

    @Headers("Content-Type: application/json")

    @POST("/content/register/{article-id}")
    Call<Content> newContent(@Body Content content, @Path("article-id") String id);

    //로그인 유지
    @GET("/member/logincheck/{id}")
    Call<Login> idCheck(@Path("id") String id);

    // 회원가입
    @POST("/member/join")
    Call<User> newUser(@Body User user);
    // 로그인
    @POST("/member/sign_in")
    Call<User> loginUser(@Body User user);

    // 소속 생성
    @POST("/content/groupregist/{id}")
    Call<Content_group> createGroup(@Body Content_group group, @Path("id") String id);


    @GET("/content/delete/{article-id}")
    Call<Content> pleaseThunder1(@Path("article-id") long id);
    @GET("/content/cancel/{article-id}/{users-id}")
    Call<Content> pleaseThunder2(@Path("article-id") long id, @Path("users-id") String userid);
    @GET("/content/join/{article-id}/{users-id}")
    Call<Content> pleaseThunder3(@Path("article-id") long id, @Path("users-id") String userid);

    // 로그아웃
    @GET("/member/sign_out/{id}")
    Call<User> getString(@Path("id") String id);

    // 회원탈퇴
    @GET("/member/delete/{id}")
    Call<User> getDelete(@Path("id") String id);

    // 정보수정
    @POST("member/edit")
    Call<User> editUser(@Body User user );

    @GET("/content/select/{thunder-id}/{user-id}")
    Call<ViewCheck> toServer(@Path("thunder-id") int id, @Path("user-id") String userid);
//////////////////

    // 소속 선택 눌렀을 때
    @POST("/content/groupjoin/{user_id}")
    Call<ViewCheck> choiceGroup(@Body Content_group group, @Path("user_id") String id);

    // 나의 소속들 창에서 선택 눌렀을 때
    @GET("/content/group_select/{userid}/{groupid}")
    Call<ViewCheck> choice_MyGroup(@Path("userid") String id1,@Path("groupid") int id2);

    @GET("/contents/{article-id}")
    Call<Content> getContent(@Path("article-id") long id);

    @GET("/content/list/{article-id}")//해당 게시글의 참가인원을 받는다
    Call<List<Participant>> getParticipant(@Path("article-id") long id);

    //소속 신청할 때
    @GET("/member/sign_out/{id}")
    Call<User> giveUserId(@Path("id") String id);

    //댓글 등록
    @POST("/content/comment/{user-id}/{thunder-id}")
    Call<Comment> newComment(@Body Comment comment, @Path("user-id") String userid, @Path("thunder-id") int id);

    // 등록된 댓글 요청
    @GET("/content/commentlist/{thunder-id}")
    Call<List<Comment>> getCommentList(@Path("thunder-id") int id);

    // 댓글 삭제
    @POST("/content/commentdel/{thunder-id}/{user-id}/{write-time}")
    Call<Comment> deleteComment(@Path("thunder-id") int thunderid, @Path("user-id") String userid, @Path("write-time") String writetime);

    // 댓글 수정
    @POST("/content/comment_update/{thunder-id}/{user-id}/{write-time}")
    Call<Comment> editComment(@Body Comment comment, @Path("thunder-id") int thunderid, @Path("user-id") String userid, @Path("write-time") String writetime);

    ///////////////////////////////////////////////////////////////추가한다 3.6일 부터
    //검색된 소속 리스트 요청
    @POST("/content/search/")
    Call<List<Content_group>> search_getGroupList(@Body SearchClass search);


    ///////////////////////////
    /***************관리자 권한일 때 여기봐 미정아 *******************/
    // 관리자가 회원 탈퇴할 경우
    @POST("/content/memberout")
    Call<Content_member> getMemberName(@Body Content_member member);

    // 등록된 회원정보 요청
    @GET("/content/memberlist/{groupid}")
    Call<List<User>> getMemberList(@Path("groupid") int id);

    // 대기자 회원정보 요청
    @GET("/content/queuelist/{groupid}")
    Call<List<User>> getMemberQueueList(@Path("groupid") int id);

    // 가입 승인 요청 보낼때
    @POST("/content/memberin")
    Call<Content_member> getMemberSign(@Body Content_member member);

    // 관리자 지정 회원 정보 보낼 때
    @POST("/content/authorityup")
    Call<Content_member> getMemberManager(@Body Content_member member);

    // NOTICE 등록*****************
    @POST("")
    Call<Content_notice> createNotice(@Body Content_notice notice);

    // Notice 띄우기*******************
    //@GET("/content/noticelist/{groupid}")
    @GET("")
    Call<List<Content_notice>> getNoticeList(@Path("groupid") int id);

    // 총 회원수
    @GET("/content/membercount/{groupid}")
    Call<Integer> getMemberNum(@Path("groupid") int id);

    // 가입승인 대기자 수
    @GET()
    Call<Integer> getSignWaitNum(@Path("groupid") int id);
    /***************************************************/

    // 소속수정(name, contents) ***-> GroupInfoActivity.class 여서 씀
    @POST("member/edit")
    Call<Content_group> editGroupInfo(@Body Content_group group);

}
