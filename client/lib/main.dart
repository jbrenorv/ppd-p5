import 'package:flutter/material.dart';

import 'pages/home_page.dart';

void main() {
  runApp(const ClientApp());
}

class ClientApp extends StatelessWidget {
  const ClientApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      debugShowCheckedModeBanner: false,
      theme: ThemeData.dark(useMaterial3: false).copyWith(
        colorScheme: ColorScheme.fromSwatch(
          primarySwatch: Colors.brown,
          brightness: Brightness.dark,
          accentColor: Colors.amber,
          cardColor: Colors.brown,
        ),
      ),
      home: const HomePage(),
    );
  }
}
