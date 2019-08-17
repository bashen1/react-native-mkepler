
package com.manmanbuy.mkepler;

import android.app.Application;
import android.os.Handler;
import org.json.JSONException;

import com.facebook.react.bridge.*;

import com.kepler.jd.Listener.AsyncInitListener;
import com.kepler.jd.Listener.ActionCallBck;
import com.kepler.jd.Listener.LoginListener;
import com.kepler.jd.Listener.OpenAppAction;
import com.kepler.jd.login.KeplerApiManager;
import com.kepler.jd.sdk.bean.KelperTask;
import com.kepler.jd.sdk.bean.KeplerAttachParameter;
import com.kepler.jd.sdk.bean.KeplerGlobalParameter;
import com.kepler.jd.sdk.exception.KeplerBufferOverflowException;

public class RNReactNativeMkeplerModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private final KeplerAttachParameter mKeplerAttachParameter = new KeplerAttachParameter();// 这个是即时性参数
    // 可以设置
    Integer timeOut = 15;
    Handler mHandler;
    KelperTask mKelperTask;
    int initKepler_success = 0;
    OpenAppAction mOpenAppAction = new OpenAppAction() {
        @Override
        public void onStatus(final int status) {

        }
    };


    public RNReactNativeMkeplerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNReactNativeMkepler";
    }


    @ReactMethod // 初始化SDK ----传入AppKey与AppSecret，获取config.xml下的配置
    public void initSDK(final ReadableMap data, final Promise p) {
        if (initKepler_success == 1) {
            // 初始化成功
            WritableMap map = Arguments.createMap();
            map.putString("message", "success");
            map.putString("code", Integer.toString(0));
            p.resolve(map);
            return;
        }
        String AppKey = data.getString("appKey");
        String AppSecret = data.getString("appSecret");
        if (!AppKey.equals("") && !AppSecret.equals("")) {
            Application app = (Application) reactContext.getApplicationContext();
            KeplerApiManager.asyncInitSdk(app,
                    AppKey, AppSecret, new AsyncInitListener() {
                        public void onSuccess() {
                            // 初始化成功
                            initKepler_success = 1;
                            WritableMap map = Arguments.createMap();
                            map.putString("message", "success");
                            map.putString("code", Integer.toString(0));
                            p.resolve(map);
                        }


                        public void onFailure() {
                            WritableMap map = Arguments.createMap();
                            map.putString("message", "Kepler asyncInitSdk 授权失败，请检查lib 工程资源引用；包名,签名证书是否和注册一致");
                            map.putString("code", Integer.toString(1));
                            p.resolve(map);
                        }
                    });
        } else {
            WritableMap map = Arguments.createMap();
            map.putString("message", "AppKey或AppSecret为空");
            map.putString("code", Integer.toString(1));
            p.resolve(map);
        }
    }

    @ReactMethod // 授权登录 ----无参数传入
    public void showLogin(final Promise p) {
        if (initKepler_success != 1) {
            WritableMap map = Arguments.createMap();
            map.putString("message", "未初始化SDK");
            map.putString("code", Integer.toString(0));
            p.resolve(map);
            return;
        }
        // 并不保证是登录状态
        KeplerApiManager.getWebViewService().checkLoginState(new ActionCallBck() {
            @Override
            public boolean onDateCall(int key, String info) { //已登录
                WritableMap map = Arguments.createMap();
                map.putString("message", "success");
                map.putString("code", Integer.toString(1));
                p.resolve(map);
                return false;
            }

            @Override
            public boolean onErrCall(int key, String error) { //未登录
                // 未登陆
                final LoginListener mLoginListener = new LoginListener() {
                    @Override
                    public void authSuccess() {
                        WritableMap map = Arguments.createMap();
                        map.putString("message", "success");
                        map.putString("code", Integer.toString(1));
                        p.resolve(map);
                    }

                    @Override
                    public void authFailed(int errorCode) {
                        String msg = "授权失败";
                        switch (errorCode) {
                            // 初始化失败
                            case KeplerApiManager.KeplerApiManagerLoginErr_Init:
                                msg = "初始化失败";
                                break;
                            // 初始化没有完成
                            case KeplerApiManager.KeplerApiManagerLoginErr_InitIng:
                                msg = "初始化没有完成";
                                break;
                            // 跳转url
                            case KeplerApiManager.KeplerApiManagerLoginErr_openH5authPageURLSettingNull:
                                msg = "跳转url";
                                break;
                            // 获取失败(OAuth授权之后，获取cookie过程出错)
                            case KeplerApiManager.KeplerApiManagerLoginErr_getTokenErr:
                                msg = "获取失败(OAuth授权之后，获取cookie过程出错)";
                                break;
                            // 用户取消
                            case KeplerApiManager.KeplerApiManagerLoginErr_User_Cancel:
                                msg = "用户取消";
                                break;
                            // 打开授权页面失败
                            case KeplerApiManager.KeplerApiManagerLoginErr_AuthErr_ActivityOpen:
                                msg = "打开授权页面失败";
                                break;
                            default:
                                msg = "授权失败";
                                break;
                        }
                        WritableMap map = Arguments.createMap();
                        map.putString("message", msg);
                        map.putString("code", Integer.toString(errorCode));
                        p.resolve(map);
                    }
                };

                KeplerApiManager.getWebViewService().login(reactContext.getCurrentActivity(), mLoginListener);
                return false;
            }
        });
    }

    @ReactMethod // 是否登录
    public void isLogin(final Promise p) {
        // 并不保证是登录状态
        if (initKepler_success != 1) {
            WritableMap map = Arguments.createMap();
            map.putString("message", "未初始化SDK");
            map.putString("code", Integer.toString(0));
            p.resolve(map);
            return;
        }
        KeplerApiManager.getWebViewService().checkLoginState(new ActionCallBck() {
            @Override
            public boolean onDateCall(int key, String info) { //已登录
                WritableMap map = Arguments.createMap();
                map.putString("message", "success");
                map.putString("code", Integer.toString(1));
                p.resolve(map);
                return true;
            }

            @Override
            public boolean onErrCall(int key, String error) { //未登录
                // 未登陆
                WritableMap map = Arguments.createMap();
                map.putString("message", "not login");
                map.putString("code", Integer.toString(0));
                p.resolve(map);
                return false;
            }
        });
    }

    @ReactMethod // 退出登录
    public void logout(final Promise p) {
        if (initKepler_success != 1) {
            WritableMap map = Arguments.createMap();
            map.putString("message", "未初始化SDK");
            map.putString("code", Integer.toString(0));
            p.resolve(map);
            return;
        }
        KeplerApiManager.getWebViewService().checkLoginState(new ActionCallBck() {
            @Override
            public boolean onDateCall(int key, String info) {
                KeplerApiManager.getWebViewService().cancelAuth(reactContext);
                WritableMap map = Arguments.createMap();
                map.putString("message", "取消授权成功");
                map.putString("code", Integer.toString(0));
                p.resolve(map);
                return false;
            }

            @Override
            public boolean onErrCall(int key, String error) {
                WritableMap map = Arguments.createMap();
                map.putString("message", "未授权");
                map.putString("code", Integer.toString(90000));
                p.resolve(map);
                return false;
            }
        });
    }

    /**
     * 通过sku打开商品
     * ----传入itemID，isOpenByH5，processColor，backTagID，openType，customParams
     */
    @ReactMethod
    public void showItemById(final ReadableMap data, final Promise p) {
        if (initKepler_success != 1) {
            WritableMap map = Arguments.createMap();
            map.putString("message", "未初始化SDK");
            map.putString("code", Integer.toString(0));
            p.resolve(map);
            return;
        }
        String itemID = data.getString("itemID");
        Boolean isOpenByH5 = data.getBoolean("isOpenByH5");
        this.dealParam(data);
        if (isOpenByH5 == true) {
            try {
                KeplerApiManager.getWebViewService()
                        .openItemDetailsWebViewPage(itemID,
                                mKeplerAttachParameter);
            } catch (KeplerBufferOverflowException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                mKelperTask = KeplerApiManager.getWebViewService()
                        .openItemDetailsPage(itemID, mKeplerAttachParameter,
                                reactContext, mOpenAppAction, timeOut);
            } catch (KeplerBufferOverflowException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过url打开连接
     * ----传入url，isOpenByH5，processColor，backTagID，openType，customParams
     */
    @ReactMethod
    public void showItemByUrl(final ReadableMap data, final Promise p) {
        if (initKepler_success != 1) {
            WritableMap map = Arguments.createMap();
            map.putString("message", "未初始化SDK");
            map.putString("code", Integer.toString(0));
            p.resolve(map);
            return;
        }
        String url = data.getString("url");
        Boolean isOpenByH5 = data.getBoolean("isOpenByH5");
        this.dealParam(data);
        if (isOpenByH5 == true) {
            try {
                KeplerApiManager.getWebViewService().openJDUrlWebViewPage(url,
                        mKeplerAttachParameter);
            } catch (KeplerBufferOverflowException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                mKelperTask = KeplerApiManager.getWebViewService()
                        .openJDUrlPage(url, mKeplerAttachParameter,
                                reactContext, mOpenAppAction, timeOut);
            } catch (KeplerBufferOverflowException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 打开订单列表 ----传入isOpenByH5，processColor，backTagID，openType，customParams
     */
    @ReactMethod
    public void openOrderList(final ReadableMap data, final Promise p) {
        if (initKepler_success != 1) {
            WritableMap map = Arguments.createMap();
            map.putString("message", "未初始化SDK");
            map.putString("code", Integer.toString(0));
            p.resolve(map);
            return;
        }
        Boolean isOpenByH5 = data.getBoolean("isOpenByH5");
        this.dealParam(data);
        if (isOpenByH5 == true) {
            try {
                KeplerApiManager.getWebViewService().openOrderListWebViewPage(
                        mKeplerAttachParameter);
            } catch (KeplerBufferOverflowException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                mKelperTask = KeplerApiManager.getWebViewService()
                        .openOrderListPage(mKeplerAttachParameter,
                                reactContext, mOpenAppAction, timeOut);
            } catch (KeplerBufferOverflowException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 打开导航页 ----传入isOpenByH5，processColor，backTagID，openType，customParams
     */
    @ReactMethod
    public void openNavigationPage(final ReadableMap data, final Promise p) {
        if (initKepler_success != 1) {
            WritableMap map = Arguments.createMap();
            map.putString("message", "未初始化SDK");
            map.putString("code", Integer.toString(0));
            p.resolve(map);
            return;
        }
        Boolean isOpenByH5 = data.getBoolean("isOpenByH5");
        this.dealParam(data);
        if (isOpenByH5 == true) {
            try {
                KeplerApiManager.getWebViewService().openNavigationWebViewPage(
                        mKeplerAttachParameter);
            } catch (KeplerBufferOverflowException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                mKelperTask = KeplerApiManager.getWebViewService()
                        .openNavigationPage(mKeplerAttachParameter,
                                reactContext, mOpenAppAction, timeOut);
            } catch (KeplerBufferOverflowException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据搜索关键字打开搜索结果页
     * searchKey，isOpenByH5，processColor，backTagID，openType，customParams
     */
    @ReactMethod
    public void openSearchResult(final ReadableMap data, final Promise p) {
        if (initKepler_success != 1) {
            WritableMap map = Arguments.createMap();
            map.putString("message", "未初始化SDK");
            map.putString("code", Integer.toString(0));
            p.resolve(map);
            return;
        }
        String searchKey = data.getString("searchKey");
        Boolean isOpenByH5 = data.getBoolean("isOpenByH5");
        this.dealParam(data);
        if (isOpenByH5 == true) {
            try {
                KeplerApiManager.getWebViewService().openSearchWebViewPage(
                        searchKey, mKeplerAttachParameter);
            } catch (KeplerBufferOverflowException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                mKelperTask = KeplerApiManager.getWebViewService()
                        .openSearchPage(searchKey, mKeplerAttachParameter,
                                reactContext, mOpenAppAction, timeOut);
            } catch (KeplerBufferOverflowException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 打开购物车界面 isOpenByH5，processColor，backTagID，openType，customParams
     */
    @ReactMethod
    public void openShoppingCart(final ReadableMap data, final Promise p) {
        if (initKepler_success != 1) {
            WritableMap map = Arguments.createMap();
            map.putString("message", "未初始化SDK");
            map.putString("code", Integer.toString(0));
            p.resolve(map);
            return;
        }
        Boolean isOpenByH5 = data.getBoolean("isOpenByH5");
        this.dealParam(data);
        if (isOpenByH5 == true) {
            try {
                KeplerApiManager.getWebViewService().openCartWebViewPage(
                        mKeplerAttachParameter);
            } catch (KeplerBufferOverflowException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                mKelperTask = KeplerApiManager.getWebViewService()
                        .openCartPage(mKeplerAttachParameter, reactContext, mOpenAppAction, timeOut);
            } catch (KeplerBufferOverflowException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理公用参数 isOpenByH5，processColor，backTagID，openType，customParams
     */
    private void dealParam(final ReadableMap data) {
        //设置小把手
        if (data.getString("backTagID") != null) {
            String backTagID = data.getString("backTagID");
            if (!backTagID.equals("")) {
                KeplerGlobalParameter.getSingleton().setJDappBackTagID(backTagID);
            }
        }

        //设置ActId 内容ID
        if (data.getString("actId") != null) {
            String actId = data.getString("actId");
            if (!actId.equals("")) {
                KeplerGlobalParameter.getSingleton().setActId(actId);
            }
        }

        //ext：内容渠道扩展字段
        if (data.getString("ext") != null) {
            String ext = data.getString("ext");
            if (!ext.equals("")) {
                KeplerGlobalParameter.getSingleton().setExt(ext);
            }
        }

        //virtualAppkey:计费到另外一个账号体系的appkey
        if (data.getString("virtualAppkey") != null) {
            String virtualAppkey = data.getString("virtualAppkey");
            if (!virtualAppkey.equals("")) {
                KeplerGlobalParameter.getSingleton().setVirtualAppkey(virtualAppkey);
            }
        }

        //计费参数
        if (data.getString("position") != null) {
            String position = data.getString("position");
            if (!position.equals("")) {
                try {
                    mKeplerAttachParameter.setPositionId(Integer.parseInt(position));
                } catch (KeplerBufferOverflowException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        //设置customParams
        ReadableMap customParams = data.getMap("customParams");
        try {
            mKeplerAttachParameter.setCustomerInfo(customParams.toString());
        } catch (KeplerBufferOverflowException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
