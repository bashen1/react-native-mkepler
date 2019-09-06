import React, {Component} from 'react';
import {
    SafeAreaView,
    ScrollView,
    View,
    Text,
} from 'react-native';
import * as mKepler from './ModuleIndex';
import CommonCss from './CommonCss';
import ListItem from './ListItem';

class App extends Component {
    constructor(props) {
        super(props);
        this.state = {}
    }

    render(): React.ReactElement<any> | string | number | {} | React.ReactNodeArray | React.ReactPortal | boolean | null | undefined {
        let param = {
            isOpenByH5: false,
            processColor: '#ed0000',
            backTagID: '',
            openType: 'push',
            actId: '',
            ext: '',
            virtualAppkey: '',
            position: '',
            customParams: {}
        };
        return (
            <SafeAreaView style={[CommonCss.wrap]}>
                <ScrollView>
                    <Text style={{
                        textAlign: 'center',
                        fontSize: 20,
                        fontWeight: '500',
                        color: '#000',
                        marginTop: 30
                    }}>京东开普勒SDK（联盟）</Text>
                    <View style={[{marginTop: 30}]}>
                        <ListItem
                            title={'initSDK'}
                            action={async () => {
                                alert(JSON.stringify(await mKepler.initSDK({
                                    appKey: '3ec51467c2964905807a7316470b3897',
                                    appSecret: '53f0cc8c3e3f43f9bbcecb8475f10abc'
                                })));
                            }}
                        />
                        <ListItem
                            title={'showLogin'}
                            action={async () => {
                                alert(JSON.stringify(await mKepler.showLogin()));
                            }}
                        />
                        <ListItem
                            title={'isLogin'}
                            action={async () => {
                                alert(JSON.stringify(await mKepler.isLogin()));
                            }}
                        />
                        <ListItem
                            title={'logout'}
                            action={async () => {
                                alert(JSON.stringify(await mKepler.logout()));
                            }}
                        />
                        <ListItem
                            title={'showItemById'}
                            action={async () => {
                                alert(JSON.stringify(await mKepler.showItemById(Object.assign({}, param, {
                                    itemID: '2205070',
                                    ext: '1000141393_1587282006_1648676421',
                                    virtualAppkey: '3ec51467c2964905807a7316470b3897'
                                }))));
                            }}
                        />
                        <ListItem
                            title={'showItemByUrl'}
                            action={async () => {
                                alert(JSON.stringify(await mKepler.showItemByUrl(Object.assign({}, param, {
                                    url: 'https://item.jd.com/2205070.html',
                                    isOpenByH5: true
                                }))));
                            }}
                        />
                        <ListItem
                            title={'openOrderList'}
                            action={async () => {
                                alert(JSON.stringify(await mKepler.openOrderList(param)));
                            }}
                        />
                        <ListItem
                            title={'openNavigationPage'}
                            action={async () => {
                                alert(JSON.stringify(await mKepler.openNavigationPage(param)));
                            }}
                        />
                        <ListItem
                            title={'openSearchResult'}
                            action={async () => {
                                alert(JSON.stringify(await mKepler.openSearchResult(Object.assign({}, param, {
                                    searchKey: 'iphone'
                                }))));
                            }}
                        />
                        <ListItem
                            title={'openShoppingCart'}
                            action={async () => {
                                alert(JSON.stringify(await mKepler.openShoppingCart(param)));
                            }}
                        />
                    </View>
                </ScrollView>
            </SafeAreaView>
        );
    }
}

export default App;
