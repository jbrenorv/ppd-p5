import 'dart:async';

import 'package:flutter/material.dart';

import '../models/contact_model.dart';
import '../services/client_service.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  late final ClientService _service;
  late final StreamSubscription _streamSubscription;

  final _nameInput = TextEditingController();
  final _phoneInput = TextEditingController();

  bool _loading = false;
  bool _connected = false;
  List<ContactModel> _contacts = [];

  @override
  void initState() {
    _service = ClientService.instance;
    _streamSubscription = _service.onContactsListChanged.listen(_onContactsListChanged);
    super.initState();
  }

  @override
  void dispose() {
    _streamSubscription.cancel();
    _service.disconnect();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (_loading) {
      return const Scaffold(
        body: Center(
          child: Column(
            children: [
              Text('Conectando ao servidor'),
              SizedBox(height: 32.0),
              CircularProgressIndicator()
            ],
          ),
        ),
      );
    }

    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            _buildServerStatus(),
            const SizedBox(height: 32.0),
            _buildCreateContactForm(),
            const SizedBox(height: 32.0),
            Expanded(child: _buildContactsList()),
          ],
        ),
      ),
    );
  }

  Widget _buildServerStatus() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        if (_connected) ...[
          const Icon(
            Icons.signal_cellular_4_bar,
            color: Colors.green,
          ),
          const SizedBox(width: 16.0),
          const Text('Conectado ao servidor'),
        ]
        else ...[
          const Icon(
            Icons.signal_cellular_connected_no_internet_4_bar,
            color: Colors.red,
          ),
          const SizedBox(width: 16.0),
          const Text('Desconectado'),
          const SizedBox(width: 16.0),
          ElevatedButton(
            onPressed: !_connected ? _connectedToServer : null,
            child: const Text('Conectar'),
          ),
        ],
      ],
    );
  }

  Widget _buildCreateContactForm() {
    return SizedBox(
      height: 50.0,
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Expanded(
            child: TextField(
              controller: _nameInput,
              decoration: const InputDecoration(
                border: OutlineInputBorder(),
                hintText: 'Nome',
              ),
            ),
          ),
          const SizedBox(width: 16.0),
          Expanded(
            child: TextField(
              controller: _phoneInput,
              decoration: const InputDecoration(
                border: OutlineInputBorder(),
                hintText: 'Telefone',
              ),
            ),
          ),
          const SizedBox(width: 16.0),
          ElevatedButton(
            onPressed: _connected ? _createContact : null,
            child: const Text('Criar contato'),
          ),
        ],
      ),
    );
  }

  Widget _buildContactsList() {
    if (!_connected) {
      return const Center(
        child: Text(
          'Desconectado do servidor,\nconecte-se ao servidor para ver e criar contatos.',
          textAlign: TextAlign.center,
          style: TextStyle(
            fontSize: 32.0,
            color: Colors.grey,
          ),
        ),
      );
    }

    if (_contacts.isEmpty) {
      return const Center(
        child: Text(
          'Não há nenhum contato cadastrado,\nuse o formulário acima para cadastrar contatos.',
          textAlign: TextAlign.center,
          style: TextStyle(
            fontSize: 32.0,
            color: Colors.grey,
          ),
        ),
      );
    }

    return ListView.separated(
      itemCount: _contacts.length,
      separatorBuilder: (c, i) => const Divider(),
      itemBuilder: (c, i) {
        return ListTile(
          title: Text(_contacts[i].name),
          subtitle: Text(_contacts[i].phone),
          trailing: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              IconButton(
                onPressed: () => _deleteContact(_contacts[i]),
                icon: const Icon(Icons.delete, color: Colors.red),
              ),
              IconButton(
                onPressed: () => _editContact(_contacts[i]),
                icon: const Icon(Icons.edit),
              ),
            ],
          ),
        );
      },
    );
  }

  void _createContact() {
    if (!_validateInputs(_nameInput.text, _phoneInput.text)) {
      return;
    }
    _service.createContact(
      ContactModel(
        id: -1,
        name: _nameInput.text,
        phone: _phoneInput.text,
      ),
    );
    _nameInput.clear();
    _phoneInput.clear();
  }

  void _deleteContact(ContactModel contact) {
    _service.deleteContact(contact);
  }

  void _editContact(ContactModel contact) async {
    final edtited = await _getUpdatedContact(contact);
    if (edtited != null && _validateInputs(edtited.name, edtited.phone)) {
      _service.updateContact(edtited);
    }
  }

  Future<ContactModel?> _getUpdatedContact(ContactModel contact) async {
    return await showDialog<ContactModel>(
      context: context,
      builder: (c) {
        final nCtrl = TextEditingController(text: contact.name);
        final pCtrl = TextEditingController(text: contact.phone);
        return AlertDialog(
          title: const Text('Editando contato'),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.of(c).pop();
              },
              child: const Text('Cancelar'),
            ),
            TextButton(
              onPressed: () {
                Navigator.of(c).pop(
                  ContactModel(
                    id: contact.id,
                    name: nCtrl.text,
                    phone: pCtrl.text,
                  ));
              },
              child: const Text('Ok'),
            ),
          ],
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: nCtrl,
                decoration: const InputDecoration(
                  border: OutlineInputBorder(),
                  hintText: 'Nome',
                ),
              ),
              const SizedBox(height: 16.0),
              TextField(
                controller: pCtrl,
                decoration: const InputDecoration(
                  border: OutlineInputBorder(),
                  hintText: 'Telefone',
                ),
              ),
            ],
          ),
        );
      },
    );
  }

  void _connectedToServer() async {
    setState(() => _loading = true);
    
    final connected = await _service.init(_onDisconnected);
    
    setState(() {
      _loading = false;
      _connected = connected;
    });
  }

  void _onContactsListChanged(List<ContactModel> contacts) {
    setState(() {
      _contacts = contacts;
      _contacts.sort((a, b) => a.name.compareTo(b.name));
    });
  }

  void _onDisconnected() {
    setState(() {
      _connected = false;
      _contacts = [];
    });
  }

  bool _validateInputs(String name, String phone) {
    String? message;
    if (name.isEmpty || phone.isEmpty) {
      message = 'Preencha todos os campos';
    }
    if (_contacts.any((c) => c.name == name)) {
      message = 'Já existe um contato com esse nome';
    }
    if (message != null) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          backgroundColor: const Color.fromARGB(255, 109, 28, 23),
          content: Text(
            message,
            style: const TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
      );
      return false;
    }
    return true;
  }
}
