
#import "RNReactNativeMkepler.h"
#import "UIColor+Hex.h"
#import <JDKeplerSDK/JDKeplerSDK.h>


@interface RNReactNativeMkepler () {
    NSInteger initKepler_success;
}
@end

@implementation RNReactNativeMkepler {
      bool hasListeners;
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

+ (BOOL) requiresMainQueueSetup {
    return YES;
}

RCT_EXPORT_MODULE()

// Will be called when this module's first listener is added.
-(void)startObserving {
    hasListeners = YES;
    // Set up any upstream listeners or background tasks as necessary
}

// Will be called when this module's last listener is removed, or on dealloc.
-(void)stopObserving {
    hasListeners = NO;
    // Remove upstream listeners, stop unnecessary background tasks
}

- (instancetype)init {
    self = [super init];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(handleOpenURL:)
                                                     name:@"RCTOpenURLNotification"
                                                   object:nil];
    }
    return self;
}

- (void)handleOpenURL:(NSNotification *)note {
    NSDictionary *userInfo = note.userInfo;
    NSString *url = userInfo[@"url"];
    NSURL *URL = [NSURL URLWithString:url];
    [[KeplerApiManager sharedKPService] handleOpenURL:URL];
}

///////
/**
 *  处理公用参数
 *  isOpenByH5，processColor，backTagID，openType，customParams
 */
- (NSDictionary *)dealParam:(NSDictionary *)param {
    NSInteger openType = 2;
    NSDictionary *customParams = (NSDictionary *)param[@"customParams"];
    BOOL isOpenByH5 = [(NSNumber *)param[@"isOpenByH5"] boolValue];
    NSString *processColor = (NSString *)param[@"processColor"];
    NSString *JDappBackTagID = (NSString *)param[@"backTagID"];
    if([(NSString *)param[@"openType"] isEqual:@"present"]){
        openType = 1;
    }else{
        openType = 2;
    }
    
    NSString *actId = (NSString *)param[@"actId"];
    NSString *ext = (NSString *)param[@"ext"];
    NSString *virtualAppkey = (NSString *)param[@"virtualAppkey"];
    
    [[KeplerApiManager sharedKPService] setKeplerProgressBarColor:[UIColor colorWithHexString: processColor]];
    [KeplerApiManager sharedKPService].isOpenByH5 = isOpenByH5;
    if(JDappBackTagID != nil && ![JDappBackTagID isEqual:@""]){
        [KeplerApiManager sharedKPService].JDappBackTagID = JDappBackTagID;
    }
    
    if(actId != nil && ![actId isEqual:@""]){
        [KeplerApiManager sharedKPService].actId = actId;
    }
    
    if(ext != nil && ![ext isEqual:@""]){
        [KeplerApiManager sharedKPService].ext = ext;
    }
    
    if(virtualAppkey != nil && ![virtualAppkey isEqual:@""]){
        [KeplerApiManager sharedKPService].secondAppKey = virtualAppkey;
    }
    
    //返回处理后的参数
    return @{
        @"openType":[NSNumber numberWithInteger:openType],
        @"customParams":customParams
    };
}
///////

//初始化SDK
RCT_EXPORT_METHOD(initSDK: (NSDictionary *)param resolve: (RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    NSString *AppKey = @"";
    NSString *AppSecret = @"";
    if ((NSString *)param[@"appKey"] != nil) {
        AppKey=(NSString *)param[@"appKey"];
    }
    if ((NSString *)param[@"appSecret"] != nil) {
        AppSecret=(NSString *)param[@"appSecret"];
    }
    if(![AppKey isEqual: @""] && ![AppSecret isEqual: @""]){
        [[KeplerApiManager sharedKPService]asyncInitSdk:AppKey secretKey:AppSecret sucessCallback:^(){
            NSDictionary *ret = @{@"code": @"0", @"message":@"success"};
            initKepler_success = 1;
            resolve(ret);
        }failedCallback:^(NSError *error){
            NSDictionary *ret = @{@"code": @(error.code), @"message":error.description};
            resolve(ret);
        }];
    }else{
        NSDictionary *ret = @{@"code": @"1", @"message":@"AppKey或AppSecret为空"};
        resolve(ret);
    }
}

//授权登录
RCT_EXPORT_METHOD(showLogin: (RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    if(initKepler_success != 1){
        NSDictionary *ret = @{@"code": @"0", @"message":@"未初始化SDK"};
        resolve(ret);
        return;
    }
    [[KeplerApiManager sharedKPService] keplerLoginWithSuccess:^{
        //已登录
        NSDictionary *ret = @{@"code": @"1", @"message":@"success"};
        resolve(ret);
    } failure:^{
        //如果没有登录，唤醒app，并输出用户信息
        [[KeplerApiManager sharedKPService] keplerLoginWithViewController:([UIApplication sharedApplication].delegate.window.rootViewController) success:^{
            //授权成功
            NSDictionary *ret = @{@"code": @"1", @"message":@"success"};
            resolve(ret);
        } failure:^(NSError *error) {
            //授权失败
            NSDictionary *ret = @{@"code": @(error.code), @"message":error.description};
            resolve(ret);
        }];
    }];
}

//是否授权
RCT_EXPORT_METHOD(isLogin: (RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    if(initKepler_success != 1){
        NSDictionary *ret = @{@"code": @"0", @"message":@"未初始化SDK"};
        resolve(ret);
        return;
    }
    [[KeplerApiManager sharedKPService] keplerLoginWithSuccess:^{
        //已登录
        NSDictionary *ret = @{@"code": @"1", @"message":@"success"};
        resolve(ret);
    } failure:^{
        //如果没有登录，唤醒app，并输出用户信息
        NSDictionary *ret = @{@"code": @"0", @"message":@"not login"};
        resolve(ret);
    }];
}

//取消授权
RCT_EXPORT_METHOD(logout: (RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    if(initKepler_success != 1){
        NSDictionary *ret = @{@"code": @"0", @"message":@"未初始化SDK"};
        resolve(ret);
        return;
    }
    [[KeplerApiManager sharedKPService] keplerLoginWithSuccess:^{
        //已登录
        [[KeplerApiManager sharedKPService] cancelAuth];
        NSDictionary *ret = @{@"code":@"0",@"message":@"取消授权成功"};
        resolve(ret);
    } failure:^{
        NSDictionary *ret = @{@"code":@"90000",@"message":@"未授权"};
        resolve(ret);
    }];
}

//通过sku打开商品
RCT_EXPORT_METHOD(showItemById: (NSDictionary *)param resolve: (RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    if(initKepler_success != 1){
        NSDictionary *ret = @{@"code": @"0", @"message":@"未初始化SDK"};
        resolve(ret);
        return;
    }
    NSDictionary* result = [self dealParam: param];
    NSInteger openType = [result[@"openType"] intValue];
    NSDictionary *customParams = result[@"customParams"];
    NSString *skuID = (NSString *)param[@"itemID"];
    
    [[KeplerApiManager sharedKPService] openItemDetailWithSKU:skuID sourceController:([UIApplication sharedApplication].delegate.window.rootViewController) jumpType:openType userInfo:customParams];
}

//通过url打开商品
RCT_EXPORT_METHOD(showItemByUrl: (NSDictionary *)param resolve: (RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    if(initKepler_success != 1){
        NSDictionary *ret = @{@"code": @"0", @"message":@"未初始化SDK"};
        resolve(ret);
        return;
    }
    NSDictionary* result = [self dealParam: param];
    NSInteger openType = [result[@"openType"] intValue];
    NSDictionary *customParams = result[@"customParams"];
    NSString *url = (NSString *)param[@"url"];
    
    [[KeplerApiManager sharedKPService] openKeplerPageWithURL:url sourceController:([UIApplication sharedApplication].delegate.window.rootViewController) jumpType:openType userInfo:customParams];
}

//打开订单列表
RCT_EXPORT_METHOD(openOrderList: (NSDictionary *)param resolve: (RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    if(initKepler_success != 1){
        NSDictionary *ret = @{@"code": @"0", @"message":@"未初始化SDK"};
        resolve(ret);
        return;
    }
    NSDictionary* result = [self dealParam: param];
    NSInteger openType = [result[@"openType"] intValue];
    NSDictionary *customParams = result[@"customParams"];
    
    [[KeplerApiManager sharedKPService] openOrderList:([UIApplication sharedApplication].delegate.window.rootViewController) jumpType:openType userInfo:customParams];
}

//打开导航页
RCT_EXPORT_METHOD(openNavigationPage: (NSDictionary *)param resolve: (RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    if(initKepler_success != 1){
        NSDictionary *ret = @{@"code": @"0", @"message":@"未初始化SDK"};
        resolve(ret);
        return;
    }
    NSDictionary* result = [self dealParam: param];
    NSInteger openType = [result[@"openType"] intValue];
    NSDictionary *customParams = result[@"customParams"];
    [[KeplerApiManager sharedKPService] openNavigationPage:([UIApplication sharedApplication].delegate.window.rootViewController) jumpType:openType userInfo:customParams];
}

//打开搜索结果页
RCT_EXPORT_METHOD(openSearchResult: (NSDictionary *)param resolve: (RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    if(initKepler_success != 1){
        NSDictionary *ret = @{@"code": @"0", @"message":@"未初始化SDK"};
        resolve(ret);
        return;
    }
    NSDictionary* result = [self dealParam: param];
    NSInteger openType = [result[@"openType"] intValue];
    NSDictionary *customParams = result[@"customParams"];
    NSString *searchKey = (NSString *)param[@"searchKey"];
    [[KeplerApiManager sharedKPService] openSearchResult:searchKey sourceController:([UIApplication sharedApplication].delegate.window.rootViewController) jumpType:openType userInfo:customParams];
}

//打开购物车页
RCT_EXPORT_METHOD(openShoppingCart: (NSDictionary *)param resolve: (RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    if(initKepler_success != 1){
        NSDictionary *ret = @{@"code": @"0", @"message":@"未初始化SDK"};
        resolve(ret);
        return;
    }
    NSDictionary* result = [self dealParam: param];
    NSInteger openType = [result[@"openType"] intValue];
    NSDictionary *customParams = result[@"customParams"];
    [[KeplerApiManager sharedKPService] openShoppingCart:([UIApplication sharedApplication].delegate.window.rootViewController) jumpType:openType userInfo:customParams];
}
///////

@end
  
