import 'package:flutter/services.dart';
import 'package:folder_permission/folder_permission.dart';

class FetchFolderFiles {
  /// Define Method Channel
  static const MethodChannel _channel = MethodChannel('fetch_folder_files');

  // Request Permission For Given Path
  static Future<List<Uri>> getFiles({required String path}) async {
    bool isPermitted = await FolderPermission.checkPermission(path: path);

    if (!isPermitted) {
      await FolderPermission.request(path: path);
    }

    final List<Object?> folderFiles = await _channel.invokeMethod(
      'fetch_files',
      {"path": path},
    );

    final imageUris = folderFiles
        .where(
          (uri) =>
              uri.toString().toLowerCase().endsWith('.jpg') ||
              uri.toString().toLowerCase().endsWith('.mp4'),
        )
        .map((uri) => Uri.parse(uri.toString()))
        .toList();

    return imageUris;
  }
}
