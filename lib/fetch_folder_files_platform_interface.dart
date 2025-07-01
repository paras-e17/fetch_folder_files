import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'fetch_folder_files_method_channel.dart';

abstract class FetchFolderFilesPlatform extends PlatformInterface {
  /// Constructs a FetchFolderFilesPlatform.
  FetchFolderFilesPlatform() : super(token: _token);

  static final Object _token = Object();

  static FetchFolderFilesPlatform _instance = MethodChannelFetchFolderFiles();

  /// The default instance of [FetchFolderFilesPlatform] to use.
  ///
  /// Defaults to [MethodChannelFetchFolderFiles].
  static FetchFolderFilesPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FetchFolderFilesPlatform] when
  /// they register themselves.
  static set instance(FetchFolderFilesPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
