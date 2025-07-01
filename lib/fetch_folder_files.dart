import 'package:flutter/services.dart';
import 'package:folder_permission/folder_permission.dart';

enum FileType { all, image, video }

class FetchFolderFiles {
  /// Define Method Channel
  static const MethodChannel _channel = MethodChannel('fetch_folder_files');

  // Define extensions for each file type
  static const List<String> _imageExtensions = [
    'jpg',
    'jpeg',
    'png',
    'gif',
    'bmp',
    'webp',
    'svg',
    'tiff',
    'ico',
  ];

  static const List<String> _videoExtensions = [
    'mp4',
    'avi',
    'mov',
    'wmv',
    'flv',
    'webm',
    'mkv',
    '3gp',
    'm4v',
  ];

  // Request Permission For Given Path
  static Future<List<Uri>> getFiles({
    required String path,
    required FileType fileType,
  }) async {
    bool isPermitted = await FolderPermission.checkPermission(path: path);

    if (!isPermitted) {
      await FolderPermission.request(path: path);
    }

    final List<Object?> folderFiles = await _channel.invokeMethod(
      'fetch_files',
      {"path": path},
    );

    // Filter based on file type
    final filteredUris = folderFiles
        .where((uri) {
          if (fileType == FileType.all) {
            return true; // Return all files
          }

          final uriString = uri.toString().toLowerCase();
          List<String> extensions;

          switch (fileType) {
            case FileType.image:
              extensions = _imageExtensions;
              break;
            case FileType.video:
              extensions = _videoExtensions;
              break;
            case FileType.all:
            default:
              return true;
          }

          return extensions.any(
            (ext) => uriString.endsWith('.${ext.toLowerCase()}'),
          );
        })
        .map((uri) => Uri.parse(uri.toString()))
        .toList();

    return filteredUris;
  }
}
