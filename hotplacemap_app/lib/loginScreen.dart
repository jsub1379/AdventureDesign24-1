import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import 'mainPage.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  @override
  _LoginPageState createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final _formKey = GlobalKey<FormState>();
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();

  Future<void> _login() async {
    if (_formKey.currentState?.validate() ?? false) {
      final response = await http.post(
        Uri.parse('http://3.35.180.133/HotPlaceMap/login'),
        body: {
          'username': _emailController.text,
          'password': _passwordController.text,
        },
      );

      if (response.statusCode == 200) {
        final responseBody = json.decode(response.body);
        String token = responseBody['Authorization'];

        SharedPreferences prefs = await SharedPreferences.getInstance();
        prefs.setString('username', _emailController.text);
        prefs.setString('password', _passwordController.text);
        prefs.setString('token', token);

        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (context) => const MainPage()),
        );
      } else {
        _showErrorDialog("Invalid username or password");
      }
    }
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

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;

    return Scaffold(
      body: Center(
        child: Padding(
          padding: EdgeInsets.symmetric(horizontal: screenWidth * 0.1),
          child: Form(
            key: _formKey,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                SizedBox(height: screenHeight * 0.03),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Image.asset(
                      'assets/icon_main_4x.png',
                      width: screenWidth * 0.2,
                      height: screenHeight * 0.2,
                    ),
                    Image.asset(
                      'assets/logo_4x.png',
                      width: screenWidth * 0.6,
                      height: screenHeight * 0.2,
                    ),
                  ],
                ),
                SizedBox(height: screenHeight * 0.015),
                Stack(
                  clipBehavior: Clip.none,
                  children: [
                    TextFormField(
                      controller: _emailController,
                      decoration: InputDecoration(
                        hintText: 'ID',
                        hintStyle: TextStyle(fontSize: screenHeight * 0.02),
                        filled: true,
                        fillColor: Colors.grey[200],
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(25),
                          borderSide: BorderSide.none,
                        ),
                        contentPadding: EdgeInsets.symmetric(
                          vertical: screenHeight * 0.025,
                          horizontal: screenWidth * 0.05,
                        ),
                      ),
                      style: TextStyle(fontSize: screenHeight * 0.02),
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return '아이디를 입력해주세요!';
                        }
                        return null;
                      },
                    ),
                    Positioned(
                      left: screenWidth * 0.05,
                      top: -screenHeight * 0.015,
                      child: Container(
                        padding: EdgeInsets.symmetric(horizontal: screenWidth * 0.02),
                        color: Colors.transparent,
                        child: Text(
                          '아이디',
                          style: TextStyle(color: Colors.black, fontSize: screenHeight * 0.017),
                        ),
                      ),
                    ),
                  ],
                ),
                SizedBox(height: screenHeight * 0.02),
                Stack(
                  clipBehavior: Clip.none,
                  children: [
                    TextFormField(
                      controller: _passwordController,
                      obscureText: true,
                      decoration: InputDecoration(
                        hintText: 'PW',
                        hintStyle: TextStyle(fontSize: screenHeight * 0.02),
                        filled: true,
                        fillColor: Colors.grey[200],
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(25),
                          borderSide: BorderSide.none,
                        ),
                        contentPadding: EdgeInsets.symmetric(
                          vertical: screenHeight * 0.025,
                          horizontal: screenWidth * 0.05,
                        ),
                      ),
                      style: TextStyle(fontSize: screenHeight * 0.02),
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return '비밀번호를 입력해주세요!';
                        }
                        return null;
                      },
                    ),
                    Positioned(
                      left: screenWidth * 0.05,
                      top: -screenHeight * 0.015,
                      child: Container(
                        padding: EdgeInsets.symmetric(horizontal: screenWidth * 0.02),
                        color: Colors.transparent,
                        child: Text(
                          '비밀번호',
                          style: TextStyle(color: Colors.black, fontSize: screenHeight * 0.017),
                        ),
                      ),
                    ),
                  ],
                ),
                SizedBox(height: screenHeight * 0.02),
                SizedBox(
                  width: screenWidth * 0.8,
                  height: screenHeight * 0.08,
                  child: ElevatedButton(
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.deepOrange,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(25),
                      ),
                    ),
                    onPressed: _login,
                    child: Text(
                      '로그인',
                      style: TextStyle(fontSize: screenHeight * 0.02, color: Colors.grey[200]),
                    ),
                  ),
                ),
                SizedBox(height: screenHeight * 0.02),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[
                    TextButton(
                      onPressed: () {
                        // TODO: Navigate to sign up
                      },
                      child: Text(
                        '회원가입',
                        style: TextStyle(color: Colors.black87, fontSize: screenHeight * 0.02),
                      ),
                    ),
                    Text(
                      '   |   ',
                      style: TextStyle(color: Colors.black87, fontSize: screenHeight * 0.02),
                    ),
                    TextButton(
                      onPressed: () {
                        // TODO: Navigate to forgot password
                      },
                      child: Text(
                        '비밀번호 찾기',
                        style: TextStyle(color: Colors.black87, fontSize: screenHeight * 0.02),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
