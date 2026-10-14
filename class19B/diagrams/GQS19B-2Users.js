const express = require('express');
const bodyParser = require('body-parser');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');

const app = express();
app.use(bodyParser.json());

// Usuários em memória
let users = [];

// Função para gerar token JWT
function generateToken(username) {
    return jwt.sign({ username }, 'secret_key', { expiresIn: '1h' });
}

// Middleware para autenticar o token JWT
function authenticateToken(req, res, next) {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];
    if (!token) return res.sendStatus(401);

    jwt.verify(token, 'secret_key', (err, user) => {
        if (err) return res.sendStatus(403);
        req.user = user;
        next();
    });
}

// Rota de registro de usuário
app.post('/register', async (req, res) => {
    try {
        const { username, password } = req.body;
        const hashedPassword = await bcrypt.hash(password, 10);
        users.push({ username, password: hashedPassword });
        res.status(201).json({ message: 'Usuário registrado com sucesso.' });
    } catch (err) {
        res.status(500).json({ error: 'Erro ao registrar o usuário.' });
    }
});

// Rota de login de usuário
app.post('/login', async (req, res) => {
    try {
        const { username, password } = req.body;
        const user = users.find(user => user.username === username);
        if (user && await bcrypt.compare(password, user.password)) {
            const token = generateToken(username);
            res.json({ token });
        } else {
            res.status(401).json({ error: 'Credenciais inválidas.' });
        }
    } catch (err) {
        res.status(500).json({ error: 'Erro ao fazer login.' });
    }
});

// Rota protegida para listar todos os usuários
app.get('/users', authenticateToken, (req, res) => {
    res.json(users.map(user => ({ username: user.username })));
});

// Rota protegida para obter informações do usuário autenticado
app.get('/profile', authenticateToken, (req, res) => {
    res.json({ username: req.user.username });
});

// Rota protegida para atualizar a senha do usuário autenticado
app.put('/profile', authenticateToken, async (req, res) => {
    try {
        const { newPassword } = req.body;
        const index = users.findIndex(user => user.username === req.user.username);
        users[index].password = await bcrypt.hash(newPassword, 10);
        res.json({ message: 'Senha atualizada com sucesso.' });
    } catch (err) {
        res.status(500).json({ error: 'Erro ao atualizar a senha.' });
    }
});

// Rota protegida para excluir o usuário autenticado
app.delete('/profile', authenticateToken, (req, res) => {
    const index = users.findIndex(user => user.username === req.user.username);
    users.splice(index, 1);
    res.json({ message: 'Usuário excluído com sucesso.' });
});

const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Servidor rodando na porta ${PORT}`);
});
