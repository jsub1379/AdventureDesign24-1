import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:flutter/services.dart';

class MapSearchScreen extends StatefulWidget {
  const MapSearchScreen({Key? key}) : super(key: key);

  @override
  _MapSearchScreenState createState() => _MapSearchScreenState();
}

class _MapSearchScreenState extends State<MapSearchScreen> {
  final TextEditingController _searchController = TextEditingController();

  static const CameraPosition _initialPosition = CameraPosition(
    target: LatLng(37.558146, 127.000222),
    zoom: 18,
  );

  @override
  void initState() {
    super.initState();
    SystemChrome.setSystemUIOverlayStyle(SystemUiOverlayStyle(
        statusBarColor: Color(0xFFEF6C00), // Set any color you need here
        statusBarBrightness: Brightness.dark // Dark text for status bar
    ));
  }

  @override
  void dispose() {
    SystemChrome.setSystemUIOverlayStyle(SystemUiOverlayStyle(
        statusBarColor: Colors.transparent, // Optional: Reset to default
        statusBarBrightness: Brightness.light // Optional: Reset to default
    ));
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;
    return SafeArea(
      child: Column(
        children: [
          Container(
            color: Color(0xFFEF6C00),
            padding: EdgeInsets.fromLTRB(screenWidth * 0.04, screenHeight * 0.03, screenWidth * 0.04, screenHeight * 0.01),
            child: Container(
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(screenHeight * 0.04),
              ),
              child: TextField(
                controller: _searchController,
                style:TextStyle(fontSize:screenHeight * 0.02),
                decoration: InputDecoration(
                  hintText: "주소, 건물, 장소 검색",
                  contentPadding: EdgeInsets.symmetric(vertical: screenHeight*0.015),
                  prefixIcon: Container(
                    width: screenHeight * 0.046,
                    height: screenHeight * 0.046,
                    margin: EdgeInsets.only(left: screenWidth * 0.03),
                    padding: EdgeInsets.all(screenHeight * 0.007),
                    child: Image.asset('assets/icon_search_2x.png'),
                  ),
                  border: InputBorder.none,
                  filled: true,
                  fillColor: Colors.transparent,
                ),
              ),
            ),
          ),
          Expanded(
            child: GoogleMap(
              mapType: MapType.normal,
              initialCameraPosition: _initialPosition,
            ),
          ),
        ],
      ),
    );
  }
}