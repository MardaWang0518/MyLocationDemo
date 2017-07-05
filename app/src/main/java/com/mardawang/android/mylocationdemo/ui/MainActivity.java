package com.mardawang.android.mylocationdemo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.mardawang.android.mylocationdemo.R;
import com.mardawang.android.mylocationdemo.util.CalendarUtil;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    MapView mMapView = null;
    public LocationClient mLocationClient = null;
    private BaiduMap mBaiduMap;
    private LocationClientOption mOption;
    boolean isFirstLoc = true;// 是否首次定位
    private ListView lv_view;
    private TextView tv_curlocal;
    private TextView tv_curdate;
    private TextView tv_curtime;
    private TextView tv_location_update;
    private RelativeLayout rl_signed;
    private RelativeLayout rl_update;
    private LocationAdapter mAdapper;
    private RelativeLayout rl_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.bmapView);
        lv_view = (ListView) findViewById(R.id.lv_view);
        rl_title = (RelativeLayout) findViewById(R.id.rl_title);
        rl_signed = (RelativeLayout) findViewById(R.id.rl_signed);
        rl_update = (RelativeLayout) findViewById(R.id.rl_update);
        tv_curlocal = (TextView) findViewById(R.id.tv_curlocal);
        tv_curdate = (TextView) findViewById(R.id.tv_curdate);
        tv_curtime = (TextView) findViewById(R.id.tv_curtime);
        tv_location_update = (TextView) findViewById(R.id.tv_location_update);

        tv_location_update.setOnClickListener(this);
        mMapView.setOnClickListener(this);
        rl_signed.setOnClickListener(this);
        initMap();
    }

    private void initMap() {
        infoUplate(false);
        mBaiduMap = mMapView.getMap();

        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mBDLocationListener);
        mLocationClient.start();
        mLocationClient.setLocOption(getDefaultLocationClientOption());

        mMapView.refreshDrawableState();
    }

    public LocationClientOption getDefaultLocationClientOption() {

        mOption = new LocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        //mOption.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        mOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
        mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集

        return mOption;
    }

    private BDLocationListener mBDLocationListener = new BDLocationListener() {

        private ArrayList<String> list;

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder().
                    accuracy(location.getRadius()).direction(100).latitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());

                //地图标注
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.mine_locate);
                OverlayOptions options = new MarkerOptions().position(ll).icon(bitmapDescriptor);
                mBaiduMap.addOverlay(options);
                  /*
                   * 标绘圆
                   * */
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(ll);//设置圆心坐标
                circleOptions.fillColor(0Xaafaa355);//圆的填充颜色
                circleOptions.fillColor(0Xaafaa355);//圆的填充颜色
                circleOptions.radius(80);//设置半径
                circleOptions.stroke(new Stroke(2, 0xAA00FF00));//设置边框
                mBaiduMap.addOverlay(circleOptions);

                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 17);   //设置地图中心点以及缩放级别
                //MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }


            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nerror code : ");
                sb.append(location.getLocType());
                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());
                sb.append("\nradius : ");
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");
                sb.append(location.getCityCode());
                sb.append("\ncity : ");
                sb.append(location.getCity());
                sb.append("\nDistrict : ");
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");
                sb.append(location.getStreet());
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\nDescribe: ");
                sb.append(location.getLocationDescribe());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());
                sb.append("\nPoi: ");

                tv_curdate.setText(CalendarUtil.getBirth(new Date()));
                tv_curtime.setText(CalendarUtil.getHourAndMinutes(new Date()));
                list = new ArrayList();
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    list.clear();
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                        list.add(poi.getName());
                        if (0 == i) {   //当前位置
                            tv_curlocal.setText(poi.getName());
                        }
                    }
                }

                mAdapper = new LocationAdapter(MainActivity.this, list);
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 单位：米
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    sb.append("\noperationers : ");
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                inflateMsg(sb.toString());
            }
        }

    };

    private void inflateMsg(String s) {
        Log.e("msg", s);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mMapView = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bmapView:
            case R.id.tv_location_update:
                //地点微调
                infoUplate(true);
                break;
            case R.id.rl_signed:
                Toast.makeText(MainActivity.this, "签到", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void infoUplate(boolean flag) {
        if (flag) {//微调页面
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 900);
            mMapView.setLayoutParams(param);

            rl_signed.setVisibility(View.GONE);
            rl_update.setVisibility(View.GONE);
            tv_curdate.setVisibility(View.GONE);
            rl_title.setVisibility(View.VISIBLE);
            lv_view.setVisibility(View.VISIBLE);
            lv_view.setAdapter(mAdapper);
        } else {//默认页面
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500);
            mMapView.setLayoutParams(param);

            rl_signed.setVisibility(View.VISIBLE);
            rl_update.setVisibility(View.VISIBLE);
            tv_curdate.setVisibility(View.VISIBLE);
            rl_title.setVisibility(View.GONE);
            lv_view.setVisibility(View.GONE);
        }
    }
}

