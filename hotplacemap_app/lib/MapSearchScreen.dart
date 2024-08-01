import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'dart:async';
import 'package:shared_preferences/shared_preferences.dart';
import 'Facility.dart';

class MapSearchScreen extends StatefulWidget {
  const MapSearchScreen({Key? key}) : super(key: key);

  @override
  _MapSearchScreenState createState() => _MapSearchScreenState();
}

class _MapSearchScreenState extends State<MapSearchScreen> with WidgetsBindingObserver {
  final TextEditingController _searchController = TextEditingController();
  final List<Facility> _facilities = [];
  final Map<int, bool> _favoriteStatus = {};
  final Map<int, String> _estimatedPeople = {};
  GoogleMapController? _mapController;
  bool _isListViewVisible = false;
  Timer? _timer;
  bool isActive = false; // Default to false

  static const CameraPosition _initialPosition = CameraPosition(
    target: LatLng(37.558146, 127.000222),
    zoom: 18,
  );

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    SystemChrome.setSystemUIOverlayStyle(SystemUiOverlayStyle(
      statusBarColor: Color(0xFFEF6C00),
      statusBarBrightness: Brightness.dark,
    ));
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    SystemChrome.setSystemUIOverlayStyle(SystemUiOverlayStyle(
      statusBarColor: Colors.transparent,
      statusBarBrightness: Brightness.light,
    ));
    _timer?.cancel();
    super.dispose();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    final bool newIsActive = ModalRoute.of(context)?.isCurrent ?? false;
    if (isActive != newIsActive) {
      isActive = newIsActive;
      if (isActive) {
        _startPeriodicUpdate();
      } else {
        _stopPeriodicUpdate();
      }
    }
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed && isActive) {
      _startPeriodicUpdate();
    } else if (state == AppLifecycleState.paused || !isActive) {
      _stopPeriodicUpdate();
    }
  }

  void _startPeriodicUpdate() {
    _timer = Timer.periodic(Duration(seconds: 7), (timer) {
      if (mounted && isActive) {
        _updateEstimatedPeople();
      }
    });
  }

  void _stopPeriodicUpdate() {
    _timer?.cancel();
  }

  Future<void> _updateEstimatedPeople() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? token = prefs.getString('token');

    if (token != null) {
      for (var facility in _facilities) {
        final response = await http.get(
          Uri.parse('http://3.35.180.133/HotPlaceMap/devices/query?fid=${facility.facilityId}'),
          headers: {'Authorization': token},
        );

        if (response.statusCode == 200) {
          final responseBody = response.body.trim();
          if (responseBody.contains('y=')) {
            final parts = responseBody.split(',');
            final yValue = parts[0].split('=')[1];
            final measurementTime = DateTime.parse(parts[1].split('=')[1].trim());
            final currentTime = DateTime.now();
            final differenceInMinutes = currentTime.difference(measurementTime).inMinutes;

            if (mounted && isActive) {
              setState(() {
                _estimatedPeople[facility.facilityId] = '예상: $yValue명 (${differenceInMinutes}분 전)';
              });
            }
          }
        }
      }
    }
  }

  Future<void> _search() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? token = prefs.getString('token');

    if (token != null) {
      final response = await http.get(
        Uri.parse('http://3.35.180.133/HotPlaceMap/search?keyword=${Uri.encodeComponent(_searchController.text)}'),
        headers: {'Authorization': token},
      );

      if (response.statusCode == 200) {
        final List<dynamic> responseBody = json.decode(utf8.decode(response.bodyBytes));
        if (mounted && isActive) {
          setState(() {
            _facilities.clear();
            _facilities.addAll(responseBody.map((e) => Facility.fromJson(e)).toList());
            _isListViewVisible = true;
          });
        }
        _checkFavorites();
        _startPeriodicUpdate();
      } else {
        _showErrorDialog("Failed to load search results");
      }
    } else {
      _showErrorDialog("No token found. Please log in again.");
    }
  }

  Future<void> _checkFavorites() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? token = prefs.getString('token');

    if (token != null) {
      for (var facility in _facilities) {
        final response = await http.get(
          Uri.parse('http://3.35.180.133/HotPlaceMap/favorites/isFav?fid=${facility.facilityId}'),
          headers: {'Authorization': token},
        );

        if (response.statusCode == 200) {
          final bool isFav = response.body.trim() == 'true';
          if (mounted && isActive) {
            setState(() {
              _favoriteStatus[facility.facilityId] = isFav;
            });
          }
        }
      }
    } else {
      _showErrorDialog("No token found. Please log in again.");
    }
  }

  Future<void> _toggleFavorite(int facilityId) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? token = prefs.getString('token');

    if (token != null) {
      final response = await http.post(
        Uri.parse('http://3.35.180.133/HotPlaceMap/favorites/toggleFav?fid=$facilityId'),
        headers: {'Authorization': token},
      );

      if (response.statusCode == 200) {
        _showPopupMessage(response.body.trim());
      } else {
        _showPopupMessage("Error toggling favorite status");
      }
      await _checkFavorites();
    } else {
      _showErrorDialog("No token found. Please log in again.");
    }
  }

  void _showPopupMessage(String message) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text("Message"),
          content: Text(message),
          actions: <Widget>[
            TextButton(
              child: const Text("OK"),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }

  void _showErrorDialog(String message) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text("Error"),
          content: Text(message),
          actions: <Widget>[
            TextButton(
              child: const Text("OK"),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }

  void _onFacilitySelected(Facility facility) {
    _mapController?.animateCamera(
      CameraUpdate.newLatLng(LatLng(facility.latitude, facility.longitude)),
    );
    setState(() {
      _isListViewVisible = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;

    return SafeArea(
      child: WillPopScope(
        onWillPop: () async {
          if (_isListViewVisible) {
            setState(() {
              _isListViewVisible = false;
            });
            return false;
          }
          return true;
        },
        child: GestureDetector(
          onTap: () {
            if (_isListViewVisible) {
              setState(() {
                _isListViewVisible = false;
              });
            }
          },
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
                    style: TextStyle(fontSize: screenHeight * 0.02),
                    decoration: InputDecoration(
                      hintText: "주소, 건물, 장소 검색",
                      contentPadding: EdgeInsets.symmetric(vertical: screenHeight * 0.015),
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
                    onSubmitted: (value) {
                      _search();
                    },
                  ),
                ),
              ),
              Expanded(
                child: Stack(
                  children: [
                    GoogleMap(
                      mapType: MapType.normal,
                      initialCameraPosition: _initialPosition,
                      onMapCreated: (GoogleMapController controller) {
                        _mapController = controller;
                      },
                      onTap: (LatLng position) {
                        if (_isListViewVisible) {
                          setState(() {
                            _isListViewVisible = false;
                          });
                        }
                      },
                    ),
                    if (_isListViewVisible)
                      Align(
                        alignment: Alignment.bottomCenter,
                        child: DraggableScrollableSheet(
                          initialChildSize: 0.4,
                          minChildSize: 0.2,
                          maxChildSize: 0.8,
                          builder: (BuildContext context, ScrollController scrollController) {
                            return Container(
                              color: Colors.white,
                              child: ListView.builder(
                                controller: scrollController,
                                itemCount: _facilities.length,
                                itemBuilder: (context, index) {
                                  final facility = _facilities[index];
                                  final bool isFav = _favoriteStatus[facility.facilityId] ?? false;
                                  final String estimatedPeople = _estimatedPeople[facility.facilityId] ?? '데이터 로딩 중..';
                                  return ListTile(
                                    title: Text(facility.facilityName),
                                    trailing: IconButton(
                                      icon: Image.asset(
                                        isFav ? 'assets/icon_fav_on_2x.png' : 'assets/icon_fav_off_2x.png',
                                        width: screenHeight * 0.02,
                                        height: screenHeight * 0.02,
                                      ),
                                      onPressed: () async {
                                        await _toggleFavorite(facility.facilityId);
                                      },
                                    ),
                                    subtitle: Text(estimatedPeople),
                                    onTap: () => _onFacilitySelected(facility),
                                  );
                                },
                              ),
                            );
                          },
                        ),
                      ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

