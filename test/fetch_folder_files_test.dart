// import 'package:flutter_test/flutter_test.dart';
// import 'package:fetch_folder_files/fetch_folder_files.dart';
// import 'package:fetch_folder_files/fetch_folder_files_platform_interface.dart';
// import 'package:fetch_folder_files/fetch_folder_files_method_channel.dart';
// import 'package:plugin_platform_interface/plugin_platform_interface.dart';

// class MockFetchFolderFilesPlatform
//     with MockPlatformInterfaceMixin
//     implements FetchFolderFilesPlatform {

//   @override
//   Future<String?> getPlatformVersion() => Future.value('42');
// }

// void main() {
//   final FetchFolderFilesPlatform initialPlatform = FetchFolderFilesPlatform.instance;

//   test('$MethodChannelFetchFolderFiles is the default instance', () {
//     expect(initialPlatform, isInstanceOf<MethodChannelFetchFolderFiles>());
//   });

//   test('getPlatformVersion', () async {
//     FetchFolderFiles fetchFolderFilesPlugin = FetchFolderFiles();
//     MockFetchFolderFilesPlatform fakePlatform = MockFetchFolderFilesPlatform();
//     FetchFolderFilesPlatform.instance = fakePlatform;

//     expect(await fetchFolderFilesPlugin.getPlatformVersion(), '42');
//   });
// }
