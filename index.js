import {NativeModules} from 'react-native';

const {RNReactNativeMkepler} = NativeModules;

export async function initSDK(params) {
    return await RNReactNativeMkepler.initSDK(params);
}

export async function showLogin() {
    return await RNReactNativeMkepler.showLogin();
}

export async function isLogin() {
    return await RNReactNativeMkepler.isLogin();
}

export async function logout() {
    return await RNReactNativeMkepler.logout();
}

export async function showItemById(params) {
    return await RNReactNativeMkepler.showItemById(params);
}

export async function showItemByUrl(params) {
    return await RNReactNativeMkepler.showItemByUrl(params);
}

export async function openOrderList(params) {
    return await RNReactNativeMkepler.openOrderList(params);
}

export async function openNavigationPage(params) {
    return await RNReactNativeMkepler.openNavigationPage(params);
}

export async function openSearchResult(params) {
    return await RNReactNativeMkepler.openSearchResult(params);
}

export async function openShoppingCart(params) {
    return await RNReactNativeMkepler.openShoppingCart(params);
}