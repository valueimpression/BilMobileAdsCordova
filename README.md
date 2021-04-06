# BilMobileAdsCordova
 Integrate the ValueImpression SDK for Ionic

## Install

```bash
npm i cordova-plugin-bilmobileads
```

### IMPORT TO USE
```bash
declare var cordova;

export class YourClass {
    ads: any

    constructor(){
        this.platform.ready().then((data) => {
        if (this.platform.is("ios") || this.platform.is("android")) {
            this.ads = cordova.plugins.BilMobileAdsCordova;
            document.addEventListener("BilmobileAdsPluginEvent", this.eventHandler);
        }
        })
    }

    ionViewDidLeave() {
        console.log("Remove event listener: BilmobileAdsPluginEvent")
        document.removeEventListener("BilmobileAdsPluginEvent", this.eventHandler);
    }

    eventHandler(data) {
        console.log("BilmobileAdsPluginEvent: " + JSON.stringify(data));
        let obj = JSON.parse(JSON.stringify(data));
        YourClass.handlerAdEvents(obj["adType"], obj["event"], obj["message"]);
    }

    static handlerAdEvents(adType: any, event: any, data: any) {
        console.log("handlerAdEvents - adType: " + adType + " - event: " + event + " - message: " + data);
        switch (event) {
        case 0:
            console.log('*** ' + adType + ' Ad loaded!');
            break;
        case 2:
            // After Interstitial show and user closed, you can preload ads by call
            // BilMobileAds.preLoadInterstitial();
            // or BilMobileAds.preLoadRewarded();
            // if (adType == "Interstitial") {
            //   BilMobileAds.preLoadInterstitial();
            // }
        case 5:
            let obj = JSON.parse(data);
            console.log("typeRewarded: " + obj["typeRewarded"] + " - amountRewarded: " + obj["amountRewarded"]);
            break;
        }
    }
}

*Note: 
- AdPosition:
    TopCenter = 0
    TopLeft = 1
    TopRight = 2
    BottomCenter = 3
    BottomLeft = 4
    BottomRight = 5
    Center = 6

- Gender:
    Unknown = 0
    Male = 1
    Female = 2

- AdEvents:
    loaded = 0,
    opened = 1,
    closed = 2,
    clicked = 3,
    leftApplication = 4,
    rewarded = 5,
    failedToLoad = 6,
    failedToShow = 7

```