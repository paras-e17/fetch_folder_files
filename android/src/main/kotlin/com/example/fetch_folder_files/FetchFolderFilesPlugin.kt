package com.example.fetch_folder_files

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import androidx.annotation.NonNull
import androidx.documentfile.provider.DocumentFile
import java.io.File

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry

import kotlinx.coroutines.*

/** FolderPermissionPlugin */
class FetchFolderFilesPlugin: FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var context: Context
  private var activity: Activity? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "fetch_folder_files")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "fetch_files" -> {
        var folderPath = call.argument<String>("path")
        try {
          fetchAllStatus(context, folderPath) { documentFiles ->
            result.success(documentFiles.map { file ->
              file.uri.toString()
            })
          }
        } catch (e: Exception) {
          result.error("FETCH_ERROR", e.message, null)
        }
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  private fun getFormattedTime(): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", java.util.Locale.getDefault())
    return sdf.format(java.util.Date())
  }

  @OptIn(DelicateCoroutinesApi::class)
  fun fetchAllStatus(context: Context, folderPath: String?, callback: (ArrayList<DocumentFile>) -> Unit) {
    var documentFiles: DocumentFile?

    GlobalScope.launch(Dispatchers.IO) {
      // Step 1: Fetch the document files based on WhatsApp type and permissions
      Log.d("StatusSaver", "[${getFormattedTime()}] Starting to fetch WhatsApp statuses...")

      val listUriPermission = context.contentResolver.persistedUriPermissions
      documentFiles = listUriPermission.firstOrNull {
        it.uri.path?.contains("$folderPath") == true
      }?.let { uriItem ->
        DocumentFile.fromTreeUri(context, uriItem.uri)
      }
      Log.d("StatusSaver", "[${getFormattedTime()}] Document files found: ${documentFiles != null}")

      // If no document files are found, return an empty list
      if (documentFiles == null) {
        Log.d("StatusSaver", "[${getFormattedTime()}] No document files found, returning empty list")
        withContext(Dispatchers.Main) {
          callback(arrayListOf())  // Return empty list if no files
        }
        return@launch
      }

      Log.d("StatusSaver", "[${getFormattedTime()}] Starting to sort document files...")
      val sortedFilesJob = async(Dispatchers.IO) {
        sortDocumentFiles(documentFiles)
      }
      val sortedFiles = sortedFilesJob.await()
      Log.d("StatusSaver", "[${getFormattedTime()}] Finished sorting files. Total files found: ${sortedFiles.size}")

      callback(ArrayList(sortedFiles))
      Log.d("StatusSaver", "[${getFormattedTime()}] Successfully completed fetching all statuses")
//            // Directly pass the document files without processing
//            val files = documentFiles?.listFiles()?.toList() ?: emptyList()
//            callback(ArrayList(files))
//            Log.d("StatusSaver", "Successfully completed fetching all statuses. Total files: ${files.size}")
    }
  }

  private suspend fun sortDocumentFiles(documentDirectory: DocumentFile?): List<DocumentFile> {
    val metadataReadTasks: List<Deferred<DocumentFileWithMetadata>> =
      withContext(Dispatchers.IO) {
        documentDirectory?.listFiles()?.map { documentFile ->
          async {
            DocumentFileWithMetadata(documentFile)
          }
        } ?: emptyList()
      }
    val metadatas: List<DocumentFileWithMetadata> = metadataReadTasks.awaitAll()
    return metadatas
      .sorted()
      .map {
        it.documentFile
      }
  }

  private class DocumentFileWithMetadata(
    val documentFile: DocumentFile
  ) : Comparable<DocumentFileWithMetadata> {
    private val lastModified = documentFile.lastModified()
    override fun compareTo(other: DocumentFileWithMetadata): Int {
      return other.lastModified.compareTo(this.lastModified)
    }
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
    binding.addActivityResultListener(this)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activity = binding.activity
    binding.addActivityResultListener(this)
  }

  override fun onDetachedFromActivity() {
    activity = null
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    // Handle activity results if needed
    return false
  }
}