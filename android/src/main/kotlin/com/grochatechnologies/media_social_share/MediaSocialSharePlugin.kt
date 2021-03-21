package com.grochatechnologies.media_social_share

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.Profile
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import org.json.JSONArray
import java.io.File
import java.lang.ref.WeakReference
import java.io.FileOutputStream
import java.io.IOException
import android.os.Environment

/** MediaSocialSharePlugin */
class MediaSocialSharePlugin: ActivityAware, FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel

  companion object {
    lateinit var activity: WeakReference<Activity>
  }

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "media_social_share")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "getPlatformVersion" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")
      "getFacebookUser" -> getFacebookUser(result)
      "getFacebookUserPages" -> getFacebookUserPages(result)
      "shareOnFeedFacebook" -> {
        val args = call.arguments as Map<*, *>
        val url: String? = args["url"] as? String?
        val message: String? = args["message"] as? String?
        shareOnFeedFacebook(url, message, result)
      }
      "shareStoryOnInstagram" -> {
        val args = call.arguments as Map<*, *>
        val url: String? = args["url"] as? String?
        shareStoryOnInstagram(url, result)
      }
      "shareStoryOnFacebook" -> {
        val args = call.arguments as Map<*, *>
        val url: String? = args["url"] as? String?
        val facebookId = args["facebookId"] as String
        shareStoryOnFacebook(url, facebookId, result)
      }
      "shareOnFeedInstagram" -> {
        val args = call.arguments as Map<*, *>
        val url: String? = args["url"] as? String?
        val message: String? = args["message"] as? String?
        shareOnFeedInstagram(url, message, result)
      }
      "shareOnWhatsApp" -> {
        val args = call.arguments as Map<*, *>
        val url: String? = args["url"] as? String?
        val message: String? = args["message"] as? String?
        shareOnWhatsApp(url, message, result, false)
      }
      "shareOnWhatsAppBusiness" -> {
        val args = call.arguments as Map<*, *>
        val url: String? = args["url"] as? String?
        val message: String? = args["message"] as? String?
        shareOnWhatsApp(url, message, result, true)
      }
      "openAppOnStore" -> {
        val args = call.arguments as Map<*, *>
        val appUrl: String? = args["appUrl"] as? String?
        openAppOnStore(appUrl)
      }
      "shareOnNative" -> {
        val args = call.arguments as Map<*, *>
        val url: String? = args["url"] as? String?
        val message: String? = args["message"] as? String?
        shareOnNative(url, message, result)
      }
      "shareLinkOnWhatsApp" -> {
        val args = call.arguments as Map<*, *>
        val link: String? = args["link"] as? String?
        shareLinkOnWhatsApp(link, result, false)
      }
      "shareLinkOnWhatsAppBusiness" -> {
        val args = call.arguments as Map<*, *>
        val link: String? = args["link"] as? String?
        shareLinkOnWhatsApp(link, result, true)
      }
      "shareOnGallery" -> {
        val image = call.argument<ByteArray>("imageBytes") ?: return
        val name = call.argument<String>("name")
        shareOnGallery(BitmapFactory.decodeByteArray(image,0,image.size), name)
      }
      "checkPermissionToPublish" -> checkPermissionToPublish(result)
      else -> result.notImplemented()
    }
  }

  private fun getFacebookUser(result: Result) {
    if (AccessToken.getCurrentAccessToken() != null) {
      val parameters = Bundle()
      parameters.putString("fields", "id,name")
      GraphRequest(
              AccessToken.getCurrentAccessToken(),
              "/me",
              parameters,
              HttpMethod.GET
      ) { response ->
        if (response.jsonObject != null) {
          val obj = response.jsonObject
          val map = HashMap<String, String>()
          map["id"] = obj.optString("id")
          map["name"] = obj.optString("name")
          result.success(map)
        } else {
          result.error("FAIL_TO_GET_FB_USER", "Response is null", "FACEBOOK_APP")
        }
      }.executeAsync()
    }
  }

  private fun getFacebookUserPages(result: Result) {
    if (AccessToken.getCurrentAccessToken() != null) {
      val profile = Profile.getCurrentProfile()
      val parameters = Bundle()
      parameters.putString("fields", "id,name,access_token")
      GraphRequest(
              AccessToken.getCurrentAccessToken(),
              "/" + profile.id + "/accounts",
              parameters,
              HttpMethod.GET
      ) { response ->
        if (response.jsonObject != null) {
          val arr = response.jsonObject.get("data") as JSONArray
          val list = List(arr.length()) {
            val map = HashMap<String, String>()
            val obj = arr.getJSONObject(it)
            map["id"] = obj.getString("id")
            map["name"] = obj.getString("name")
            map["access_token"] = obj.getString("access_token")
            map
          }
          result.success(list)
        }
      }.executeAsync()
    }
  }

  private fun shareOnFeedFacebook(url: String?, message: String?, result: Result) {
    try {
      val imgFile = File(url)
      if (imgFile.exists()) {
        val activity = activity.get()!!
        val bitmapUri =
                FileProvider.getUriForFile(activity,
                        "com.example.postouapp.com.postouapp.provider", imgFile)
        val content = if (bitmapUri != null) {
          val photo = SharePhoto.Builder().setCaption("$message #Postou").setImageUrl(bitmapUri).build()
          SharePhotoContent.Builder().addPhoto(photo).build()
        } else {
          ShareLinkContent.Builder().setQuote(message).build()
        }
        val shareDialog = ShareDialog(activity)
        if (ShareDialog.canShow(SharePhotoContent::class.java)) {
          shareDialog.show(content)
          result.success("POST_SENT")
        } else result.error("APP_NOT_FOUND", "Facebook app not found", "FACEBOOK_APP")
      } else {
        result.error("FAIL_TO_POST", "$url not found", "FACEBOOK_APP")
      }
    } catch (e: Exception) {
      result.error("FAIL_TO_POST", e.toString(), "FACEBOOK_APP")
    }
  }

  private fun shareStoryOnInstagram(url: String?, result: Result) {
    try {
      if (isInstalled("com.instagram.android")) {
        val imgFile = File(url)
        if (imgFile.exists()) {
          val activity = activity.get()!!
          val bitmapUri =
                  FileProvider.getUriForFile(activity,
                          "com.example.postouapp.com.postouapp.provider", imgFile)
          val storiesIntent = Intent("com.instagram.share.ADD_TO_STORY")
          storiesIntent.setDataAndType(bitmapUri,
                  activity.contentResolver.getType(bitmapUri))
          storiesIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
          activity.startActivity(storiesIntent)
          result.success("POST_SENT")
        } else {
          result.error("FAIL_TO_POST", "$url not found", "INSTAGRAM_STORY_APP")
        }
      } else {
        result.error("APP_NOT_FOUND", "Instagram app not found", "INSTAGRAM_STORY_APP")
      }
    } catch (e: Exception) {
      result.error("FAIL_TO_POST", e.toString(), "INSTAGRAM_STORY_APP")
    }
  }

  private fun shareStoryOnFacebook(url: String?, facebookId: String, result: Result){

    try {
      if (isInstalled("com.facebook.katana")) {
        val imgFile = File(url)

        if (imgFile.exists()) {
          val activity = activity.get()!!

          val bitmapUri =
                  FileProvider.getUriForFile(activity,
                          "com.example.postouapp.com.postouapp.provider", imgFile)

          val intent = Intent("com.facebook.stories.ADD_TO_STORY")
          intent.type = "image/*"
          intent.putExtra("com.facebook.platform.extra.APPLICATION_ID", facebookId)
          intent.putExtra("interactive_asset_uri", bitmapUri)
          intent.putExtra("top_background_color", "#ffffff")
          intent.putExtra("bottom_background_color", "#ffffff")

          activity.grantUriPermission(
                  "com.facebook.katana", bitmapUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
          if (activity.packageManager.resolveActivity(intent, 0) != null) {
            activity.startActivityForResult(intent, 0)
          }
          result.success("POST_SENT")
        }  else {
          result.error("FAIL_TO_POST", "$url not found", "FACEBOOK_POST_APP")
        }
      } else {
        result.error("APP_NOT_FOUND", "App do Facebook não encontrado", "FACEBOOK_POST_APP")
      }
    } catch (e: Exception) {
      result.error("FAIL_TO_POST", e.toString(), "FACEBOOK_POST_APP")
    }
  }

  private fun shareOnFeedInstagram(url: String?, msg: String?, result: Result) {
    try {
      if (isInstalled("com.instagram.android")) {
        val imgFile = File(url)
        if (imgFile.exists()) {
          val activity = activity.get()!!
          val bitmapUri =
                  FileProvider.getUriForFile(activity,
                          "com.example.postouapp.com.postouapp.provider", imgFile)
          val feedIntent = Intent(Intent.ACTION_SEND)
          feedIntent.type = "image/*"
          feedIntent.putExtra(Intent.EXTRA_TEXT, msg)
          feedIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
          feedIntent.setPackage("com.instagram.android")
          activity.startActivity(feedIntent)
          result.success("POST_SENT")
        } else {
          result.error("FAIL_TO_POST", "$url not found", "INSTAGRAM_POST_APP")
        }
      } else {
        result.error("APP_NOT_FOUND", "App do Instagram não encontrado", "INSTAGRAM_POST_APP")
      }
    } catch (e: Exception) {
      result.error("FAIL_TO_POST", e.toString(), "INSTAGRAM_POST_APP")
    }
  }

  private fun shareOnWhatsApp(url: String?, msg: String?, result: Result,
                              shareToWhatsAppBiz: Boolean) {
    val app = if (shareToWhatsAppBiz) "com.whatsapp.w4b" else "com.whatsapp"
    try {
      if (isInstalled(app)) {
        val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.setPackage(app)
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, msg)
        val imgFile = File(url)
        if (imgFile.exists()) {
          val activity = activity.get()!!
          val bitmapUri =
                  FileProvider.getUriForFile(activity,
                          "com.example.postouapp.com.postouapp.provider", imgFile)
          whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
          whatsappIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
          val cr = activity.contentResolver
          whatsappIntent.type = cr.getType(bitmapUri)
          activity.startActivity(whatsappIntent)
          result.success("POST_SENT")
        } else {
          result.error("FAIL_TO_POST", "$url not found", app)
        }
      } else {
        result.error("APP_NOT_FOUND", "App do WhatsApp não foi encontrado", app)
      }
    } catch (e: Exception) {
      result.error("FAIL_TO_POST", e.toString(), app)
    }
  }

  private fun shareLinkOnWhatsApp(link: String?, result: Result, shareToWhatsAppBiz: Boolean) {
    val app = if (shareToWhatsAppBiz) "com.whatsapp.w4b" else "com.whatsapp"
    if (isInstalled(app)) {
      val intent = Intent(Intent.ACTION_SEND)
      intent.type = "text/plain"
      intent.setPackage(app)
      intent.putExtra(Intent.EXTRA_TEXT, link)
      activity.get()!!.startActivity(intent)
    } else {
      result.error("APP_NOT_FOUND", "App do WhatsApp não foi encontrado", app)
    }
  }

  private fun shareOnNative(url: String?, msg: String?, result: Result) {
    try {
      val activity = activity.get()!!
      val intent = Intent(Intent.ACTION_SEND)
      intent.putExtra(Intent.EXTRA_TEXT, msg)
      if (url != null) {
        val imgFile = File(url)
        if (imgFile.exists()) {
          val bitmapUri =
                  FileProvider.getUriForFile(activity,
                          "com.example.postouapp.com.postouapp.provider", imgFile)
          intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
          intent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
          val cr = activity.contentResolver
          intent.type = cr.getType(bitmapUri)
        } else {
          result.error("FAIL_TO_POST", "$url not found", "NATIVE")
        }
        activity.startActivity(Intent.createChooser(intent, "Enviar post..."))
      } else {
        intent.type = "text/plain"
        activity.startActivity(Intent.createChooser(intent, "Enviar mensagem..."))
      }


      result.success("POST_SENT")

    } catch (e: Exception) {
      result.error("FAIL_TO_POST", e.toString(), "NATIVE")
    }
  }

  private fun checkPermissionToPublish(result: Result) {
    result.success(AccessToken.getCurrentAccessToken() != null)
  }

  private fun shareLinkOnFacebook(link: String?, result: Result) {
    try {
      val content = ShareLinkContent.Builder()
              .setContentUrl(Uri.parse(link))
              .build()
      val shareDialog = ShareDialog(activity.get()!!)
      if (ShareDialog.canShow(ShareLinkContent::class.java)) {
        shareDialog.show(content)
        result.success("POST_SENT")
      } else result.error("APP_NOT_FOUND", "App do Facebook não foi encontrado", "FACEBOOK_APP")
    } catch (e: Exception) {
      result.error("FAIL_TO_POST", e.toString(), "FACEBOOK_APP")
    }
  }

  private fun openAppOnStore(packageName: String?) {
    val context = activity.get()!!.applicationContext
    try {
      val playStoreUri = Uri.parse("market://details?id=$packageName")
      val intent = Intent(Intent.ACTION_VIEW, playStoreUri)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
      val playStoreUri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
      val intent = Intent(Intent.ACTION_VIEW, playStoreUri)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(intent)
    }
  }

  private fun isInstalled(packageName: String): Boolean {
    val packageManager = activity.get()!!.packageManager
    return try {
      packageManager.getApplicationInfo(packageName, 0).enabled
    } catch (e: PackageManager.NameNotFoundException) {
      false
    }
  }

  private fun shareOnGallery(bmp: Bitmap, name: String?): HashMap<String, Any?> {
    val context = activity.get()!!
    val file = generateFile("jpg", name = name)
    return try {
      val fos = FileOutputStream(file)
      println("Imagem salva com sucesso na galeria")
      bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)
      fos.flush()
      fos.close()
      val uri = Uri.fromFile(file)

      MediaScannerConnection.scanFile(context, arrayOf(file.toString()),
              null, null)
      bmp.recycle()
      SaveResultModel(uri.toString().isNotEmpty(), uri.toString(), null).toHashMap()
    } catch (e: IOException) {
      SaveResultModel(false, null, e.toString()).toHashMap()
    }
  }

  private fun generateFile(extension: String = "", name: String? = null): File {
    val context = activity.get()!!

    val storePath =  context.getExternalFilesDir(null)?.absolutePath + File.separator + Environment.DIRECTORY_PICTURES
    val appDir = File(storePath)
    if (!appDir.exists()) {
      appDir.mkdir()
    }
    var fileName = name?:System.currentTimeMillis().toString()
    if (extension.isNotEmpty()) {
      fileName += (".$extension")
    }
    return File(appDir, fileName)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = WeakReference(binding.activity)
  }

  override fun onDetachedFromActivityForConfigChanges() {

  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {

  }

  override fun onDetachedFromActivity() {

  }
}

class SaveResultModel (var isSuccess: Boolean,
                       var filePath: String? = null,
                       var errorMessage: String? = null) {
  fun toHashMap(): HashMap<String, Any?> {
    val hashMap = HashMap<String, Any?>()
    hashMap["isSuccess"] = isSuccess
    hashMap["filePath"] = filePath
    hashMap["errorMessage"] = errorMessage
    return hashMap
  }
}