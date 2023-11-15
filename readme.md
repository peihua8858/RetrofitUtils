# RetrofitUtils
   一款针对Android平台下Android 基于Retrofit+Okhttp的网络请求及缓存框架。<br>

[![Jitpack](https://jitpack.io/v/peihua8858/RetrofitUtils.svg)](https://github.com/peihua8858)
[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-brightgreen.svg)](https://github.com/peihua8858)
[![Star](https://img.shields.io/github/stars/peihua8858/RetrofitUtils.svg)](https://github.com/peihua8858/RetrofitUtils)


## 目录
-[最新版本](https://github.com/peihua8858/RetrofitUtils/releases/tag/1.0.9)<br>
-[如何引用](#如何引用)<br>
-[进阶使用](#进阶使用)<br>
-[如何提Issues](https://github.com/peihua8858/RetrofitUtils/wiki/%E5%A6%82%E4%BD%95%E6%8F%90Issues%3F)<br>
-[License](#License)<br>



## 如何引用
* 把 `maven { url 'https://jitpack.io' }` 加入到 repositories 中
* 添加如下依赖，末尾的「latestVersion」指的是RetrofitUtils [![Download](https://jitpack.io/v/peihua8858/RetrofitUtils.svg)](https://jitpack.io/#peihua8858/RetrofitUtils) 里的版本名称，请自行替换。
使用Gradle
```sh
repositories {
  google()
  maven { url 'https://jitpack.io' }
}

dependencies {
  // RetrofitUtils
  implementation 'com.github.peihua8858:RetrofitUtils:${latestVersion}'
}
```

或者Maven:

```xml
<dependency>
  <groupId>com.github.peihua8858</groupId>
  <artifactId>RetrofitUtils</artifactId>
  <version>${latestVersion}</version>
</dependency>
```

## 进阶使用

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

## License
```sh
Copyright 2023 peihua

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
