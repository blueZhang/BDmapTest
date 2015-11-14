package com.bluezhang.test;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.baidu.location.*;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    MapView mMapView;
    BaiduMap mBaiduMap;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(bdLocation.getTime());
            sb.append("\nerror code : ");
            sb.append(bdLocation.getLocType());
            sb.append("\nlatitude : ");
            sb.append(bdLocation.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(bdLocation.getLongitude());
            sb.append("\nradius : ");
            sb.append(bdLocation.getRadius());
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(bdLocation.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(bdLocation.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(bdLocation.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(bdLocation.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(bdLocation.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
                sb.append("\naddr : ");
                sb.append(bdLocation.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(bdLocation.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (bdLocation.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(bdLocation.getLocationDescribe());// 位置语义化信息
            List<Poi> list = bdLocation.getPoiList();// POI数据

            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("BaiduLocationApiDem", sb.toString());
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(
                    new MapStatus.Builder().target(
                            new LatLng(
                                    bdLocation.getLatitude(),
                                    bdLocation.getLatitude()
                            )
                    ).
                            zoom(15).
                            rotate(45).
                            overlook(45).build()
            ));
        }
    };
    private BDNotifyListener mNotifyer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数/在使用SDK各组件之前初始化context信息，传入ApplicationContext
        mLocationClient.start();
        //位置提醒相关代码
        mNotifyer =new NotifyListers();
        mNotifyer.SetNotifyLocation(42.03249652949337, 113.3129895882556, 3000, "gps");//4个参数代表要位置提醒的点的坐标，具体含义依次为：纬度，经度，距离范围，坐标系类型(gcj02,gps,bd09,bd09ll)
        mLocationClient.registerNotify(mNotifyer);
        //注册位置提醒监听事件后，可以通过SetNotifyLocation 来修改位置提醒设置，修改后立刻生效。
        //BDNotifyListner实现

        //取消位置提醒
        //mLocationClient.removeNotifyEvent(mNotifyer);

        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setTrafficEnabled(true);

        //定义Maker坐标点
        LatLng point = new LatLng(40.043126, 116.37706);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.ic_launcher);
        //构建MarkerOption，用于在地图上添加Marker
//        OverlayOptions option = new MarkerOptions()
//                .position(point)
//                .icon(bitmap);
//        //在地图上添加Marker，并显示
//        mBaiduMap.addOverlay(option);
        OverlayOptions options = new MarkerOptions()//标记选项
                .position(point)  //设置marker的位置
                .icon(bitmap)  //设置marker图标
                .zIndex(9)  //设置marker所在层级
                .draggable(true);  //设置手势拖拽
        ((MarkerOptions) options).animateType(MarkerOptions.MarkerAnimateType.drop);//添加动画效果从天上掉下来
        ((MarkerOptions) options).title("天丰利商城");//设置标题
        ((MarkerOptions) options).draggable(true);
        ((MarkerOptions) options).alpha(0.5f);//设置透明度
        mBaiduMap.addOverlay(options);
        //------------------------------------------------------------------------
        //将marker添加到地图上
        //定义多边形的五个顶点
        LatLng pt1 = new LatLng(39.93923, 116.357428);
        LatLng pt2 = new LatLng(39.91923, 116.327428);
        LatLng pt3 = new LatLng(39.89923, 116.347428);
        LatLng pt4 = new LatLng(39.89923, 116.367428);
        LatLng pt5 = new LatLng(39.91923, 116.387428);
        List<LatLng> pts = new ArrayList<LatLng>();
        pts.add(pt1);
        pts.add(pt2);
        pts.add(pt3);
        pts.add(pt4);
        pts.add(pt5);
        /**
         * 构建用户绘制多边形的Option对象
         */
        OverlayOptions polygonOption = new PolygonOptions()
                .points(pts)
                .stroke(new Stroke(5, 0xAA00FF00))
                .fillColor(0xAAFFFF00);
        //在地图上添加多边形Option，用于显示
//        mBaiduMap.addOverlay(polygonOption);

        Marker marker = (Marker) (mBaiduMap.addOverlay(options));
        //调用BaiduMap对象的setOnMarkerDragListener方法设置marker拖拽的监听
        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            public void onMarkerDrag(Marker marker) {
                //拖拽中
            }

            public void onMarkerDragEnd(Marker marker) {
                //拖拽结束
            }

            public void onMarkerDragStart(Marker marker) {
                //开始拖拽
            }
        });

        //--------------------------------------------------------------
        //构造纹理资源
        BitmapDescriptor custom1 = BitmapDescriptorFactory
                .fromResource(R.drawable.line_shape);
        BitmapDescriptor custom2 = BitmapDescriptorFactory
                .fromResource(R.drawable.line_shape);
        BitmapDescriptor custom3 = BitmapDescriptorFactory
                .fromResource(R.drawable.line_shape);
// 定义点
        LatLng ll1 = new LatLng(39.93923, 116.357428);
        LatLng ll2 = new LatLng(39.91923, 116.327428);
        LatLng ll3 = new LatLng(39.89923, 116.347428);
        LatLng ll4 = new LatLng(39.89923, 116.367428);
        LatLng ll5 = new LatLng(39.91923, 116.387428);

//构造纹理队列
        List<BitmapDescriptor> customList = new ArrayList<BitmapDescriptor>();
        customList.add(custom1);
        customList.add(custom2);
        customList.add(custom3);

        List<LatLng> points = new ArrayList<LatLng>();
        List<Integer> index = new ArrayList<Integer>();
        points.add(ll1);//点元素
        index.add(0);//设置该点的纹理索引
        points.add(ll2);//点元素
        index.add(0);//设置该点的纹理索引
        points.add(ll3);//点元素
        index.add(1);//设置该点的纹理索引
        points.add(ll4);//点元素
        index.add(2);//设置该点的纹理索引
        points.add(ll5);//点元素
//构造对象
        OverlayOptions ooPolyline = new PolylineOptions().width(15).color(0xAAFF0000).points(points).customTextureList(customList).textureIndex(index);
//添加到地图
//        mBaiduMap.addOverlay(ooPolyline);


        //-----------------------------------------------------------
        // 构造折线点坐标
        List<LatLng> pointss = new ArrayList<LatLng>();
        pointss.add(new LatLng(39.965, 116.404));
        pointss.add(new LatLng(39.925, 116.454));
        pointss.add(new LatLng(39.955, 116.494));
        pointss.add(new LatLng(39.905, 116.554));
        pointss.add(new LatLng(39.965, 116.604));

//构建分段颜色索引数组
        List<Integer> colors = new ArrayList<>();
        colors.add(Integer.valueOf(Color.BLUE));
        colors.add(Integer.valueOf(Color.RED));
        colors.add(Integer.valueOf(Color.YELLOW));
        colors.add(Integer.valueOf(Color.GREEN));

        OverlayOptions ooPolylinee = new PolylineOptions().width(10)
                .colorsValues(colors).points(points);
        //添加在地图中
        Polyline mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolylinee);

        //------------------------------------------------------------
        //在地图上添加文字：
        //定义文字所显示的坐标点
        LatLng llText = new LatLng(39.86923, 116.397428);
        //构建文字Option对象，用于在地图上添加文字
        OverlayOptions textOption = new TextOptions()
                .bgColor(0xAAFFFF00)
                .fontSize(24)
                .fontColor(0xFFFF00FF)
                .text("张帆")
                .rotate(30)
                .position(llText);
        //在地图上添加该文字对象并显示
        mBaiduMap.addOverlay(textOption);

        //--------------------------------------------
        //添加弹出的Window窗口
        //创建InfoWindow展示的view
        Button button = new Button(getApplicationContext());
        button.setBackgroundResource(R.mipmap.ic_launcher);
        button.setText("世界你好");
        //定义用于显示该InfoWindow的坐标点
        LatLng pt = new LatLng(39.86923, 116.397428);
        //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
        InfoWindow mInfoWindow = new InfoWindow(button, pt, -47);
        //显示InfoWindow
        mBaiduMap.showInfoWindow(mInfoWindow);

        //----------------------------------------------------------
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 构造定位数据


    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

private boolean itIs = false;

    public void change(View view) {
        if (itIs) {
            //普通地图
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        } else {
            //卫星地图
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);

        }
        itIs = !itIs;

    }
    class NotifyListers extends BDNotifyListener{
        public void onNotify(BDLocation mlocation, float distance){
//                mVibrator01.vibrate(1000);//振动提醒已到设定位置附近
            Toast.makeText(MainActivity.this,"Hi!",Toast.LENGTH_SHORT).show();;
        }
    }
}
