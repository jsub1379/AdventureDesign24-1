import 'package:flutter/material.dart';

class MainPage extends StatefulWidget {
  const MainPage({super.key});

  @override
  _MainPageState createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  int _selectedIndex = 0;
  final PageController _pageController = PageController();

  final List<Widget> _pages = [
    const Center(child: Text('Screen 1')),
    const Center(child: Text('Screen 2')),
    const Center(child: Text('Screen 3')),
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
        items: [
          BottomNavigationBarItem(
            icon: Container(
              padding: EdgeInsets.fromLTRB(
                  screenWidth * 0.005,
                  screenHeight * 0.001,
                  screenWidth * 0.005,
                  screenHeight * 0.001
              ),
              margin: EdgeInsets.zero,
              decoration: BoxDecoration(
                color: _selectedIndex == 0 ? Colors.orange[100] : Colors.transparent,
                borderRadius: BorderRadius.circular(8),
              ),
              child: Image.asset(
                'assets/icon_mapsearch_2x.png',
                width: screenWidth * 0.06,
                height: screenHeight * 0.04,
                //color: _selectedIndex == 0 ? Colors.deepOrange : null,
              ),
            ),
            label: '검색',
          ),
          BottomNavigationBarItem(
            icon: Container(
              padding: EdgeInsets.fromLTRB(
                  screenWidth * 0.005,
                  screenHeight * 0.001,
                  screenWidth * 0.005,
                  screenHeight * 0.001
              ),
              margin: EdgeInsets.zero,
              decoration: BoxDecoration(
                color: _selectedIndex == 1 ? Colors.orange[100] : Colors.transparent,
                borderRadius: BorderRadius.circular(8),
              ),
              child: Image.asset(
                'assets/icon_fav_on_2x.png',
                width: screenWidth * 0.06,
                height: screenHeight * 0.04,
                //color: _selectedIndex == 1 ? Colors.deepOrange : null,
              ),
            ),
            label: '즐겨찾기',
          ),
          BottomNavigationBarItem(
            icon: Container(
              padding: EdgeInsets.fromLTRB(
                  screenWidth * 0.005,
                  screenHeight * 0.001,
                  screenWidth * 0.005,
                  screenHeight * 0.001
              ),
              margin: EdgeInsets.zero,
              decoration: BoxDecoration(
                color: _selectedIndex == 2 ? Colors.orange[100] : Colors.transparent,
                borderRadius: BorderRadius.circular(8),
              ),
              child: Image.asset(
                'assets/icon_user_1x.png',
                width: screenWidth * 0.06,
                height: screenHeight * 0.04,
                //color: _selectedIndex == 2 ? Colors.deepOrange : null,
              ),
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
