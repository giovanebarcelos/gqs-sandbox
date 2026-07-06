const express = require('express');
const bodyParser = require('body-parser');
const jwt = require('jsonwebtoken');

const app = express();
const PORT = process.env.PORT || 3000;
const secretKey = 'suaChaveSecretaJWT'; // Chave secreta para assinar os tokens JWT

app.use(bodyParser.json());

// Simulando um banco de dados em memória
let users = [
    { id: 1, nome: 'Usuário Teste', email: 'usuario@teste.com', senha: 'senha123' }
];

// Middleware para autenticação
function authenticate(req, res, next) {
    const token = req.headers.authorization;
    if (!token) {
        return res.status(401).json({ error: 'Token de autenticação não fornecido' });
    }

    jwt.verify(token, secretKey, (err, decoded) => {
        if (err) {
            return res.status(401).json({ error: 'Token de autenticação inválido' });
        }
        req.user = decoded;
        next();
    });
}

// Rota para autenticar o usuário e gerar um token JWT
app.post('/login', (req, res) => {
    const { email, senha } = req.body;
    const user = users.find(u => u.email === email && u.senha === senha);
    if (!user) {
        return res.status(401).json({ error: 'Credenciais inválidas' });
    }
    const token = jwt.sign({ id: user.id, email: user.email }, secretKey);
    res.json({ token });
});

// Rotas protegidas por autenticação JWT
app.use(authenticate);

// Rotas CRUD de usuários
app.get('/users', (req, res) => {
    res.json(users);
});

app.get('/users/:id', (req, res) => {
    const userId = parseInt(req.params.id);
    const user = users.find(u => u.id === userId);
    if (!user) {
        return res.status(404).json({ error: 'Usuário não encontrado' });
    }
    res.json(user);
});

app.post('/users', (req, res) => {
    if (!req.user) {
        return res.status(401).json({ error: 'Você precisa estar autenticado para criar um usuário' });
    }
    const newUser = req.body;
    users.push(newUser);
    res.status(201).json(newUser);
});

app.put('/users/:id', (req, res) => {
    const userId = parseInt(req.params.id);
    const updateUser = req.body;
    const index = users.findIndex(u => u.id === userId);
    if (index === -1) {
        return res.status(404).json({ error: 'Usuário não encontrado' });
    }
    users[index] = { ...users[index], ...updateUser };
    res.json(users[index]);
});

app.delete('/users/:id', (req, res) => {
    if (!req.user) {
        return res.status(401).json({ error: 'Você precisa estar autenticado para excluir um usuário' });
    }
    const userId = parseInt(req.params.id);
    const index = users.findIndex(u => u.id === userId);
    if (index === -1) {
        return res.status(404).json({ error: 'Usuário não encontrado' });
    }
    users.splice(index, 1);
    res.sendStatus(204);
});

app.listen(PORT, () => {
    console.log(`Servidor rodando na porta ${PORT}`);
});
