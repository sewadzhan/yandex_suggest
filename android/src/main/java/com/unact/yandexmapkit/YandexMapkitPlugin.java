package com.unact.yandexmapkit;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;

import com.yandex.mapkit.MapKitFactory;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.embedding.engine.plugins.lifecycle.FlutterLifecycleAdapter;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;

public class YandexMapkitPlugin implements FlutterPlugin, ActivityAware {
  private static final String SUGGEST_CHANNEL_ID  = "yandex_mapkit/yandex_suggest";

  @Nullable private Lifecycle lifecycle;
  @Nullable private MethodChannel suggestMethodChannel;

  @Override
  public void onAttachedToEngine(FlutterPluginBinding binding) {
    MapKitFactory.initialize(binding.getApplicationContext());

    BinaryMessenger messenger = binding.getBinaryMessenger();

    setupChannels(messenger, binding.getApplicationContext());
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    teardownChannels();
  }

  private void setupChannels(BinaryMessenger messenger, Context context) {
    suggestMethodChannel = new MethodChannel(messenger, SUGGEST_CHANNEL_ID);
    YandexSuggest yandexSuggest = new YandexSuggest(context, messenger);
    suggestMethodChannel.setMethodCallHandler(yandexSuggest);
  }

  @SuppressWarnings({"ConstantConditions"})
  private void teardownChannels() {
    suggestMethodChannel.setMethodCallHandler(null);
    suggestMethodChannel = null;
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    lifecycle = FlutterLifecycleAdapter.getActivityLifecycle(binding);
    MapKitFactory.getInstance().onStart();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity();
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  @Override
  public void onDetachedFromActivity() {
    lifecycle = null;
    MapKitFactory.getInstance().onStop();
  }

  public class LifecycleProvider {
    @Nullable
    Lifecycle getLifecycle() {
      return lifecycle;
    }
  }
}
