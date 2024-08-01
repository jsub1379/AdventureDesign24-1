import 'package:flutter/material.dart';

import 'AccountScreen.dart';
import 'MapSearchScreen.dart';
import 'FavListScreen.dart';

class MainPage extends StatefulWidget {
  const MainPage({super.key});

  @override
  _MainPageState createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  int _selectedIndex = 0;
  final PageController _pageController = PageController();

  final List<Widget> _pages = [
    const MapSearchScreen(),
    const FavListScreen(),
    const AccountScreen(),
  ];


  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
    _pageController.jumpToPage(index);
  }

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;
    final dotSize = screenHeight * 0.003;
    final dotMargin = screenHeight * 0.001;
    final innerMargin = EdgeInsets.fromLTRB(
        screenWidth * 0.005,
        screenHeight * 0.00,
        screenWidth * 0.005,
        screenHeight * 0.001);
    return Scaffold(
      body: PageView(
        controller: _pageController,
        onPageChanged: (index) {
          setState(() {
            _selectedIndex = index;
          });
        },
        children: _pages,
      ),
      bottomNavigationBar: BottomNavigationBar(
        showSelectedLabels: false,
        showUnselectedLabels: false,
        items: [
          BottomNavigationBarItem(
            icon: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Image.asset(
                  'assets/navbar_map_deselected.png',
                  width: screenWidth * 0.06,
                  height: screenHeight * 0.04,
                  color: _selectedIndex == 0 ? const Color(0xFFEF6C00) : null,
                ),
                SizedBox(height: dotMargin),
                Container(
                  width: screenWidth * 0.03,
                  height: screenHeight * 0.004,
                  decoration: BoxDecoration(
                    color: _selectedIndex == 0 ? const Color(0xFFEF6C00) : Colors.transparent,
                    borderRadius: BorderRadius.circular(screenWidth * 0.02),
                  ),
                ),
              ],
            ),
            label: '검색',
          ),
          BottomNavigationBarItem(
            icon: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Image.asset(
                  'assets/navbar_fav_deselected.png',
                  width: screenWidth * 0.06,
                  height: screenHeight * 0.04,
                  color: _selectedIndex == 1 ? const Color(0xFFEF6C00) : null,
                ),
                SizedBox(height: dotMargin),
                Container(
                  width: screenWidth * 0.03,
                  height: screenHeight * 0.004,
                  decoration: BoxDecoration(
                    color: _selectedIndex == 1 ? const Color(0xFFEF6C00) : Colors.transparent,
                    borderRadius: BorderRadius.circular(screenWidth * 0.02),
                  ),
                ),
              ],
            ),
            label: '즐겨찾기',
          ),
          BottomNavigationBarItem(
            icon: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Image.asset(
                  'assets/navbar_account_deselected.png',
                  width: screenWidth * 0.06,
                  height: screenHeight * 0.04,
                  color: _selectedIndex == 2 ? const Color(0xFFEF6C00) : null,
                ),
                SizedBox(height: dotMargin),
                Container(
                  width: screenWidth * 0.03,
                  height: screenHeight * 0.004,
                  decoration: BoxDecoration(
                    color: _selectedIndex == 2 ? const Color(0xFFEF6C00) : Colors.transparent,
                    borderRadius: BorderRadius.circular(screenWidth * 0.02),
                  ),
                ),
              ],
            ),
            label: '개인정보',
          ),
        ],
        currentIndex: _selectedIndex,
        onTap: _onItemTapped,
        selectedItemColor: Colors.deepOrange,
      ),


    );
  }
}
