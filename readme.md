# Android 基于Retrofit+Okhttp+Rxjava2的网络请求框架
## 功能介绍
### 1、网络请求
---
 + 初始化网络
  ```java 
    OKHttpBuilder.newBuilder(MainApplication.getContext())
                .writeTimeout(30_000, TimeUnit.MILLISECONDS)
                .readTimeout(30_000, TimeUnit.MILLISECONDS)
                .connectTimeout(30_000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(false)
                .timeoutInterceptor()
                .build();
  ```
+   请求参数
 ```java
     //请求参数设置
      VpRequestParams request = new VpRequestParams();
      ApiManager.Api().getMenuList(request.createRequestBody())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(...);
 ```
### 2、网络数据缓存
---
如果开启缓存，则一个请求发起，将先返回缓存数据，网络请求返回，在返回网络数据。
```java
     //开启读写缓存操作
      VpRequestParams request = new VpRequestParams(true);
      ApiManager.Api().getMenuList(request.createRequestBody())
      ....
```
### 3、动态设置网络超时时间
```java
     //动态设置超时时间
      VpRequestParams request = new VpRequestParams(true);
        或
      request.setOpenCache(true);
        或
      request.openCache();
      
      request.connectTimeoutMillis(1000);
      request.readTimeoutMillis(1000);
      request.writeTimeoutMillis(1000);
      ApiManager.Api().getMenuList(request.createRequestBody())
      ....
```
### 4、RxJava2 生命周期处理
+ 通过请求参数设置
```java
     //动态设置超时时间
      VpRequestParams request = new VpRequestParams(true);
      request.connectTimeoutMillis(1000);
      request.readTimeoutMillis(1000);
      request.writeTimeoutMillis(1000);
      ApiManager.Api().getMenuList(request.createRequestBody())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .as(RxLifecycleUtil.bindLifecycle(fragment/activity))
            .subscribe(...);
```
+ 通过请求头设置
```java
    @POST(URL)
    Flowable<Response<HttpResponse<MenuBean>>> getData(
            @Header(value = TimeoutInterceptor.CONNECT_TIMEOUT) long connectTimeout,
            @Header(value = TimeoutInterceptor.READ_TIMEOUT) long readTimeout,
            @Header(value = TimeoutInterceptor.WRITE_TIMEOUT) long writeTimeout,
            @Body RequestBody request);
```
### 5、缓存初始化
```java
   CacheManager.initCacheManager(context, "mnt/cache/network/");
```
### 6、网络日志
```java 
    OKHttpBuilder.newBuilder(MainApplication.getContext())
             .netLogInterceptor(new NetLoggingInterceptor.OnDynamicParamCallback() {
                            @Override
                            public String getVersionName() {
                                return BuildConfig.VERSION_NAME;
                            }

                            @Override
                            public String getLogTag() {
                                return "Android-Demo";
                            }

                            @Override
                            public String getServiceIp() {
                                return "10.36.5.100";
                            }
                        }) .build();
     或者
     OKHttpBuilder.newBuilder(MainApplication.getContext())
             .addInterceptor(new NetLoggingInterceptor(new NetLoggingInterceptor.OnDynamicParamCallback() {
                            @Override
                            public String getVersionName() {
                                 return BuildConfig.VERSION_NAME;
                            }

                            @Override
                            public String getLogTag() {
                                 return "Android-Demo";
                            }

                            @Override
                            public String getServiceIp() {
                                return "10.36.5.100";
                            }
                        })) .build();
```
## 添加存储库

```py
 repositories {
        maven { url 'http://10.36.5.100:8081/repository/maven-public/' }
    }
```

## 添加依赖

```py
dependencies {
    implementation "com.fz.network:Network:1.1.4"
    implementation "com.squareup.retrofit2:retrofit:2.5.0"
}
```



