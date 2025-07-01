import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'fetch_folder_files_platform_interface.dart';

/// An implementation of [FetchFolderFilesPlatform] that uses method channels.
class MethodChannelFetchFolderFiles extends FetchFolderFilesPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('fetch_folder_files');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
