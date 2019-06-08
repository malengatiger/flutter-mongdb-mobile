import 'package:flutter/material.dart';

class GitLink extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('MongoDB Mobile Plugin @ GitHub'),
        actions: <Widget>[
          IconButton(
            icon: Icon(Icons.refresh),
            onPressed: () {},
          ),
        ],
      ),
      body: Card(
        child: Stack(
          children: <Widget>[],
        ),
      ),
    );
  }
}
