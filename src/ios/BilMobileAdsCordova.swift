//
//  BilMobileAdsCordova.swift
//  
//
//  Created by HNL on 30/03/2021.
//

import BilMobileAds

@objc(BilMobileAdsCordova) class BilMobileAdsCordova : CDVPlugin, ADBannerDelegate, ADInterstitialDelegate, ADRewardedDelegate {
    
    func getUIViewController() -> UIViewController {
        return (UIApplication.shared.keyWindow?.rootViewController)!
    }
    func sendMessSucc(msg: String!, _ command: CDVInvokedUrlCommand) {
        let pluginResult = CDVPluginResult(
            status: CDVCommandStatus_OK,
            messageAs: msg
        )
        self.commandDelegate!.send(pluginResult, callbackId: command.callbackId)
    }
    func sendMessErr(msg: String, _ command: CDVInvokedUrlCommand) {
        let pluginResult = CDVPluginResult(
            status: CDVCommandStatus_ERROR,
            messageAs: msg
        )
        self.commandDelegate!.send(pluginResult, callbackId: command.callbackId)
    }
    func fireEventToCordova(_ eventName: String, data: String!) {
        var js = "javascript:cordova.fireDocumentEvent('BilmobileAdsPluginEvent'"
        js += !data.isEmpty ? "," + data + ");" : ");"
        self.webViewEngine.evaluateJavaScript(js, completionHandler: nil)
    }
    func getMess(adType: String, event: Int, mess: String) -> String? {
        let data = EventDataType(adType: adType, event: event, message: mess)
        let json = try? JSONEncoder().encode(data)
        let str = String(decoding: json!, as: UTF8.self)
        return str;
    }
    
    // MARK: - PBM
    @objc(initialize:)
    func initialize(_ command: CDVInvokedUrlCommand) {
        DispatchQueue.main.async {
            let testMode = command.arguments[0] as? Bool ?? false
            PBMobileAds.shared.initialize(testMode: testMode)
            self.sendMessSucc(msg: "initialize success", command)
        }
    }
    
    @objc(enableCOPPA:)
    func enableCOPPA(_ command: CDVInvokedUrlCommand) {
        PBMobileAds.shared.enableCOPPA()
        self.sendMessSucc(msg: "enableCOPPA success", command)
    }
    
    @objc(disableCOPPA:)
    func disableCOPPA(_ command: CDVInvokedUrlCommand) {
        PBMobileAds.shared.disableCOPPA()
        self.sendMessSucc(msg: "disableCOPPA success", command)
    }
    
    @objc(setYearOfBirth:)
    func setYearOfBirth(_ command: CDVInvokedUrlCommand) {
        guard let yearOfBirth = command.arguments[0] as? Int else {
            return;
        }
        DispatchQueue.main.async {
            PBMobileAds.shared.setYearOfBirth(yob: yearOfBirth)
            self.sendMessSucc(msg: "setYearOfBirth success", command)
        }
    }
    
    @objc(setGender:)
    func setGender(_ command: CDVInvokedUrlCommand) {
        DispatchQueue.main.async {
            let gender = command.arguments[0] as? Int ?? 0
            switch gender {
            case 0:
                PBMobileAds.shared.setGender(gender: .unknown);
            case 1:
                PBMobileAds.shared.setGender(gender: .male);
            case 2:
                PBMobileAds.shared.setGender(gender: .female);
            default:
                PBMobileAds.shared.setGender(gender: .unknown);
            }
            
            self.sendMessSucc(msg: "setGender success", command)
        }
    }
    
    // MARK: - BANNER
    private var banner: ADBanner!
    private var adPlacehold: UIViewController!
    private let BANNER_EVENT = "BilmobileAdsPluginEvent"
    private var adPosition: Int = BilUtilsCordova.BottomCenter
    
    @objc(createBanner:)
    func createBanner(_ command: CDVInvokedUrlCommand) {
        if self.banner != nil {
            BilUtilsCordova.log(mess: "ADBanner already exist")
            self.sendMessErr(msg: "ADBanner already exist", command)
            return
        }
        
        let adUnitId = command.arguments[0] as? String ?? ""
        if adUnitId.isEmpty {
            BilUtilsCordova.log(mess: "adUnitId expected an string value and not empty")
            self.sendMessErr(msg: "adUnitId expected an string value and not empty", command)
            return
        }
        
        self.adPosition = command.arguments[1] as? Int ?? BilUtilsCordova.BottomCenter
        DispatchQueue.main.async {
            let uvCtrl = self.getUIViewController()
            self.banner = ADBanner(uvCtrl, view: uvCtrl.view, placement: adUnitId)
            self.banner.setListener(self)
            self.banner.isDisSetupAnchor(true)
            self.sendMessSucc(msg: "", command)
        }
    }
    
    @objc(loadBanner:)
    func loadBanner(_ command: CDVInvokedUrlCommand) {
        if !self.isPluginReady(command) { return }
        
        DispatchQueue.main.async {
            if !self.banner.isLoaded() {
                self.banner.load()
            }
            self.sendMessSucc(msg: "", command)
        }
    }
    
    @objc(showBanner:)
    func showBanner(_ command: CDVInvokedUrlCommand) {
        if !self.isPluginReady(command) { return }
        
        DispatchQueue.main.async {
            if self.banner.isLoaded() {
                self.banner.getADView().isHidden = false
                self.banner.startFetchData()
            }
            self.sendMessSucc(msg: "", command)
        }
    }
    
    @objc(hideBanner:)
    func hideBanner(_ command: CDVInvokedUrlCommand) {
        if !self.isPluginReady(command) { return }
        
        DispatchQueue.main.async {
            if self.banner.isLoaded() {
                self.banner.getADView().isHidden = true
                self.banner.stopFetchData()
            }
            self.sendMessSucc(msg: "", command)
        }
    }
    
    @objc(destroyBanner:)
    func destroyBanner(_ command: CDVInvokedUrlCommand) {
        if !self.isPluginReady(command) { return }
        
        self.banner.destroy()
        self.banner = nil;
        self.sendMessSucc(msg: "", command)
    }
    
    @objc(setPositionBanner:)
    func setPositionBanner(_ command: CDVInvokedUrlCommand) {
        if !self.isPluginReady(command) { return }
        
        DispatchQueue.main.async {
            self.adPosition = command.arguments[0] as? Int ?? BilUtilsCordova.BottomCenter
            self.setupPosition(positionView: self.banner.getADView(), inParentView: self.getUIViewController().view, adPosition: self.adPosition)
            self.sendMessSucc(msg: "", command)
        }
    }
    
    @objc(getSafeArea:)
    func getSafeArea(_ command: CDVInvokedUrlCommand) {
        if !self.isPluginReady(command) { return }
        
        DispatchQueue.main.async {
            let window = UIApplication.shared.keyWindow
            let topPadding = window?.safeAreaInsets.top ?? 0
            let bottomPadding = window?.safeAreaInsets.bottom ?? 0
            let bannerH = self.banner.getADView().frame.size.height

            BilUtilsCordova.log(mess: "topPadding: \(bannerH + topPadding) | bottomPadding: \(bannerH + bottomPadding)")
            
            let data = "{\"topPadding\": \(bannerH + topPadding), \"bottomPadding\": \(bannerH + bottomPadding)}"
            self.sendMessSucc(msg: data, command)
        }
    }
    
    func isPluginReady(_ command: CDVInvokedUrlCommand) -> Bool {
        if self.banner == nil {
            BilUtilsCordova.log(mess: "ADBanner is nil. You need init ADBanner first")
            self.sendMessErr(msg: "ADBanner is nil. You need init ADBanner first", command)
            return false
        }
        return true
    }
    
    func setupPosition(positionView view: UIView, inParentView parentView: UIView, adPosition: Int) {
        var parentBounds: CGRect = parentView.bounds;
        if #available(iOS 11, *) {
            let safeAreaFrame: CGRect = parentView.safeAreaLayoutGuide.layoutFrame;
            if !CGSize.zero.equalTo(safeAreaFrame.size) {
                parentBounds = safeAreaFrame;
            }
        }
        
        var top: CGFloat = parentBounds.minY + view.bounds.midY
        var left: CGFloat = parentBounds.minX  + view.bounds.midX
        
        let bottom: CGFloat = parentBounds.maxY - view.bounds.midY
        let right: CGFloat = parentBounds.maxX - view.bounds.midX
        let centerX: CGFloat = parentBounds.midX
        let centerY: CGFloat = parentBounds.midY
        
        /// If this view is of greater or equal width to the parent view, do not offset
        /// to edge of safe area. Eg for smart banners that are still full screen width.
        if (view.bounds.width >= parentView.bounds.width) {
            left = parentView.bounds.midX
        }
        
        /// Similarly for height, if view is of custom size which is full screen height, do not offset.
        if (view.bounds.height >= parentView.bounds.height) {
            top = parentView.bounds.midY
        }
        
        var center: CGPoint = CGPoint(x: centerX, y: top)
        switch (adPosition) {
        case BilUtilsCordova.TopCenter:
            center = CGPoint(x: centerX, y: top)
            break;
        case BilUtilsCordova.TopLeft:
            center = CGPoint(x: left, y: top);
            break;
        case BilUtilsCordova.TopRight:
            center = CGPoint(x: right, y: top);
            break;
        case BilUtilsCordova.BottomCenter:
            center = CGPoint(x: centerX, y: bottom);
            break;
        case BilUtilsCordova.BottomLeft:
            center = CGPoint(x: left, y: bottom);
            break;
        case BilUtilsCordova.BottomRight:
            center = CGPoint(x: right, y: bottom);
            break;
        case BilUtilsCordova.Center:
            center = CGPoint(x: centerX, y: centerY);
            break;
        default:
            break;
        }
        view.center = center;
    }
    
    // MARK: - Banner Delegate
    public func bannerDidReceiveAd() {
        self.setupPosition(positionView: banner.getADView(), inParentView: self.getUIViewController().view, adPosition: self.adPosition)
        self.fireEventToCordova(self.BANNER_EVENT, data: self.getMess(adType: BilUtilsCordova.bannerType, event: BilUtilsCordova.loaded, mess: ""))
    }
    
    public func bannerWillPresentScreen() {
        self.fireEventToCordova(self.BANNER_EVENT, data: self.getMess(adType: BilUtilsCordova.bannerType, event: BilUtilsCordova.opened, mess: ""))
    }
    
    public func bannerWillDismissScreen() {}
    
    public func bannerDidDismissScreen() {
        self.fireEventToCordova( self.BANNER_EVENT, data: self.getMess(adType: BilUtilsCordova.bannerType, event: BilUtilsCordova.closed, mess: ""))
    }
    
    public func bannerWillLeaveApplication() {
        self.fireEventToCordova( self.BANNER_EVENT, data: self.getMess(adType: BilUtilsCordova.bannerType, event: BilUtilsCordova.clicked, mess: ""))
        self.fireEventToCordova( self.BANNER_EVENT, data: self.getMess(adType: BilUtilsCordova.bannerType, event: BilUtilsCordova.leftApplication, mess: ""))
    }
    
    public func bannerLoadFail(error: String) {
        self.fireEventToCordova( self.BANNER_EVENT, data: self.getMess(adType: BilUtilsCordova.bannerType, event: BilUtilsCordova.failedToLoad, mess: error))
    }
    
    // MARK: - INTERSTITIAL
    private var interstitial: ADInterstitial!
    private let INTERSTITIAL_EVENT = "BilmobileAdsPluginEvent"
    
    @objc(createInterstitial:)
    func createInterstitial(_ command: CDVInvokedUrlCommand) {
        let adUnitId = command.arguments[0] as? String ?? ""
        if adUnitId.isEmpty {
            BilUtilsCordova.log(mess: "adUnitId expected an string value and not empty")
            self.sendMessErr(msg: "adUnitId expected an string value and not empty", command)
            return
        }
        
        if self.interstitial != nil {
            BilUtilsCordova.log(mess: "AdInterstitial already exist")
            self.sendMessErr(msg: "AdInterstitial already exist", command)
            return
        }
        
        DispatchQueue.main.async {
            self.interstitial = ADInterstitial(self.getUIViewController(), placement: adUnitId)
            self.interstitial.setListener(self)
            self.sendMessSucc(msg: "", command)
        }
    }
    
    @objc(preLoadInterstitial:)
    func preLoadInterstitial(_ command: CDVInvokedUrlCommand) {
        if !self.isFullReady(command) { return }
        
        DispatchQueue.main.async {
            if self.interstitial.isReady() {
                BilUtilsCordova.log(mess: "ADInterstitial is ready to show")
                self.sendMessErr(msg: "ADInterstitial is ready to show", command)
            } else {
                self.interstitial.preLoad()
                self.sendMessSucc(msg: "", command)
            }
        }
    }
    
    @objc(showInterstitial:)
    func showInterstitial(_ command: CDVInvokedUrlCommand) {
        if !self.isFullReady(command) { return }
        
        DispatchQueue.main.async {
            if !self.interstitial.isReady() {
                self.sendMessErr(msg: "ADInterstitial currently unavailable, command preload() first", command)
            } else {
                self.interstitial.show()
                self.sendMessSucc(msg: "", command)
            }
        }
    }
    
    @objc(destroyInterstitial:)
    func destroyInterstitial(_ command: CDVInvokedUrlCommand) {
        if !self.isFullReady(command) { return }
        
        self.interstitial.destroy()
        self.interstitial = nil;
        self.sendMessSucc(msg: "", command)
    }
    
    @objc(isReadyInterstitial:)
    func isReadyInterstitial(_ command: CDVInvokedUrlCommand) {
        if !self.isFullReady(command) { return }
        
        self.sendMessSucc(msg: interstitial.isReady() ? "true": "false", command)
    }
    
    func isFullReady(_ command: CDVInvokedUrlCommand) -> Bool {
        if self.interstitial == nil {
            BilUtilsCordova.log(mess: "ADInterstitial is nil. You need init ADInterstitial first")
            self.sendMessErr(msg: "ADInterstitial is nil. You need init ADInterstitial first", command)
            return false
        }
        return true
    }
    
    // MARK: - Interstitial Delegate
    public func interstitialDidReceiveAd() {
        self.fireEventToCordova( self.INTERSTITIAL_EVENT, data: self.getMess(adType: BilUtilsCordova.interstitialType, event: BilUtilsCordova.loaded, mess: ""))
    }
    
    public func interstitialLoadFail(error: String) {
        self.fireEventToCordova( self.INTERSTITIAL_EVENT, data: self.getMess(adType: BilUtilsCordova.interstitialType, event: BilUtilsCordova.failedToLoad, mess: error))
    }
    
    public func interstitialWillPresentScreen() {
        self.fireEventToCordova( self.INTERSTITIAL_EVENT, data: self.getMess(adType: BilUtilsCordova.interstitialType, event: BilUtilsCordova.opened, mess: ""))
    }
    
    public func interstitialDidFailToPresentScreen() {
        self.fireEventToCordova( self.INTERSTITIAL_EVENT, data: self.getMess(adType: BilUtilsCordova.interstitialType, event: BilUtilsCordova.failedToShow, mess: ""))
    }
    
    public func interstitialWillDismissScreen() {}
    
    public func interstitialDidDismissScreen() {
        self.fireEventToCordova( self.INTERSTITIAL_EVENT, data: self.getMess(adType: BilUtilsCordova.interstitialType, event: BilUtilsCordova.closed, mess: ""))
    }
    
    public func interstitialWillLeaveApplication() {
        self.fireEventToCordova( self.INTERSTITIAL_EVENT, data: self.getMess(adType: BilUtilsCordova.interstitialType, event: BilUtilsCordova.clicked, mess: ""))
        self.fireEventToCordova( self.INTERSTITIAL_EVENT, data: self.getMess(adType: BilUtilsCordova.interstitialType, event: BilUtilsCordova.leftApplication, mess: ""))
    }
    
    // MARK: - REWARDED
    private var rewarded: ADRewarded!
    private let REWARDED_EVENT = "BilmobileAdsPluginEvent"
    
    @objc(createRewarded:)
    func createRewarded(_ command: CDVInvokedUrlCommand) {
        let adUnitId = command.arguments[0] as? String ?? ""
        if adUnitId.isEmpty {
            BilUtilsCordova.log(mess: "adUnitId expected an string value and not empty")
            self.sendMessErr(msg: "adUnitId expected an string value and not empty", command)
            return
        }
        
        if self.rewarded != nil {
            BilUtilsCordova.log(mess: "Adrewarded already exist")
            self.sendMessErr(msg: "Adrewarded already exist", command)
            return
        }
        
        DispatchQueue.main.async {
            self.rewarded = ADRewarded(self.getUIViewController(), placement: adUnitId)
            self.rewarded.setListener(self)
            self.sendMessSucc(msg: "", command)
        }
    }
    
    @objc(preLoadRewarded:)
    func preLoadRewarded(_ command: CDVInvokedUrlCommand) {
        if !self.isRewardedReady(command) { return }
        
        DispatchQueue.main.async {
            if self.rewarded.isReady() {
                BilUtilsCordova.log(mess: "ADrewarded is ready to show")
                self.sendMessErr(msg: "ADrewarded is ready to show", command)
            } else {
                self.rewarded.preLoad()
                self.sendMessSucc(msg: "", command)
            }
        }
    }
    
    @objc(showRewarded:)
    func showRewarded(_ command: CDVInvokedUrlCommand) {
        if !self.isRewardedReady(command) { return }
        
        DispatchQueue.main.async {
            if !self.rewarded.isReady() {
                BilUtilsCordova.log(mess: "ADrewarded currently unavailable, command preload() first")
                self.sendMessErr(msg: "ADrewarded currently unavailable, command preload() first", command)
            } else {
                self.rewarded.show()
                self.sendMessSucc(msg: "", command)
            }
        }
    }
    
    @objc(destroyRewarded:)
    func destroyRewarded(_ command: CDVInvokedUrlCommand) {
        if !self.isRewardedReady(command) { return }
        
        self.rewarded.destroy()
        self.rewarded = nil;
        self.sendMessSucc(msg: "", command)
    }
    
    @objc(isReadyRewarded:)
    func isReadyRewarded(_ command: CDVInvokedUrlCommand) {
        if !self.isRewardedReady(command) { return }
        
        self.sendMessSucc(msg: rewarded.isReady() ? "true": "false", command)
    }
    
    func isRewardedReady(_ command: CDVInvokedUrlCommand) -> Bool {
        if self.rewarded == nil {
            BilUtilsCordova.log(mess: "ADrewarded is nil. You need init ADrewarded first")
            self.sendMessErr(msg: "ADrewarded is nil. You need init ADrewarded first", command)
            return false
        }
        return true
    }
    
    // MARK: - Rewarded Delegate
    public func rewardedDidReceiveAd() {
        self.fireEventToCordova( self.REWARDED_EVENT, data: self.getMess(adType: BilUtilsCordova.rewardedType, event: BilUtilsCordova.loaded, mess: ""))
    }
    
    public func rewardedDidPresent() {
        self.fireEventToCordova( self.REWARDED_EVENT, data: self.getMess(adType: BilUtilsCordova.rewardedType, event: BilUtilsCordova.opened, mess: ""))
    }
    
    public func rewardedDidDismiss() {
        self.fireEventToCordova( self.REWARDED_EVENT, data: self.getMess(adType: BilUtilsCordova.rewardedType, event: BilUtilsCordova.closed, mess: ""))
    }
    
    public func rewardedUserDidEarn(rewardedItem: ADRewardedItem) {
        let data = "{\"typeRewarded\": \"\(rewardedItem.getType())\", \"amountRewarded\": \(rewardedItem.getAmount().doubleValue)}"
        self.fireEventToCordova( self.REWARDED_EVENT, data: self.getMess(adType: BilUtilsCordova.rewardedType, event: BilUtilsCordova.rewarded, mess: data))
    }
    
    public func rewardedFailedToLoad(error: String) {
        self.fireEventToCordova( self.REWARDED_EVENT, data: self.getMess(adType: BilUtilsCordova.rewardedType, event: BilUtilsCordova.failedToLoad, mess: error))
    }
    
    public func rewardedFailedToPresent(error: String) {
        self.fireEventToCordova( self.REWARDED_EVENT, data: self.getMess(adType: BilUtilsCordova.rewardedType, event: BilUtilsCordova.failedToShow, mess: error))
    }
    
}
@objc class EventDataType: NSObject, Codable {
    let adType: String
    let event: Int
    let message: String!
    
    init(adType: String, event: Int, message: String?){
        self.adType = adType
        self.event = event
        self.message = message
    }
}