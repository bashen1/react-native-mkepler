
# react-native-mkepler (SDK Ver. 2.2.4)

## 开始

`$ npm install react-native-mkepler --save`

### 自动配置

`$ react-native link react-native-mkepler`

### 手动配置


#### iOS

1. 打开XCode工程中, 右键点击 `Libraries` ➜ `Add Files to [your project's name]`
2. 去 `node_modules` ➜ `react-native-mkepler` 目录添加 `RNReactNativeMkepler.xcodeproj`
3. 在工程 `Build Phases` ➜ `Link Binary With Libraries` 中添加 `libRNReactNativeMkepler.a`

#### Android

1. 打开 `android/app/src/main/java/[...]/MainActivity.java`
  - 在顶部添加 `import com.manmanbuy.mkepler.RNReactNativeMkeplerPackage;`
  - 在 `getPackages()` 方法后添加 `new RNReactNativeMkeplerPackage()`
2. 打开 `android/settings.gradle` ，添加:
  	```
  	include ':react-native-mkepler'
  	project(':react-native-mkepler').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-mkepler/android')
  	```
3. 打开 `android/app/build.gradle` ，添加:
  	```
      compile project(':react-native-mkepler')
  	```


### 其他配置

#### iOS

1. 将下载的开普勒iOS SDK引入到项目中
2. `URL Schemes` 添加 `sdkback + 开普勒appKey` 
3. `LSApplicationQueriesSchemes` 添加 `jdlogin`、`openapp.jdmobile`
4. 需要按照步骤来【加入依赖库、关闭Bitcode、设置application openURL等】http://kepler.jd.com/console/docCenterCatalog/docContent?channelId=46

#### Android

1. 打开 `android/app/build.gradle` ，在 `defaultConfig` 下添加:   
    ```
    manifestPlaceholders = [
            KeplerScheme  : "xxxxxx" //开普勒SDK中AndroidManifest.xml 中的值
    ]
    ```
2. 复制开普勒SDK中的 `safe.jpg` 到 `android/app/src/main/res/raw/safe.jpg`
3. 配置混淆
```
-keep class com.kepler.**{*;}
-dontwarn com.kepler.**
-keep class com.jingdong.jdma.**{*;}
-dontwarn com.jingdong.jdma.**
```

## 使用方法
```javascript
import * as mKepler from 'react-native-mkepler';

// TODO: What to do with the module?
```
