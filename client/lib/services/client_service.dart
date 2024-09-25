// ignore_for_file: avoid_print

import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';

import '../models/delete_contact_message_model.dart';
import '../models/update_contact_message_model.dart';
import '../models/contact_list_message_model.dart';
import '../models/contact_model.dart';
import '../models/create_contact_message_model.dart';
import '../models/message_model.dart';

class ServerInstance {
  final String host;
  final int port;

  const ServerInstance({
    this.host = 'localhost',
    required this.port,
  });
}

class ClientService {
  
  ClientService._();

  static final ClientService instance = ClientService._();

  static const servers = <ServerInstance>[
    ServerInstance(port: 1024),
    ServerInstance(port: 1025),
    ServerInstance(port: 1026),
  ];

  Socket? _socket;

  final _streamController = StreamController<List<ContactModel>>();
  Stream<List<ContactModel>> get onContactsListChanged => _streamController.stream;

  // Try to connect to an server instance. Returns false if connection fails
  Future<bool> init(void Function() onDisconnected) async {
    for (var server in servers) {
      try {
        _socket = await Socket.connect(
          server.host,
          server.port,
          timeout: const Duration(seconds: 5),
        );
        
        _socket!.encoding = utf8;
        _socket!.listen(
          _receiveMessageFromRemoteServer,
          cancelOnError: true,
          onDone: () => onDisconnected(),
          onError: (e, s) => onDisconnected(),
        );

        return true;
      } catch (e) {
        print(e);
        disconnect();
      }
    }
    return false;
  }

  void createContact(ContactModel contact) {
    final messageModel = CreateContactMessageModel(
      contact: contact,
    );
    _sendMessageToRemoteServer(messageModel);
  }

  void deleteContact(ContactModel contact) {
    final messageModel = DeleteContactMessageModel(
      contact: contact,
    );
    _sendMessageToRemoteServer(messageModel);
  }

  void updateContact(ContactModel contact) {
    final messageModel = UpdateContactMessageModel(
      contact: contact,
    );
    _sendMessageToRemoteServer(messageModel);
  }

  void _receiveMessageFromRemoteServer(Uint8List rawData) {
    final json = utf8.decode(rawData).trim();
    for (var line in json.split('\n')) {
      try {
        final map = jsonDecode(line);
        if (MessageType.values[map['messageType'] as int] == MessageType.get) {
          _streamController.add(ContactListMessageModel.fromMap(map).contacts);
        }
      } catch (e) {
        print('Deserialization error: $e');
      }
    }
  }

  Future<void> _sendMessageToRemoteServer(MessageModel message) async {
    final json = message.toJson();
    print('Message sent: $json');
    await _socket!.flush();
    _socket!.writeln(json);
  }

  void disconnect() {
    _socket?.close();
    _socket = null;
  }
}
