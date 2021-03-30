var exec = require('cordova/exec');

// This just makes it easier for us to export all of the functions at once.
var BilMobileAdsCordova = function () { };

// AdType {
//     Banner = "Banner",
//     Interstitial = "Interstitial",
//     Rewarded = "Rewarded"
// }

// AdPosition {
//     TopCenter = 0,
//     TopLeft = 1,
//     TopRight = 2,
//     BottomCenter = 3,
//     BottomLeft = 4,
//     BottomRight = 5,
//     Center = 6
// }

// BilAdEvents {
//     loaded = 0,
//     opened = 1,
//     closed = 2,
//     clicked = 3,
//     leftApplication = 4,
//     rewarded = 5,
//     failedToLoad = 6,
//     failedToShow = 7
// }
// BilGender {
//     Unknown = 0,
//     Male = 1,
//     Female = 2
// }

// PBM
BilMobileAdsCordova.initialize = function (testMode, onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "initialize", [testMode]);
};
BilMobileAdsCordova.enableCOPPA = function () {
    exec(null, null, "BilMobileAdsCordova", "enableCOPPA", []);
};
BilMobileAdsCordova.disableCOPPA = function () {
    exec(null, null, "BilMobileAdsCordova", "disableCOPPA", []);
};
BilMobileAdsCordova.setYearOfBirth = function (yob, onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "setYearOfBirth", [yob]);
};
BilMobileAdsCordova.setGender = function (gender, onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "setGender", [gender]);
};

// BANNER
BilMobileAdsCordova.createBanner = function (adUnitId, position, onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "createBanner", [adUnitId, position]);
};
BilMobileAdsCordova.loadBanner = function (onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "loadBanner", []);
};
BilMobileAdsCordova.showBanner = function (onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "showBanner", []);
};
BilMobileAdsCordova.hideBanner = function (onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "hideBanner", []);
};
BilMobileAdsCordova.destroyBanner = function (onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "destroyBanner", []);
};
BilMobileAdsCordova.isReadyBanner = function (onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "isReadyBanner", []);
};
BilMobileAdsCordova.setPositionBanner = function (position, onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "setPositionBanner", [position]);
};
BilMobileAdsCordova.getSafeArea = function (onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "getSafeArea", []);
};

// INTERSTITIAL
BilMobileAdsCordova.createInterstitial = function (adUnitId, onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "createInterstitial", [adUnitId]);
};
BilMobileAdsCordova.preLoadInterstitial = function (onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "preLoadInterstitial", []);
};
BilMobileAdsCordova.showInterstitial = function (onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "showInterstitial", []);
};
BilMobileAdsCordova.destroyInterstitial = function (onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "destroyInterstitial", []);
};
BilMobileAdsCordova.isReadyInterstitial = function (onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "isReadyInterstitial", []);
};

// REWARDED
BilMobileAdsCordova.createRewarded = function (adUnitId, onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "createRewarded", [adUnitId]);
};
BilMobileAdsCordova.preLoadRewarded = function (onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "preLoadRewarded", []);
};
BilMobileAdsCordova.showRewarded = function (onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "showRewarded", []);
};
BilMobileAdsCordova.destroyRewarded = function (onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "destroyRewarded", []);
};
BilMobileAdsCordova.isReadyRewarded = function (onSuccess, onError) {
    exec(onSuccess, onError, "BilMobileAdsCordova", "isReadyRewarded", []);
};

module.exports = BilMobileAdsCordova;