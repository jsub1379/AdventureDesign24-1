import 'package:flutter/material.dart';

class AccountScreen extends StatelessWidget {
  const AccountScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;

    return Scaffold(
      appBar: AppBar(
        title: const Text("Account"),
        backgroundColor: Color(0xFFEF6C00),
      ),
      body: ListView(
        children: [
          Padding(
            padding: EdgeInsets.all(screenHeight * 0.02),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Text(
                  '이정섭 님 환영합니다!',
                  style: TextStyle(fontSize: screenHeight * 0.03, fontWeight: FontWeight.bold),
                ),
                SizedBox(height: screenHeight * 0.02),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    ElevatedButton(
                      onPressed: () {
                        // Action for 정보수정
                      },
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.orangeAccent,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(20),
                        ),
                      ),
                      child: Text('정보수정', style: TextStyle(fontSize: screenHeight * 0.02)),
                    ),
                    SizedBox(width: screenWidth * 0.05),
                    ElevatedButton(
                      onPressed: () {
                        // Action for 로그아웃
                      },
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.orangeAccent,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(20),
                        ),
                      ),
                      child: Text('로그아웃', style: TextStyle(fontSize: screenHeight * 0.02)),
                    ),
                  ],
                ),
              ],
            ),
          ),
          Divider(color: Colors.grey, thickness: 1),
          ListTile(
            leading: Image.asset('assets/account_camera.png', width: screenHeight * 0.04, height: screenHeight * 0.04),
            title: Text('제보하기', style: TextStyle(fontSize: screenHeight * 0.02)),
            onTap: () {
              // Action for 제보하기
            },
          ),
          Divider(color: Colors.grey, thickness: 1),
          ListTile(
            leading: Image.asset('assets/account_boardlist.png', width: screenHeight * 0.04, height: screenHeight * 0.04),
            title: Text('내 제보', style: TextStyle(fontSize: screenHeight * 0.02)),
            onTap: () {
              // Action for 내 제보
            },
          ),
          Divider(color: Colors.grey, thickness: 1),
          ListTile(
            leading: Image.asset('assets/account_terms.png', width: screenHeight * 0.04, height: screenHeight * 0.04),
            title: Text('이용약관', style: TextStyle(fontSize: screenHeight * 0.02)),
            onTap: () {
              // Action for 이용약관
            },
          ),
          Divider(color: Colors.grey, thickness: 1),
        ],
      ),
    );
  }
}
