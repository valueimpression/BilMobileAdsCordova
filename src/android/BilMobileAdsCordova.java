package cordova.plugin.bilmobileads;

import android.graphics.Color;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bil.bilmobileads.ADBanner;
import com.bil.bilmobileads.ADInterstitial;
import com.bil.bilmobileads.ADRewarded;
import com.bil.bilmobileads.PBMobileAds;
import com.bil.bilmobileads.entity.ADRewardItem;
import com.bil.bilmobileads.interfaces.AdDelegate;
import com.bil.bilmobileads.interfaces.AdRewardedDelegate;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class BilMobileAdsCordova extends CordovaPlugin {

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
  }

  private void fireEventToCordova(String eventName, String jsonData) {
    StringBuilder js = new StringBuilder();
    js.append("javascript:cordova.fireDocumentEvent('");
    js.append(eventName);
    js.append("'");
    if (jsonData != null && !"".equals(jsonData)) {
      js.append(",");
      js.append(jsonData);
    }
    js.append(");");

    this.webView.loadUrl(js.toString());
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    // PBM
    if (action.equals("initialize")) {
      boolean testMode = args.getBoolean(0);
      this.initialize(testMode, callbackContext);
      return true;
    } else if (action.equals("enableCOPPA")) {
      if (!isInitSucc) {
        BilUtilsCordova.log("PBMobileAds uninitialized, please call PBMobileAds.initialize() first.");
        callbackContext.error("PBMobileAds uninitialized, please call PBMobileAds.initialize() first.");
        return false;
      }

      PBMobileAds.instance.enableCOPPA();
      callbackContext.success();
      return true;
    } else if (action.equals("disableCOPPA")) {
      if (!isInitSucc) {
        BilUtilsCordova.log("PBMobileAds uninitialized, please call PBMobileAds.initialize() first.");
        callbackContext.error("PBMobileAds uninitialized, please call PBMobileAds.initialize() first.");
        return false;
      }

      PBMobileAds.instance.disableCOPPA();
      callbackContext.success();
      return true;
    } else if (action.equals("setYearOfBirth")) {
      if (!isInitSucc) {
        BilUtilsCordova.log("PBMobileAds uninitialized, please call PBMobileAds.initialize() first.");
        callbackContext.error("PBMobileAds uninitialized, please call PBMobileAds.initialize() first.");
        return false;
      }

      int yob = args.getInt(0);
      if (yob > 0) {
        PBMobileAds.instance.setYearOfBirth(yob);
        callbackContext.success();
      } else {
        callbackContext.error("Year of birth must be integer and greater than 0.");
      }
      return true;
    } else if (action.equals("setGender")) {
      if (!isInitSucc) {
        BilUtilsCordova.log("PBMobileAds uninitialized, please call PBMobileAds.initialize() first.");
        callbackContext.error("PBMobileAds uninitialized, please call PBMobileAds.initialize() first.");
        return false;
      }

      int gen = args.getInt(0);
      if (gen > 0) {
        switch (gen) {
          case 0:
            PBMobileAds.instance.setGender(PBMobileAds.GENDER.UNKNOWN);
            break;
          case 1:
            PBMobileAds.instance.setGender(PBMobileAds.GENDER.MALE);
            break;
          case 2:
            PBMobileAds.instance.setGender(PBMobileAds.GENDER.FEMALE);
            break;
        }
        callbackContext.success();
      } else {
        callbackContext.error("Year of birth must be integer and greater than 0.");
      }
      return true;
    }
    // BANNER
    else if (action.equals("createBanner")) {
      this.createBanner(args, callbackContext);
      return true;
    } else if (action.equals("loadBanner")) {
      this.loadBanner(args, callbackContext);
      return true;
    } else if (action.equals("showBanner")) {
      this.showBanner(args, callbackContext);
      return true;
    } else if (action.equals("hideBanner")) {
      this.hideBanner(args, callbackContext);
      return true;
    } else if (action.equals("isReadyBanner")) {
      this.isReadyBanner(args, callbackContext);
      return true;
    } else if (action.equals("setPositionBanner")) {
      this.setPositionBanner(args, callbackContext);
      return true;
    } else if (action.equals("getSafeArea")) {
      this.getSafeArea(args, callbackContext);
      return true;
    } else if (action.equals("destroyBanner")) {
      this.destroyBanner(args, callbackContext);
      return true;
    }
    // INTERSTITAL
    else if (action.equals("createInterstitial")) {
      this.createInterstitial(args, callbackContext);
      return true;
    } else if (action.equals("preLoadInterstitial")) {
      this.preLoadInterstitial(args, callbackContext);
      return true;
    } else if (action.equals("showInterstitial")) {
      this.showInterstitial(args, callbackContext);
      return true;
    } else if (action.equals("destroyInterstitial")) {
      this.destroyInterstitial(args, callbackContext);
      return true;
    } else if (action.equals("isReadyInterstitial")) {
      this.isReadyInterstitial(args, callbackContext);
      return true;
    }
    // REWARDED
    else if (action.equals("createRewarded")) {
      this.createRewarded(args, callbackContext);
      return true;
    } else if (action.equals("preLoadRewarded")) {
      this.preLoadRewarded(args, callbackContext);
      return true;
    } else if (action.equals("showRewarded")) {
      this.showRewarded(args, callbackContext);
      return true;
    } else if (action.equals("destroyRewarded")) {
      this.destroyRewarded(args, callbackContext);
      return true;
    } else if (action.equals("isReadyRewarded")) {
      this.isReadyRewarded(args, callbackContext);
      return true;
    }
    return false;
  }

  static boolean isInitSucc = false;

  /**
   * PBM
   */
  private void initialize(boolean testMode, CallbackContext callbackContext) {
    this.cordova.getActivity().runOnUiThread(() -> {
      PBMobileAds.instance.initialize(this.cordova.getActivity(), testMode);
      callbackContext.success("PBM initialize success");
      this.isInitSucc = true;
    });
  }

  String getMess(String adType, int type, String mess) {
    try {
      JSONObject data = new JSONObject();
      data.put("adType", adType);
      data.put("event", type);
      data.put("message", mess);
      return data.toString();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return "";
  }

  /**
   * BANNER
   */
  private final String BANNER_EVENT = "BilmobileAdsPluginEvent";

  private ADBanner adBanner;
  private RelativeLayout adPlaceholder;
  private int adPosition = BilUtilsCordova.BottomCenter;

  /**
   * A boolean indicating whether the ad has been hidden.
   * true -> ad is off, false -> ad is on screen.
   */
  private boolean isHidden = false;

  public void createBanner(JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (!isInitSucc) {
      BilUtilsCordova.log("PBMobileAds uninitialized, please call PBMobileAds.initialize() first.");
      callbackContext.error("PBMobileAds uninitialized, please call PBMobileAds.initialize() first.");
      return;
    }

    if (this.adBanner != null) {
      BilUtilsCordova.log("ADBanner already exist");
      callbackContext.error("ADBanner already exist");
      return;
    }

    String adUnitId = args.getString(0);
    if (adUnitId == null || adUnitId == "") {
      BilUtilsCordova.log("adUnitId expected an string value and not empty");
      callbackContext.error("adUnitId expected an string value and not empty");
      return;
    }

    int position = args.getInt(1);
    this.cordova.getActivity().runOnUiThread(() -> {
      this.isHidden = false;
      this.adPosition = position;

      createADPlaceholder();
      this.cordova.getActivity().addContentView(adPlaceholder,
        new LinearLayout.LayoutParams(
          FrameLayout.LayoutParams.WRAP_CONTENT,
          FrameLayout.LayoutParams.WRAP_CONTENT));

      this.adBanner = new ADBanner(adPlaceholder, adUnitId);
      this.adBanner.setListener(new AdDelegate() {
        @Override
        public void onAdLoaded() {
          updateADPosition();
          fireEventToCordova(BANNER_EVENT, getMess(BilUtilsCordova.BannerType, BilUtilsCordova.loaded, null));
        }

        @Override
        public void onAdOpened() {
          fireEventToCordova(BANNER_EVENT, getMess(BilUtilsCordova.BannerType, BilUtilsCordova.opened, null));
        }

        @Override
        public void onAdClosed() {
          fireEventToCordova(BANNER_EVENT, getMess(BilUtilsCordova.BannerType, BilUtilsCordova.closed, null));
        }

        @Override
        public void onAdClicked() {
          fireEventToCordova(BANNER_EVENT, getMess(BilUtilsCordova.BannerType, BilUtilsCordova.clicked, null));
        }

        @Override
        public void onAdLeftApplication() {
          fireEventToCordova(BANNER_EVENT, getMess(BilUtilsCordova.BannerType, BilUtilsCordova.leftApplication, null));
        }

        @Override
        public void onAdFailedToLoad(final String errorCode) {
          fireEventToCordova(BANNER_EVENT, getMess(BilUtilsCordova.BannerType, BilUtilsCordova.failedToLoad, errorCode));
        }
      });
      callbackContext.success();
    });
  }

  public void loadBanner(JSONArray args, CallbackContext callbackContext) {
    if (!this.isPluginReady(callbackContext)) return;

    this.cordova.getActivity().runOnUiThread(() -> {
      if (!this.adBanner.isLoaded()) {
        this.adBanner.load();
        callbackContext.success();
      }
    });
  }

  public void showBanner(JSONArray args, CallbackContext callbackContext) {
    if (!this.isPluginReady(callbackContext)) return;

    this.cordova.getActivity().runOnUiThread(() -> {
      this.isHidden = false;
      this.adPlaceholder.setVisibility(View.VISIBLE);
      this.adBanner.startFetchData();
      this.updateADPosition();
      callbackContext.success();
    });
  }

  public void hideBanner(JSONArray args, CallbackContext callbackContext) {
    if (!this.isPluginReady(callbackContext)) return;

    this.cordova.getActivity().runOnUiThread(() -> {
      this.isHidden = true;
      this.adPlaceholder.setVisibility(View.GONE);
      this.adBanner.stopFetchData();
      callbackContext.success();
    });
  }

  public void destroyBanner(JSONArray args, CallbackContext callbackContext) {
    if (!this.isPluginReady(callbackContext)) return;

    this.cordova.getActivity().runOnUiThread(() -> {
      adPlaceholder.removeAllViews();
      adPlaceholder.setVisibility(View.GONE);

      this.adBanner.destroy();
      this.adBanner = null;

      //            this.cordova.getActivity().getWindow()
      //                    .getDecorView()
      //                    .getRootView()
      //                    .removeOnLayoutChangeListener(viewChangeListener);

      callbackContext.success();
    });
  }

  public void setPositionBanner(JSONArray args, CallbackContext callbackContext) {
    if (!this.isPluginReady(callbackContext)) return;

    this.cordova.getActivity().runOnUiThread(() -> {
      try {
        this.adPosition = args.getInt(0);
        this.updateADPosition();
        callbackContext.success();
      } catch (JSONException e) {
        callbackContext.error(e.getLocalizedMessage());
      }
    });
  }

  public void getSafeArea(JSONArray args, CallbackContext callbackContext) {
    if (!this.isPluginReady(callbackContext)) return;

    this.cordova.getActivity().runOnUiThread(() -> {
      try {
        Insets insets = getSafeInsets();
        int bannerH = this.adBanner.getHeight() > 0 ? this.adBanner.getHeight() : 50;

        JSONObject data = new JSONObject();
        data.put("topPadding", bannerH + insets.top);
        data.put("bottomPadding", bannerH + insets.bottom);

        String dataStr = "{\"topPadding\": " + bannerH + insets.top + ", \"bottomPadding\": " + bannerH + insets.bottom + "}";
        callbackContext.success(dataStr);
      } catch (JSONException e) {
        callbackContext.error(e.getLocalizedMessage());
      }
    });
  }

  public void isReadyBanner(JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (!this.isPluginReady(callbackContext)) return;

    JSONObject data = new JSONObject();
    data.put("isReady", this.adBanner.isLoaded());
    callbackContext.success(data);
  }

  boolean isPluginReady(CallbackContext callbackContext) {
    if (this.adBanner == null) {
      BilUtilsCordova.log("ADBanner is nil. You need init ADBanner first.");
      callbackContext.error("ADBanner is nil. You need init ADBanner first.");
      return false;
    }
    return true;
  }

  private void createADPlaceholder() {
    // Create a RelativeLayout and add the ad view to it
    if (this.adPlaceholder == null) {
      this.adPlaceholder = new RelativeLayout(this.cordova.getActivity());
    } else {
      // Remove the layout if it has a parent
      FrameLayout parentView = (FrameLayout) this.adPlaceholder.getParent();
      if (parentView != null)
        parentView.removeView(this.adPlaceholder);
    }
    this.adPlaceholder.setBackgroundColor(Color.TRANSPARENT);
    this.adPlaceholder.setVisibility(View.VISIBLE);

    this.updateADPosition();
  }

  private void updateADPosition() {
    if (adPlaceholder == null || isHidden) return;

    this.cordova.getActivity().runOnUiThread(() -> {
      adPlaceholder.setLayoutParams(getLayoutParams());
    });
  }

  private FrameLayout.LayoutParams getLayoutParams() {
    Insets insets = getSafeInsets();

    final FrameLayout.LayoutParams adParams = new FrameLayout.LayoutParams(
      FrameLayout.LayoutParams.WRAP_CONTENT,
      FrameLayout.LayoutParams.WRAP_CONTENT);
    adParams.gravity = BilUtilsCordova.getGravityForPositionCode(this.adPosition);
    adParams.bottomMargin = insets.bottom;
    adParams.rightMargin = insets.right;
    adParams.leftMargin = insets.left;
    if (this.adPosition == BilUtilsCordova.TopCenter || this.adPosition == BilUtilsCordova.TopLeft || this.adPosition == BilUtilsCordova.TopRight) {
      adParams.topMargin = insets.top;
    }

    return adParams;
  }

  /**
   * Class to hold the insets of the cutout area.
   */
  private static class Insets {
    int top = 0;
    int bottom = 0;
    int left = 0;
    int right = 0;
  }

  private Insets getSafeInsets() {
    Insets insets = new Insets();

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return insets;

    Window window = this.cordova.getActivity().getWindow();
    if (window == null) return insets;

    WindowInsets windowInsets = window.getDecorView().getRootWindowInsets();
    if (windowInsets == null) return insets;

    DisplayCutout displayCutout = windowInsets.getDisplayCutout();
    if (displayCutout == null) return insets;

    insets.top = displayCutout.getSafeInsetTop();
    insets.left = displayCutout.getSafeInsetLeft();
    insets.bottom = displayCutout.getSafeInsetBottom();
    insets.right = displayCutout.getSafeInsetRight();

    return insets;
  }

  /**
   * INTERSTITIAL
   */
  private final String INTERSTITIAL_EVENT = "BilmobileAdsPluginEvent";
  private ADInterstitial adInterstitial;

  public void createInterstitial(JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (!isInitSucc) {
      BilUtilsCordova.log("PBMobileAds uninitialized, please call PBMobileAds.initialize() first.");
      callbackContext.error("PBMobileAds uninitialized, please call PBMobileAds.initialize() first.");
      return;
    }

    String adUnitId = args.getString(0);
    if (adUnitId == null || adUnitId == "") {
      BilUtilsCordova.log("adUnitId expected an string value and not empty");
      callbackContext.error("adUnitId expected an string value and not empty");
      return;
    }

    if (this.adInterstitial != null) {
      BilUtilsCordova.log("AdInterstitial already exist");
      callbackContext.error("ADRewarded already exist");
      return;
    }

    this.cordova.getActivity().runOnUiThread(() -> {
      this.adInterstitial = new ADInterstitial(adUnitId);
      this.adInterstitial.setListener(new AdDelegate() {
        @Override
        public void onAdLoaded() {
          fireEventToCordova(INTERSTITIAL_EVENT, getMess(BilUtilsCordova.InterstitialType, BilUtilsCordova.loaded, null));
        }

        @Override
        public void onAdOpened() {
          fireEventToCordova(INTERSTITIAL_EVENT, getMess(BilUtilsCordova.InterstitialType, BilUtilsCordova.opened, null));
        }

        @Override
        public void onAdClosed() {
          fireEventToCordova(INTERSTITIAL_EVENT, getMess(BilUtilsCordova.InterstitialType, BilUtilsCordova.closed, null));
        }

        @Override
        public void onAdClicked() {
          fireEventToCordova(INTERSTITIAL_EVENT, getMess(BilUtilsCordova.InterstitialType, BilUtilsCordova.clicked, null));
        }

        @Override
        public void onAdLeftApplication() {
          fireEventToCordova(INTERSTITIAL_EVENT, getMess(BilUtilsCordova.InterstitialType, BilUtilsCordova.leftApplication, null));
        }

        @Override
        public void onAdFailedToLoad(final String errorCode) {
          fireEventToCordova(INTERSTITIAL_EVENT, getMess(BilUtilsCordova.InterstitialType, BilUtilsCordova.failedToLoad, errorCode));
        }
      });
      callbackContext.success();
    });
  }

  public void preLoadInterstitial(JSONArray args, CallbackContext callbackContext) {
    if (!this.isFullReady(callbackContext)) return;

    this.cordova.getActivity().runOnUiThread(() -> {
      if (this.adInterstitial.isReady()) {
        BilUtilsCordova.log("ADInterstitial is ready to show.");
        callbackContext.error("ADInterstitial is ready to show.");
      } else {
        this.adInterstitial.preLoad();
        callbackContext.success();
      }
    });
  }

  public void showInterstitial(JSONArray args, CallbackContext callbackContext) {
    if (!this.isFullReady(callbackContext)) return;

    this.cordova.getActivity().runOnUiThread(() -> {
      if (!this.adInterstitial.isReady()) {
        BilUtilsCordova.log("ADInterstitial currently unavailable, call preload() first.");
        callbackContext.error("ADInterstitial currently unavailable, call preload() first.");
      } else {
        this.adInterstitial.show();
        callbackContext.success();
      }
    });
  }

  public void destroyInterstitial(JSONArray args, CallbackContext callbackContext) {
    if (!this.isFullReady(callbackContext)) return;

    this.cordova.getActivity().runOnUiThread(() -> {
      this.adInterstitial.destroy();
      this.adInterstitial = null;
      callbackContext.success();
    });
  }

  public void isReadyInterstitial(JSONArray args, CallbackContext callbackContext) {
    if (!this.isFullReady(callbackContext)) return;

    this.cordova.getActivity().runOnUiThread(() -> {
      try {
        JSONObject data = new JSONObject();
        data.put("isReady", this.adInterstitial.isReady());
        callbackContext.success(data);
      } catch (JSONException e) {
        callbackContext.error(e.getLocalizedMessage());
      }
    });
  }

  boolean isFullReady(CallbackContext callbackContext) {
    if (this.adInterstitial == null) {
      BilUtilsCordova.log("ADInterstitial is nil. You need init ADInterstitial first.");
      callbackContext.error("ADInterstitial is nil. You need init ADInterstitial first");
      return false;
    }
    return true;
  }

  /**
   * REWARDED
   */
  private final String REWARDED_EVENT = "BilmobileAdsPluginEvent";
  private ADRewarded adRewarded;

  public void createRewarded(JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (!isInitSucc) {
      BilUtilsCordova.log("PBMobileAds uninitialized, please call PBMobileAds.initialize() first.");
      callbackContext.error("PBMobileAds uninitialized, please call PBMobileAds.initialize() first.");
      return;
    }

    String adUnitId = args.getString(0);
    if (adUnitId == null || adUnitId == "") {
      BilUtilsCordova.log("adUnitId expected an string value and not empty");
      callbackContext.error("adUnitId expected an string value and not empty");
      return;
    }

    if (this.adRewarded != null) {
      BilUtilsCordova.log("ADRewarded already exist");
      callbackContext.error("ADRewarded already exist");
      return;
    }

    this.cordova.getActivity().runOnUiThread(() -> {
      this.adRewarded = new ADRewarded(this.cordova.getActivity(), adUnitId);
      this.adRewarded.setListener(new AdRewardedDelegate() {
        @Override
        public void onRewardedAdLoaded() {
          fireEventToCordova(REWARDED_EVENT, getMess(BilUtilsCordova.RewardedType, BilUtilsCordova.loaded, null));
        }

        @Override
        public void onRewardedAdOpened() {
          fireEventToCordova(REWARDED_EVENT, getMess(BilUtilsCordova.RewardedType, BilUtilsCordova.opened, null));
        }

        @Override
        public void onRewardedAdClosed() {
          fireEventToCordova(REWARDED_EVENT, getMess(BilUtilsCordova.RewardedType, BilUtilsCordova.closed, null));
        }

        @Override
        public void onUserEarnedReward(final ADRewardItem adRewardItem) {
          try {
            JSONObject info = new JSONObject();
            info.put("typeRewarded", adRewardItem.getType());
            info.put("amountRewarded", adRewardItem.getAmount());

            String data = "{\"typeRewarded\": \"" + adRewardItem.getType() + "\", \"amountRewarded\": " + adRewardItem.getAmount() + "}";
            fireEventToCordova(REWARDED_EVENT, getMess(BilUtilsCordova.RewardedType, BilUtilsCordova.rewarded, data));
          } catch (JSONException e) {
            callbackContext.error(e.getLocalizedMessage());
          }
        }

        @Override
        public void onRewardedAdFailedToLoad(final String error) {
          fireEventToCordova(REWARDED_EVENT, getMess(BilUtilsCordova.RewardedType, BilUtilsCordova.failedToLoad, null));
        }

        @Override
        public void onRewardedAdFailedToShow(final String error) {
          fireEventToCordova(REWARDED_EVENT, getMess(BilUtilsCordova.RewardedType, BilUtilsCordova.failedToShow, null));
        }
      });
      callbackContext.success();
    });
  }

  public void preLoadRewarded(JSONArray args, CallbackContext callbackContext) {
    if (!this.isRewardedReady(callbackContext)) return;

    this.cordova.getActivity().runOnUiThread(() -> {
      if (this.adRewarded.isReady()) {
        BilUtilsCordova.log("ADRewarded is ready to show.");
        callbackContext.error("ADRewarded is ready to show.");
      } else {
        this.adRewarded.preLoad();
        callbackContext.success();
      }

    });
  }

  public void showRewarded(JSONArray args, CallbackContext callbackContext) {
    if (!this.isRewardedReady(callbackContext)) return;

    this.cordova.getActivity().runOnUiThread(() -> {
      if (!this.adRewarded.isReady()) {
        BilUtilsCordova.log("ADRewarded currently unavailable, call preload() first.");
        callbackContext.error("ADRewarded currently unavailable, call preload() first.");
      } else {
        this.adRewarded.show();
        callbackContext.success();
      }
    });
  }

  public void destroyRewarded(JSONArray args, CallbackContext callbackContext) {
    if (!this.isRewardedReady(callbackContext)) return;

    this.cordova.getActivity().runOnUiThread(() -> {
      this.adRewarded.destroy();
      this.adRewarded = null;
      callbackContext.success();
    });
  }

  public void isReadyRewarded(JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (!this.isRewardedReady(callbackContext)) return;

    this.cordova.getActivity().runOnUiThread(() -> {
      try {
        JSONObject data = new JSONObject();
        data.put("isReady", this.adRewarded.isReady());
        callbackContext.success(data);
      } catch (JSONException e) {
        callbackContext.error(e.getLocalizedMessage());
      }
    });
  }

  boolean isRewardedReady(CallbackContext callbackContext) {
    if (this.adRewarded == null) {
      BilUtilsCordova.log("ADRewarded is nil. You need init ADRewarded first.");
      callbackContext.error("ADRewarded is nil. You need init ADRewarded first.");
      return false;
    }
    return true;
  }
}
