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
        // Must initialize before create ads
        if (this.platform.is("ios") || this.platform.is("android")) {
            this.ads = cordova.plugins.BilMobileAdsCordova
            this.ads.initialize(false, (data) => console.log("data: " + data), (err) => console.log("err: " + err));
        }

        // Add Event Listener
        document.addEventListener("BilmobileAdsPluginEvent", (data) => {
            let obj = JSON.parse(JSON.stringify(data));
            console.log("adType: " + obj["adType"] + " - event: " + obj["event"] + " - message: " + obj["message"]);
            this.handlerAdEvents(obj["adType"], obj["event"], obj["message"]);
        });
    }

    handlerAdEvents(adType: any, event: any, data: any) {
        switch (event) {
        case 0:
            console.log('*** ' + adType + ' Ad loaded!');
            if (adType == "Interstitial") {
                // Code Process
            }
            break;
        case 2:
            // After Interstitial show and user closed, you can preload ads by call
            // this.ads.preLoadInterstitial();
            // or this.ads.preLoadRewarded();        
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