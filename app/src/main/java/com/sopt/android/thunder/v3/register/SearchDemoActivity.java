package com.sopt.android.thunder.v3.register;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sopt.android.thunder.R;
import com.sopt.android.thunder.search.Item;
import com.sopt.android.thunder.search.OnFinishSearchListener;
import com.sopt.android.thunder.search.Searcher;
import com.sopt.android.thunder.v3.register.MapApiConst;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPoint.GeoCoordinate;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class SearchDemoActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener ,MapView.CurrentLocationEventListener  {

	private static final String LOG_TAG = "SearchDemoActivity";

	private MapView mMapView;
	private EditText mEditTextQuery;
	private ImageView mButtonSearch;
	private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();
	private MapReverseGeoCoder mReverseGeoCoder = null;
	private String juso = null;
	private boolean click_flag = false;
	SharedPreferences setting;
	SharedPreferences.Editor editor;
	double lat=0, lng=0;
	double currentlat=37.537229, currentlng=127.005515;
	//현재위치
	LocationManager manager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.demo_search);

		mMapView = (MapView)findViewById(R.id.map_view);
		mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
		mMapView.setMapViewEventListener(this);
		mMapView.setPOIItemEventListener(this);
		mMapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());

		mEditTextQuery = (EditText) findViewById(R.id.editTextQuery); // 검색창

		// 타이틀 바
		getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.titlebar_search_map);


		mButtonSearch = (ImageView) findViewById(R.id.buttonSearch); // 검색버튼
		mButtonSearch.setOnClickListener(new OnClickListener() { // 검색버튼 클릭 이벤트 리스너
			@Override
			public void onClick(View v) {
				String query = mEditTextQuery.getText().toString();
				if (query == null || query.length() == 0) {
					showToast("검색어를 입력하세요.");
					return;
				}
				hideSoftKeyboard(); // 키보드 숨김
				GeoCoordinate geoCoordinate = mMapView.getMapCenterPoint().getMapPointGeoCoord();
				double latitude = geoCoordinate.latitude; // 위도
				double longitude = geoCoordinate.longitude; // 경도
				int radius = 10000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
				int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
				String apikey = MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY;

				Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
				searcher.searchKeyword(getApplicationContext(), query, latitude, longitude, radius, page, apikey, new OnFinishSearchListener() {
					@Override
					public void onSuccess(List<Item> itemList) {
						Log.i("search","검색 성공");
						mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
						click_flag = false;
						showResult(itemList); // 검색 결과 보여줌
					}

					@Override
					public void onFail() {
						Log.i("search","검색 실패");
						//showToast("API_KEY의 제한 트래픽이 초과되었습니다.");
					}
				});
			}
		});

		//현재위치
		Button btnShowLocation = (Button) findViewById(R.id.btnShowLocation);

		btnShowLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String context = Context.LOCATION_SERVICE;
				manager = (LocationManager)getSystemService(context);
				if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					alertCheckGPS();
				}
				else {
					getMyLocation();
				}
			}
		});
	}

	private void alertCheckGPS() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage("GPS가 켜져 있지 않습니다.\nGPS를 켜시겠습니까?")
				.setCancelable(false)
				.setPositiveButton("예",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								moveConfigGPS();
							}
						})
				.setNegativeButton("아니요",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	// GPS 설정화면으로 이동
	private void moveConfigGPS() {
		Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(gpsOptionsIntent);
	}

	//나의 위치정보
	private void getMyLocation() {

		if (manager == null) {
			manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}
		// provider 기지국||GPS 를 통해서 받을건지 알려주는 Stirng 변수
		// minTime 최소한 얼마만의 시간이 흐른후 위치정보를 받을건지 시간간격을 설정 설정하는 변수
		// minDistance 얼마만의 거리가 떨어지면 위치정보를 받을건지 설정하는 변수
		// manager.requestLocationUpdates(provider, minTime, minDistance, listener);

		// 10초
		long minTime = 1000000000;

		// 거리는 0으로 설정
		// 그래서 시간과 거리 변수만 보면 움직이지않고 10초뒤에 다시 위치정보를 받는다
		float minDistance = 500;
		MyLocationListener listener = new MyLocationListener();

		try {
			mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, listener);
			Toast.makeText(getApplication(),"현재 위치로 가는중",Toast.LENGTH_SHORT).show();
		}
		catch(Exception e){
			Toast.makeText(getApplication(),"현재 위치 가져오기 실패",Toast.LENGTH_SHORT).show();
		}

		mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
		mMapView.setShowCurrentLocationMarker(true);
		//mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(currentlat, currentlng), 2, true);
		//다시 끄기

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
			}
		}, 10000);
	}

	//현재위치
	class MyLocationListener implements LocationListener {

		// 위치정보는 아래 메서드를 통해서 전달된다.
		@Override
		public void onLocationChanged(Location location) {
			currentlat = location.getLatitude();
			currentlng = location.getLongitude();
			Toast.makeText(getApplication(),"lat : "+currentlat+" lng : "+currentlng,Toast.LENGTH_SHORT).show();
			Log.i("current", "현재위치4 : " + currentlat + "," + currentlng);
		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

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
					textViewDesc.setText(juso);
					imageViewBadge.setImageDrawable(createDrawableFromUrl(null));
				}
			}
			else {//검색 했을 때
				imageViewBadge.setImageDrawable(createDrawableFromUrl(item.imageUrl));
				textViewDesc.setText(item.title);
				juso = item.title;
			}
			return mCalloutBalloon;
		}

		@Override
		public View getPressedCalloutBalloon(MapPOIItem poiItem) {
			return null;
		}

	}

	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditTextQuery.getWindowToken(), 0);
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.i("current","다시 돌아옴");
		//gps키고 돌아왔을때
		String context = Context.LOCATION_SERVICE;
		manager = (LocationManager)getSystemService(context);
		if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					getMyLocation();
					Log.i("current", "다시 돌아와서 현재위치 갱신 성공");
				}
			}, 1000);
		} else {
			Log.i("current","다시 돌아와서 현재위치 갱신 실패");
		}
	}

	@Override
	public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
		mapReverseGeoCoder.toString();
		juso = s;
		Log.i("Map", "주소 찾기");
		mMapView.selectPOIItem(mMapView.findPOIItemByTag(99999), false);
	}
	@Override
	public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
		Log.i("Connection", "주소 찾기 실패");
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

		lat = customMarker.getMapPoint().getMapPointGeoCoord().latitude;
		lng = customMarker.getMapPoint().getMapPointGeoCoord().longitude;
	}

	public void onMapViewInitialized(MapView mapView) {
		//처음 위치 설정
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage("현재위치로 이동하겠습니까?")
				.setCancelable(false)
				.setPositiveButton("예",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String context = Context.LOCATION_SERVICE;
								manager = (LocationManager)getSystemService(context);
								if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
									alertCheckGPS();
								}
								else {
									getMyLocation();
								}
							}
						})
				.setNegativeButton("아니요",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();

		Log.i(LOG_TAG, "MapView had loaded. Now, MapView APIs could be called safely");

		mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(currentlat, currentlng), 2, true);

	}

	private void showToast(final String text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(SearchDemoActivity.this, text, Toast.LENGTH_SHORT).show();
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

	//v2 웹뷰 띄우기 위해 url주소 저장
	@Override
	public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
		Item item = mTagItemMap.get(mapPOIItem.getTag());

		lat = mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude;
		lng = mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude;
		setting = getSharedPreferences("setting", 0);
		editor = setting.edit();
		editor.putString("address", juso);
		editor.putString("lat", String.valueOf(lat));
		editor.putString("lng", String.valueOf(lng));
		if(item != null) {
			String url = item.placeUrl;
			editor.putString("url",url);
		}
		editor.commit();
		finish();
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
		mReverseGeoCoder = new MapReverseGeoCoder(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY, mapPoint, SearchDemoActivity.this, SearchDemoActivity.this);
		mReverseGeoCoder.startFindingAddress();
//		Log.i("Map",findAddressForMapPoint(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY, mapPoint));

		//MapPOIItem customMarker = new MapPOIItem();//선택으로 추가되는 마크

		if (click_flag == true)
			mMapView.removePOIItem(mMapView.findPOIItemByTag(99999));
		setCreateMarker(mapView, mapPoint);
		click_flag = true;
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

	//현재위치
	@Override
	public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
		MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
		currentlat = mapPointGeo.latitude; currentlng = mapPointGeo.longitude;
	}

	@Override
	public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

	}

	@Override
	public void onCurrentLocationUpdateFailed(MapView mapView) {

	}

	@Override
	public void onCurrentLocationUpdateCancelled(MapView mapView) {

	}
}
