package com.example.rutinapp.viewmodels

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rutinapp.MainActivity
import com.example.rutinapp.data.SECRET_CODE
import com.example.rutinapp.utils.DataStoreManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AdViewModel() : ViewModel() {

    private var isInitiated = false
    get() = field
        private set

    private var mInterstitialAd: InterstitialAd? = null
    private var isInterSitialLoading = false

    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading = false

    private val TAG = "MainActivity"

    @SuppressLint("StaticFieldLeak")
    private lateinit var activity: Activity

    fun initiateObjects(mainActivity: MainActivity, dataStoreManager: DataStoreManager) {
        activity = mainActivity

        viewModelScope.launch(Dispatchers.Main) {

            if (dataStoreManager.getData().first().code != SECRET_CODE) {

                initiateInterstitialAd(activity.baseContext)
                initiateRewardedAd(activity.baseContext)
                isInitiated = true

            }else{
                Toast.makeText(mainActivity.baseContext, "Bienvenido Maestro", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun callmInterstitialAd() {
        viewModelScope.launch(Dispatchers.Main) {
            while (isInterSitialLoading) {
                delay(100)
            }

            if (mInterstitialAd != null) {
                mInterstitialAd?.show(activity)
                Log.d("TAG", "Ad was shown.")
                initiateInterstitialAd(activity.baseContext)
            } else {
                Log.d("TAG", "No ad was available.")
                initiateInterstitialAd(activity.baseContext)
            }
        }
    }

    private fun callRewardedAd() {
        viewModelScope.launch(Dispatchers.Main) {
            while (isRewardedLoading) {
                delay(100)
            }
            if (rewardedAd != null) {
                rewardedAd?.show(activity) {
                    Log.d("TAG", "The user earned the reward.")
                }
                Log.d("TAG", "Ad was shown.")
                initiateRewardedAd(activity.baseContext)

            } else {
                Log.d("TAG", "The rewarded ad wasn't ready yet.")
                initiateRewardedAd(activity.baseContext)
            }
        }
    }

    private fun initiateInterstitialAd(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {

            val adRequest = AdRequest.Builder()

            isInterSitialLoading = true
            InterstitialAd.load(context,
                "ca-app-pub-3940256099942544/1033173712",
                adRequest.build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        Log.d(TAG, p0.toString())
                        isRewardedLoading = false
                        mInterstitialAd = null
                        super.onAdFailedToLoad(p0)
                    }

                    override fun onAdLoaded(p0: InterstitialAd) {

                        Log.d(TAG, "Ad was loaded.")
                        mInterstitialAd = p0
                        isInterSitialLoading = false
                        super.onAdLoaded(p0)
                    }
                })

            while (isInterSitialLoading) {
                delay(100)
            }

            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.")
                    super.onAdClicked()
                }

                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    Log.d(TAG, "Ad dismissed fullscreen content.")
                    mInterstitialAd = null
                    super.onAdDismissedFullScreenContent()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    // Called when ad fails to show.
                    Log.e(TAG, "Ad failed to show fullscreen content.")
                    mInterstitialAd = null
                    super.onAdFailedToShowFullScreenContent(p0)
                }

                override fun onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.")
                    super.onAdImpression()
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.")
                    super.onAdShowedFullScreenContent()
                }
            }
        }
    }

    private fun initiateRewardedAd(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {

            val adRequest = AdRequest.Builder()

            isRewardedLoading = true
            RewardedAd.load(context,
                "ca-app-pub-3940256099942544/5224354917",
                adRequest.build(),
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(TAG, adError.toString())
                        isRewardedLoading = false
                        rewardedAd = null
                    }

                    override fun onAdLoaded(ad: RewardedAd) {
                        Log.d(TAG, "Ad was loaded.")
                        isRewardedLoading = false
                        rewardedAd = ad
                    }
                })

            while (isRewardedLoading) {
                delay(100)
            }

            rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.")
                }

                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad dismissed fullscreen content.")
                    rewardedAd = null
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    // Called when ad fails to show.
                    Log.e(TAG, "Ad failed to show fullscreen content.")
                    rewardedAd = null
                }

                override fun onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.")
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.")
                }
            }

        }

    }

    fun callRandomAd() {
        if (isInitiated)
        if ((0..1).random() == 0) {
            callmInterstitialAd()
        } else {
            callRewardedAd()
        }
    }
}