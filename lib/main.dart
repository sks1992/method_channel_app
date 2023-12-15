import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:method_channel_test/app_route.dart';
import 'package:method_channel_test/core/bindings/init_binding.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      title: 'Location Tracker App',
      debugShowCheckedModeBanner: false,
      initialRoute: RouteName.locationScreen,
      initialBinding: InitBinding(),
      getPages: AppRoute.route,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
    );
  }
}

