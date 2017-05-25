package com.sopt.android.thunder.v3.register;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sopt.android.thunder.R;
import com.sopt.android.thunder.search.Item;
import com.sopt.android.thunder.search.OnFinishSearchListener;
import com.sopt.android.thunder.search.Searcher;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class ShowDetailMap extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener  {

	private static final String LOG_TAG = "SearchDemoActivity";

	private View shadow;
	private ImageView food,drink,play, coffee;
	private LinearLayout buttons;
	private MapView mMapView;
	private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();
	private String juso = null;
	double lat=0, lng=0;
	int before = 0;
	//v2
	SharedPreferences setting;
	private String url = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.show_detail_map);

		mMapView = (MapView)findViewById(R.id.map_view);
		mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
		mMapView.setMapViewEventListener(this);
		mMapView.setPOIItemEventListener(this);
		mMapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());

		// 타이틀 바
		getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.titlebar_detail_map);

		//intent 받기
		Intent intent = getIntent();
		lat = Double.parseDouble(intent.getStringExtra("lat"));
		lng = Double.parseDouble(intent.getStringExtra("lng"));
		juso = intent.getStringExtra("juso");
		url = intent.getStringExtra("url");
		Log.i("확인","lat :"+Double.toString(lat));
		Log.i("확인","lng :"+Double.toString(lng));
		Log.i("확인","juso :"+juso);
		Log.i("url","확인 url :"+url);
		// 추천 버튼들
		initView();
		shadow.bringToFront();
		buttons.bringToFront();
		food.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
				Search("맛집");
				unclick_backgroud_img(before);
				click_backgroud_img(1);
				before = 1;
				setCreateMarker(mMapView, MapPoint.mapPointWithGeoCoord(lat, lng));
			}
		});
		drink.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
				Search("술집");
				unclick_backgroud_img(before);
				click_backgroud_img(2);
				before = 2;
				setCreateMarker(mMapView, MapPoint.mapPointWithGeoCoord(lat, lng));
			}
		});
		play.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
				Search("당구");
				Search("볼링");
				unclick_backgroud_img(before);
				click_backgroud_img(3);
				before = 3;
				setCreateMarker(mMapView, MapPoint.mapPointWithGeoCoord(lat, lng));
			}
		});
		coffee.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
				Search("카페");
				unclick_backgroud_img(before);
				click_backgroud_img(4);
				before = 4;
				setCreateMarker(mMapView, MapPoint.mapPointWithGeoCoord(lat, lng));
			}
		});
}

	private void click_backgroud_img(int btn)
	{
		switch (btn){
			case 1:
				food.setImageDrawable(getResources().getDrawable(R.drawable.map_btn_food_push));
				break;
			case 2 : drink.setImageDrawable(getResources().getDrawable(R.drawable.map_btn_drink_push)); break;
			case 3 : play.setImageDrawable(getResources().getDrawable(R.drawable.map_btn_play_push)); break;
			case 4 : coffee.setImageDrawable(getResources().getDrawable(R.drawable.map_btn_coffee_push)); break;
			default: break;
		}
	}
	private void unclick_backgroud_img(int btn)
	{
		switch (btn){
			case 1 : food.setImageDrawable(getResources().getDrawable(R.drawable.map_btn_food)); break;
			case 2 : drink.setImageDrawable(getResources().getDrawable(R.drawable.map_btn_drink)); break;
			case 3 : play.setImageDrawable(getResources().getDrawable(R.drawable.map_btn_play)); break;
			case 4 : coffee.setImageDrawable(getResources().getDrawable(R.drawable.map_btn_coffee)); break;
			default: break;
		}
	}

	private void initView() {
		food = (ImageView) findViewById(R.id.food);
		drink = (ImageView) findViewById(R.id.drink);
		play = (ImageView) findViewById(R.id.play);
		coffee = (ImageView) findViewById(R.id.coffee);
		buttons = (LinearLayout)findViewById(R.id.buttons);
		shadow = (View)findViewById(R.id.shadow);
	}

	public void Search(String what)
	{
		String query = what;
		hideSoftKeyboard(); // 키보드 숨김
		MapPoint.GeoCoordinate geoCoordinate = mMapView.getMapCenterPoint().getMapPointGeoCoord();
		//double latitude = geoCoordinate.latitude; // 위도
		//double longitude = geoCoordinate.longitude; // 경도
		int radius = 1000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
		int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
		String apikey = MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY;

		Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
		searcher.searchKeyword(getApplicationContext(), query, lat, lng, radius, page, apikey, new OnFinishSearchListener() {
			@Override
			public void onSuccess(List<Item> itemList) {
				//mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
				showResult(itemList); // 검색 결과 보여줌
			}

			@Override
			public void onFail() {
				showToast("API_KEY의 제한 트래픽이 초과되었습니다.");
			}
		});
	}
	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(coffee.getWindowToken(), 0);
	}
	class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {

		private final View mCalloutBalloon;

		public CustomCalloutBalloonAdapter() {
			mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
		}

		@Override
		public View getCalloutBalloon(MapPOIItem poiItem) {
			Log.i("Connection", Integer.toString(poiItem.getTag()));
			if (poiItem == null) return null;
			Item item = mTagItemMap.get(poiItem.getTag());
			TextView textViewDesc = (TextView) mCalloutBalloon.findViewById(R.id.desc);
			TextView textViewTitle = (TextView) mCalloutBalloon.findViewById(R.id.title);
			ImageView imageViewBadge = (ImageView) mCalloutBalloon.findViewById(R.id.badge);

			if (item == null) {//검색아닐때 클릭일때
				if(poiItem.getTag()!=99999)
					return null;
				else {//내가 선택한 임의의 장소인 경우
					Log.i("Map", "getCallBalloon");
					textViewTitle.setText("만날 장소");
						textViewDesc.setText(juso);
					imageViewBadge.setImageDrawable(createDrawableFromUrl(null));
				}
			}
			else {//검색 했을 때
				imageViewBadge.setImageDrawable(createDrawableFromUrl(item.imageUrl));
					textViewTitle.setText(item.title);
					textViewDesc.setText(item.address);
			}
			return mCalloutBalloon;
		}

		@Override
		public View getPressedCalloutBalloon(MapPOIItem poiItem) {
			return null;
		}

	}

	public void setCreateMarker(MapView mapView, MapPoint mapPoint) {
			MapPOIItem customMarker = new MapPOIItem();//선택으로 추가되는 마크

		customMarker.setItemName("Custom Marker");
		customMarker.setTag(99999);
		customMarker.setMapPoint(mapPoint);
		customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
		customMarker.setCustomImageResourceId(R.drawable.custom_map_pin); // 마커 이미지.
		customMarker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
		customMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.

		mapView.addPOIItem(customMarker);

				mMapView.selectPOIItem(customMarker, false);
		}

	public void onMapViewInitialized(MapView mapView) {
		Log.i(LOG_TAG, "MapView had loaded. Now, MapView APIs could be called safely");

			setCreateMarker(mapView, MapPoint.mapPointWithGeoCoord(lat, lng));
	}

	private void showToast(final String text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(ShowDetailMap.this, text, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void showResult(List<Item> itemList) {
		MapPointBounds mapPointBounds = new MapPointBounds();

		for (int i = 0; i < itemList.size(); i++) {
			Item item = itemList.get(i);

			MapPOIItem poiItem = new MapPOIItem();
			poiItem.setItemName(item.title);
			poiItem.setTag(i);
			MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
			poiItem.setMapPoint(mapPoint);
			mapPointBounds.add(mapPoint);
			poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
			poiItem.setCustomImageResourceId(R.drawable.unselect_map_pin);
			poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
			poiItem.setCustomSelectedImageResourceId(R.drawable.select_map_pin);
			poiItem.setCustomImageAutoscale(false);
			poiItem.setCustomImageAnchor(0.5f, 1.0f);

			mMapView.addPOIItem(poiItem);
			mTagItemMap.put(poiItem.getTag(), item);
		}

		mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));

		MapPOIItem[] poiItems = mMapView.getPOIItems();
		if (poiItems.length > 0) {
			mMapView.selectPOIItem(poiItems[0], false);
		}
	}

	private Drawable createDrawableFromUrl(String url) {
		try {
			InputStream is = (InputStream) this.fetch(url);
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}

	//v2 웹뷰 띄우기
	@Override
	public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
		setting = getSharedPreferences("setting", 0);
		Log.i("테스트트","url : "+url);
		if(!url.equals("")) {
			Log.i("테스트트","아이템 존재 웹뷰 띄우기");
			Intent intent = new Intent(getApplicationContext(), WebDialog.class);
			intent.putExtra("URL",url);
			startActivity(intent);
		}
		else{
			Log.i("테스트트", "아이템 존재x 웹뷰띄우기");
		}
	}

	@Override
	@Deprecated
	public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
	}

	@Override
	public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
	}

	@Override
	public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
	}

	@Override
	public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapCenterPoint) {
	}

	@Override
	public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
	}

	@Override
	public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
	}

	@Override
	public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

	}

	@Override
	public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
	}

	@Override
	public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
	}

	@Override
	public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
	}

	@Override
	public void onMapViewZoomLevelChanged(MapView mapView, int zoomLevel) {
	}
}
