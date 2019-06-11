import 'package:flutter/material.dart';

class Configurator extends StatefulWidget {
  @override
  _ConfiguratorState createState() => _ConfiguratorState();
}

class _ConfiguratorState extends State<Configurator> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('DB Config'),
      ),
      body: Column(),
    );
  }
}
