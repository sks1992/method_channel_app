import 'package:get/get.dart';
import 'package:method_channel_test/ui/screens/location%20_screen.dart';

class RouteName {
  static String locationScreen = "/locationScreen";
}

class AppRoute {
  static final route = [
    GetPage(name: RouteName.locationScreen, page: () => LocationScreen()),
  ];
}
