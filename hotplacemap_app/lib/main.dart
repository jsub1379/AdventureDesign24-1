import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Login Page',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const LoginPage(),
    );
  }
}

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  @override
  _LoginPageState createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final _formKey = GlobalKey<FormState>();
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    // Get screen size
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
                const Text(
                  '핫플맵',
                  style: TextStyle(
                    fontSize: 32, // Adjust as needed
                    color: Colors.black87,
                  ),
                ),
                SizedBox(height: screenHeight * 0.03),
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
                          return 'Please enter your ID';
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
                          return 'Please enter your password';
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
                    onPressed: () {
                      if (_formKey.currentState?.validate() ?? false) {
                        // Process login
                      }
                    },
                    child: Text(
                      '로그인',
                      style: TextStyle(fontSize: screenHeight * 0.02),
                    ),
                  ),
                ),
                SizedBox(height: screenHeight * 0.02),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[
                    TextButton(
                      onPressed: () {
                        // Navigate to sign up
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
                        // Navigate to forgot password
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
